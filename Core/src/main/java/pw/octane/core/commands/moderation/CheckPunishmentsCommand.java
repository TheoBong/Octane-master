package pw.octane.core.commands.moderation;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.octane.core.CoreModule;
import pw.octane.manager.MCommand;
import pw.octane.manager.Module;

public class CheckPunishmentsCommand extends MCommand {

    private CoreModule module;

    public CheckPunishmentsCommand(Module module, String name) {
        super(module, name);
        this.module = (CoreModule) module;
    }

    @Override
    public void execute(CommandSender sender, String[] args, String alias) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage(ChatColor.RED + "Coming soon.");
        }
    }
}
