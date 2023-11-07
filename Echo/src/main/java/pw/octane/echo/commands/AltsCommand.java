package pw.octane.echo.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import pw.octane.echo.EchoModule;
import pw.octane.echo.classes.API;
import pw.octane.manager.MCommand;

public class AltsCommand extends MCommand {
    private EchoModule echoModule;

    public AltsCommand(EchoModule echoModule) {
        super(echoModule, "alts");
        this.echoModule = echoModule;
        this.setDescription("Check someone's alts.");
        this.setUsage(ChatColor.RED + "Usage: /alts <player>");
    }

    @Override
    public void execute(final CommandSender sender, final String[] args, String alias) {
        if (!sender.hasPermission("echo.alts")) {
            sender.sendMessage(ChatColor.RED + "No permission");
            return;
        }

        if (args.length != 1) {
            sender.sendMessage(usageMessage);
            return;
        }

        getAlts(args[0], sender);
    }

    private void getAlts(String p, CommandSender sender) {
        new Thread(() -> {
            sender.sendMessage("Searching Echo for aliases of " + net.md_5.bungee.api.ChatColor.AQUA + p + net.md_5.bungee.api.ChatColor.WHITE + "...");
            API api = new API();

            String final_alt_list = String.join(", ", api.getAlts(echoModule.getKey(), p, sender));;

            if (!final_alt_list.contains(", ")) {
                sender.sendMessage(net.md_5.bungee.api.ChatColor.RED + "Cannot find any aliases for this player.");
            } else {
                sender.sendMessage(final_alt_list);
            }
        }).start();
    }
}

