package pw.octane.manager.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import pw.octane.manager.MCommand;
import pw.octane.manager.Module;
import pw.octane.manager.ModuleInformation;
import pw.octane.manager.OctaneManager;

public class ModuleInfoCommand extends MCommand {
    private OctaneManager octaneManager;

    public ModuleInfoCommand(OctaneManager octaneManager) {
        super(null, "moduleinfo");
        this.octaneManager = octaneManager;
        this.setDescription("View a module's information.");
        this.setUsage(ChatColor.RED + "Usage: /moduleinfo <module>");
        this.setAliases("modinfo");
    }

    @Override
    public void execute(final CommandSender commandSender, final String[] args, String alias) {
        if (args.length == 0) {
            commandSender.sendMessage(usageMessage);
            return;
        }

        for (Module module : octaneManager.getLoadedModules()) {
            if (module.getModuleInformation().getName().equalsIgnoreCase(args[0])) {
                ModuleInformation mi = module.getModuleInformation();
                StringBuilder sb = new StringBuilder();
                sb.append(ChatColor.WHITE + mi.getName() + ChatColor.GREEN + " version " + ChatColor.WHITE + mi.getVersion());

                if (mi.getDescription() != null) {
                    sb.append("\n" + ChatColor.WHITE + mi.getDescription());
                }

                if (mi.getAuthor() != null) {
                    sb.append("\n" + ChatColor.GREEN + "Made by: " + ChatColor.WHITE + mi.getAuthor());
                }

                commandSender.sendMessage(sb.toString());
                return;
            }
        }

        commandSender.sendMessage(ChatColor.RED + "The module you specified was not found/loaded.");
    }
}