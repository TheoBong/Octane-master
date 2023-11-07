package pw.octane.core.commands.staff;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.octane.core.CoreModule;
import pw.octane.manager.MCommand;
import pw.octane.manager.Module;

public class SudoCommand extends MCommand {

    private CoreModule module;

    public SudoCommand(Module module, String name) {
        super(module, name);
        this.module = (CoreModule) module;
        this.setPermission("core.commands.sudo");
    }

    @Override
    public void execute(CommandSender sender, String[] args, String alias) {
        if(args.length > 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if(target != null) {
                StringBuilder sb = new StringBuilder();
                for(int i = 1; i < args.length; i++) {
                    sb.append(args[i]);
                    if(i + 1 != args.length) {
                        sb.append(" ");
                    }
                }

                String request = sb.toString();
                if(request.toLowerCase().startsWith("c:")) {
                    String chat = request.substring(2);
                    if(chat.isEmpty()) {
                        sender.sendMessage(ChatColor.RED + "You cannot send a blank message.");
                    } else {
                        sender.sendMessage(ChatColor.GREEN + "You made " + ChatColor.WHITE + target.getName() + ChatColor.GREEN + " say: " + ChatColor.WHITE + chat);
                        target.chat(chat);
                    }
                } else {
                    sender.sendMessage(ChatColor.GREEN + "You made " + ChatColor.WHITE + target.getName() + ChatColor.GREEN + " perform command: " + ChatColor.WHITE + request);
                    target.performCommand(request);
                }
            } else {
                sender.sendMessage(ChatColor.RED + "The target you specified is not on this server.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /sudo <player> <action>");
        }
    }
}
