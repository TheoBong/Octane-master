package pw.octane.core.commands.moderation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.octane.core.CoreModule;
import pw.octane.core.profiles.Profile;
import pw.octane.core.punishments.Punishment;
import pw.octane.core.web.WebPlayer;
import pw.octane.manager.MCommand;
import pw.octane.manager.Module;

import java.util.UUID;

public class PunishCommand extends MCommand {
    
    private CoreModule module;
    
    public PunishCommand(Module module, String name) {
        super(module, name);
        this.module = (CoreModule) module;
        this.setAliases("ban", "blacklist", "kick", "mute", "m");
    }

    @Override
    public void execute(CommandSender sender, String[] args, String alias) {
        String label = alias.toLowerCase();
        if(args.length > 1) {

            Player target = Bukkit.getPlayer(args[0]);
            Profile profile;
            if(target != null) {
                profile = module.getProfileManager().get(target.getUniqueId());
            } else {
                WebPlayer wp = new WebPlayer(args[0]);
                if(wp.isValid()) {
                    profile = module.getProfileManager().find(wp.getUuid(), false);
                } else {
                    sender.sendMessage(ChatColor.RED + "The target you specified does not exist.");
                    return;
                }
            }

            if (profile == null) {
                sender.sendMessage(ChatColor.RED + "The target you specified has never joined the server.");
                return;
            }
            
            Punishment.Type punishmentType;
            switch (label) {
                case "ban":
                    punishmentType = Punishment.Type.BAN;
                    break;
                case "blacklist":
                    punishmentType = Punishment.Type.BLACKLIST;
                    break;
                case "kick":
                    punishmentType = Punishment.Type.KICK;
                    break;
                case "mute":
                case "m":
                    punishmentType = Punishment.Type.MUTE;
                    break;
                default:
                    sender.sendMessage(ChatColor.RED + "Available commands: /ban, /blacklist, /kick, /mute.");
                    return;
            }

            UUID issuer = null;
            String issuerName = "&4Console";
            if(sender instanceof Player) {
                Player player = (Player) sender;
                Profile pr = module.getProfileManager().get(player.getUniqueId());
                issuerName = pr.getHighestRank().getColor() + player.getName();
                issuer = player.getUniqueId();
                if(!player.hasPermission("core.punish." + punishmentType.toString().toLowerCase())) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to " + punishmentType.toString() + ".");
                    return;
                } else if(pr.getHighestRank() != null && profile.getHighestRank() != null && (pr.getHighestRank().getWeight() < profile.getHighestRank().getWeight())) {
                    sender.sendMessage(ChatColor.RED + "You cannot punish someone who has a higher rank than you.");
                    return;
                }
            }
            
            Punishment punishment = profile.getActivePunishment(punishmentType);
            if(punishment == null || punishmentType.equals(Punishment.Type.KICK)) {
                StringBuilder sb = new StringBuilder();
                boolean silent = false;
                for(int i = 1; i < args.length; i++) {
                    String s = args[i];
                    if(s.equalsIgnoreCase("-s")) {
                        silent = true;
                    } else {
                        sb.append(args[i]);
                        if (i + 1 != args.length) {
                            sb.append(" ");
                        }
                    }
                }

                profile.punish(punishmentType, issuer, sb.toString(), null, silent);
            } else {
                sender.sendMessage(ChatColor.RED + "The target you specified already has an active punishment of that type.");
            }
            
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <target> <reason>");
        }
    }
}
