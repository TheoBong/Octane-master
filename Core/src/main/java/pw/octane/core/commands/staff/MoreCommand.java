package pw.octane.core.commands.staff;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pw.octane.core.CoreModule;
import pw.octane.manager.MCommand;
import pw.octane.manager.Module;

public class MoreCommand extends MCommand {

    private CoreModule module;

    public MoreCommand(Module module, String name) {
        super(module, name);
        this.module = (CoreModule) module;
        this.setPermission("core.commands.more");
    }

    @Override
    public void execute(CommandSender sender, String[] args, String alias) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack item = player.getItemInHand();
            if(item != null) {
                item.setAmount(64);
                player.sendMessage(ChatColor.GREEN + "There you go.");
            } else {
                player.sendMessage(ChatColor.RED + "Want more nothing? Idiot.");
            }
        }
    }
}
