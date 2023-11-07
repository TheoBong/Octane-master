package pw.octane.manager.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import pw.octane.manager.MCommand;
import pw.octane.manager.OctaneManager;

public class UnloadCommand extends MCommand {
    private OctaneManager octaneManager;

    public UnloadCommand(OctaneManager octaneManager) {
        super(null, "unload");
        this.octaneManager = octaneManager;
        this.setDescription("Unload a module.");
        this.setUsage(ChatColor.RED + "Usage: /unload <module>");
    }

    @Override
    public void execute(final CommandSender commandSender, final String[] args, String alias) {
        if (!commandSender.hasPermission("octanemanager.modules.unload")) {
            commandSender.sendMessage(ChatColor.RED + "You don't have permission to do this!");
            return;
        }

        if (args.length == 0) {
            commandSender.sendMessage(usageMessage);
            return;
        }

        octaneManager.unloadModule(args[0], commandSender);
    }
}
