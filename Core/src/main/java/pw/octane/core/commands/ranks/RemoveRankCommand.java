package pw.octane.core.commands.ranks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.octane.core.CoreModule;
import pw.octane.core.profiles.Profile;
import pw.octane.core.ranks.Rank;
import pw.octane.core.web.WebPlayer;
import pw.octane.manager.MCommand;
import pw.octane.manager.Module;

public class RemoveRankCommand extends MCommand {

    private CoreModule module;

    public RemoveRankCommand(Module module, String name) {
        super(module, name);
        this.module = (CoreModule) module;
        this.setPermission("core.commands.removerank");
    }

    @Override
    public void execute(CommandSender sender, String[] args, String alias) {
        if(args.length > 1) {
            Rank rank = module.getRankManager().getRank(args[1]);
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

            if(rank == null) {
                sender.sendMessage(ChatColor.RED + "The rank you specified does not exist.");
                return;
            }

            if(profile.getRanks().contains(rank.getUuid())) {
                profile.removeRank(rank.getUuid());
                module.getProfileManager().push(true, profile, false);
                sender.sendMessage(ChatColor.WHITE + profile.getName() + ChatColor.GREEN + " no longer has the rank " + rank.getColor() + rank.getName() + ChatColor.GREEN + ".");
            } else {
                sender.sendMessage(ChatColor.RED + "The target you specified doesn't have that rank.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /removerank <target> <rank>");
        }
    }
}
