package pw.octane.manager.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import pw.octane.manager.MCommand;
import pw.octane.manager.Module;
import pw.octane.manager.OctaneManager;

public class CloseCommand extends MCommand {

    private OctaneManager octaneManager;

    public CloseCommand(OctaneManager octaneManager) {
        super(null, "close");
        this.octaneManager = octaneManager;
        this.setPermission("octanemanager.close");
        this.setDescription("Close/stop the server.");
    }

    @Override
    public void execute(CommandSender sender, String[] args, String alias) {
        octaneManager.onDisable();
        Bukkit.getServer().shutdown();
    }
}
