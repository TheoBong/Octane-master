package pw.octane.core.commands.messaging;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.octane.core.CoreModule;
import pw.octane.manager.MCommand;
import pw.octane.manager.Module;

public class ChatCommand extends MCommand {

    private CoreModule module;

    public ChatCommand(Module module, String name) {
        super(module, name, "&a&lChat Help &7(Page <page_number>)");
        this.module = (CoreModule) module;
    }

    @Override
    public void execute(CommandSender sender, String[] args, String alias) {

    }
}
