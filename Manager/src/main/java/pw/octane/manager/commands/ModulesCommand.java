package pw.octane.manager.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import pw.octane.manager.MCommand;
import pw.octane.manager.Module;
import pw.octane.manager.OctaneManager;

import java.util.ArrayList;
import java.util.List;

public class ModulesCommand extends MCommand {
    private OctaneManager octaneManager;

    public ModulesCommand(OctaneManager octaneManager) {
        super(null, "modules");
        this.octaneManager = octaneManager;
        this.setDescription("See all loaded modules.");
        this.setUsage(ChatColor.RED + "Usage: /modules");
    }

    @Override
    public void execute(final CommandSender commandSender, final String[] args, String alias) {
        StringBuilder sb = new StringBuilder();
        List<Module> modules = new ArrayList<>(octaneManager.getLoadedModules());

        while (!modules.isEmpty()) {
            final Module module = modules.get(0);

            sb.append(ChatColor.WHITE + module.getModuleInformation().getName());
            modules.remove(module);

            if (modules.isEmpty()) {
                sb.append(ChatColor.GRAY + ".");
            } else {
                sb.append(ChatColor.GRAY + ", ");
            }
        }

        final int size = octaneManager.getLoadedModules().size();
        commandSender.sendMessage(ChatColor.GREEN + "There are currently " + size + " module(s) loaded.");
        if(size > 0) {
            commandSender.sendMessage(ChatColor.GREEN + "Modules: " + sb.toString());
        }
    }
}
