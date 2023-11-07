package pw.octane.core.commands.messaging;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.octane.core.CoreModule;
import pw.octane.core.profiles.Profile;
import pw.octane.manager.MCommand;
import pw.octane.manager.Module;

public class UnignoreCommand extends MCommand {

    private CoreModule module;

    public UnignoreCommand(Module module, String name) {
        super(module, name);
        this.module = (CoreModule) module;
    }

    @Override
    public void execute(CommandSender sender, String[] args, String alias) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(args.length > 0) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                if(offlinePlayer != null) {
                    Profile profile = module.getProfileManager().get(player.getUniqueId());
                    if(!profile.getIgnored().contains(offlinePlayer.getUniqueId())) {
                        sender.sendMessage(ChatColor.RED + "You are not ignoring this player.");
                    } else {
                        profile.getIgnored().remove(offlinePlayer.getUniqueId());
                        sender.sendMessage(ChatColor.GREEN + "You are no longer ignoring " + ChatColor.WHITE + offlinePlayer.getName() + ChatColor.GREEN + ".");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "The target you specified was not found.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /unignore <player>");
            }
        }
    }
}
