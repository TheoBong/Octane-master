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
import pw.octane.manager.utils.Colors;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

public class GetRanksCommand extends MCommand {

    private CoreModule module;

    public GetRanksCommand(Module module, String name) {
        super(module, name);
        this.module = (CoreModule) module;
        this.setPermission("core.commands.getranks");
    }

    @Override
    public void execute(CommandSender sender, String[] args, String alias) {
        if(args.length > 0) {
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

            TreeMap<Integer, Rank> ranks = new TreeMap<>();
            for(Rank rank : profile.getAllRanks()) {
                ranks.put(rank.getWeight(), rank);
            }

            if(!ranks.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append("&aRanks &7(" + ranks.size() + "&7)&a: ");
                List<Rank> list = new LinkedList<>(ranks.descendingMap().values());
                while(!list.isEmpty()) {
                    final Rank rank = list.get(0);
                    list.remove(rank);
                    sb.append(rank.getColor() + rank.getName());
                    if(list.isEmpty()) {
                        sb.append("&7.");
                    } else {
                        sb.append("&7, ");
                    }
                }

                sender.sendMessage(Colors.get(sb.toString()));
            } else {
                sender.sendMessage(ChatColor.RED + "The target you specified does not have any ranks.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /getranks <target>");
        }
    }
}
