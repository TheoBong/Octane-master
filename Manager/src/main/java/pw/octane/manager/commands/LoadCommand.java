package pw.octane.manager.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import pw.octane.manager.MCommand;
import pw.octane.manager.OctaneManager;

public class LoadCommand extends MCommand {
    private OctaneManager octaneManager;

    public LoadCommand(OctaneManager octaneManager) {
        super(null, "load");
        this.octaneManager = octaneManager;
        this.setDescription("Load a module.");
        this.setUsage(ChatColor.RED + "Usage: /load <module>");
    }

    @Override
    public void execute(final CommandSender commandSender, final String[] args, String alias) {
        if (!commandSender.hasPermission("octanemanager.modules.load")) {
            commandSender.sendMessage(ChatColor.RED + "No permission.");
            return;
        }

        if (args.length != 1) {
            commandSender.sendMessage(usageMessage);
            return;
        }

        octaneManager.loadModule(args[0], commandSender);
    }
}
