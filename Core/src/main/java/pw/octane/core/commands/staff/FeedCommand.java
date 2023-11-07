package pw.octane.core.commands.staff;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.octane.core.CoreModule;
import pw.octane.manager.MCommand;
import pw.octane.manager.Module;

public class FeedCommand extends MCommand {

    private CoreModule module;

    public FeedCommand(Module module, String name) {
        super(module, name);
        this.module = (CoreModule) module;
        this.setPermission("core.commands.feed");
    }

    @Override
    public void execute(CommandSender sender, String[] args, String alias) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            Player target;

            if(args.length > 0) {
                target = Bukkit.getPlayer(args[0]);
                if(target != null) {
                    player.sendMessage(ChatColor.GREEN + "You fed " + ChatColor.WHITE + target.getName() + ChatColor.GREEN + ".");
                } else {
                    player.sendMessage(ChatColor.RED + "The target you specified is not on this server.");
                    return;
                }
            } else {
                target = player;
            }

            target.setSaturation(20);
            target.setFoodLevel(20);
            target.sendMessage(ChatColor.GREEN + "You have been fed.");
        }
    }
}
