package pw.octane.echo.commands;

import com.neovisionaries.ws.client.WebSocketException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.octane.echo.EchoModule;
import pw.octane.echo.classes.API;
import pw.octane.echo.classes.EchoClient;
import pw.octane.manager.MCommand;

import java.io.IOException;

public class PinCommand extends MCommand {
    private EchoModule echoModule;

    public PinCommand(EchoModule echoModule) {
        super(echoModule, "pin");
        this.echoModule = echoModule;
        this.setDescription("Get a pin.");
        this.setUsage(ChatColor.RED + "Usage: /pin");
    }

    @Override
    public void execute(final CommandSender sender, final String[] args, String alias) {
        if (!sender.hasPermission("echo.pin")) {
            sender.sendMessage(ChatColor.RED + "No permission");
            return;
        }

        getPin(sender);
    }

    private void getPin(CommandSender sender){
            sender.sendMessage("Getting a link...");
            API api = new API();

            String pin = api.getPin(echoModule.getKey(), sender);
            String link = api.getLink(echoModule.getKey(), sender);

            sender.sendMessage(link);
            sender.sendMessage(pin);

            try {
                EchoClient.connect(pin);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (WebSocketException e) {
                e.printStackTrace();
            }
    }


}

