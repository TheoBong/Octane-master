package pw.octane.core.commands.staff;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import pw.octane.core.CoreModule;
import pw.octane.manager.MCommand;
import pw.octane.manager.Module;
import pw.octane.manager.utils.Colors;

import java.util.Random;

public class ChatCommand extends MCommand {

    private CoreModule module;

    public ChatCommand(Module module, String name) {
        super(module, name);
        this.module = (CoreModule) module;
        this.setPermission("core.commands.chat");
        this.setAliases(
                "clearchat",
                "cc",
                "mutechat",
                "unmutechat");
    }

    @Override
    public void execute(CommandSender sender, String[] args, String alias) {
        switch(alias.toLowerCase()) {
            case "clearchat":
            case "cc":
                Random random = new Random();
                for(int i = 0; i < 1000; i++) {
                    int r = random.nextInt(200);
                    StringBuilder sb = new StringBuilder();
                    for(int a = 0; a < r; a++) {
                        sb.append("&a ");
                    }

                    Bukkit.broadcastMessage(Colors.get(sb.toString()));
                }
                break;
            case "mutechat":
                break;
            case "unmutechat":
                break;
        }
    }
}
