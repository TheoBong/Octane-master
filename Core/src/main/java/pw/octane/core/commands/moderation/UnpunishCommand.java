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

import java.util.Date;
import java.util.UUID;

public class UnpunishCommand extends MCommand {

    private CoreModule module;

    public UnpunishCommand(Module module, String name) {
        super(module, name);
        this.module = (CoreModule) module;
        this.setAliases("unban", "unblacklist", "unmute");
    }

    @Override
    public void execute(CommandSender sender, String[] args, String alias) {
        String label = alias.toLowerCase();
        if(args.length > 1) {
            Player target = Bukkit.getPlayer(args[0]);
            Profile profile = null;
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
                case "unban":
                    punishmentType = Punishment.Type.BAN;
                    break;
                case "unblacklist":
                    punishmentType = Punishment.Type.BLACKLIST;
                    break;
                case "unmute":
                    punishmentType = Punishment.Type.MUTE;
                    break;
                default:
                    sender.sendMessage(ChatColor.RED + "Available commands: /unban, /unblacklist, /unmute.");
                    return;
            }

            UUID pardoner = null;
            String pardonerName = "&4Console";
            if(sender instanceof Player) {
                Player player = (Player) sender;
                Profile pr = module.getProfileManager().get(player.getUniqueId());
                pardonerName = pr.getHighestRank().getColor() + player.getName();
                pardoner = player.getUniqueId();
                if(!player.hasPermission("core.unpunish." + punishmentType.toString().toLowerCase())) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to " + punishmentType.toString() + ".");
                    return;
                } else if(pr.getHighestRank() != null && profile.getHighestRank() != null && (pr.getHighestRank().getWeight() < profile.getHighestRank().getWeight())) {
                    sender.sendMessage(ChatColor.RED + "You cannot punish someone who has a higher rank than you.");
                    return;
                }
            }

            Punishment punishment = profile.getActivePunishment(punishmentType);
            if(punishment != null) {
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

                punishment.setPardoned(new Date());
                punishment.setPardoner(pardoner);
                punishment.setPardonReason(sb.toString());
                punishment.setSilentPardon(silent);
                punishment.execute();
                module.getPunishmentManager().push(true, punishment, false);
            } else {
                sender.sendMessage(ChatColor.RED + "The target you specified does not have an active punishment of that type.");
            }

        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <target> <reason>");
        }
    }
}
