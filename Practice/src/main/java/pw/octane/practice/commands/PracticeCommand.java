package pw.octane.practice.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.octane.manager.MCommand;
import pw.octane.manager.Module;
import pw.octane.practice.PracticeModule;

public class PracticeCommand extends MCommand {

    private PracticeModule module;

    public PracticeCommand(Module module, String name) {
        super(module, name, "&a&lPractice Help &7(Page <page_number>)");
        this.module = (PracticeModule) module;
        this.getCommandHelper()
                .addEntry("&e/practice setlobby &7- &fSets the lobby location.")
                .addEntry("&e/practice refreshqueues &7- &fRefreshes all queues.");
    }

    @Override
    public void execute(CommandSender sender, String[] args, String alias) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(args.length > 0) {
                switch(args[0].toLowerCase()) {
                    case "setlobby":
                        final Location location = player.getLocation();
                        module.setLobby(location);
                        module.getConfig().set("locations.lobby", location);
                        player.sendMessage(ChatColor.GREEN + "Practice lobby has been set to your current location.");
                        break;
                    case "refreshqueues":
                        module.getQueueManager().refresh();
                        player.sendMessage(ChatColor.GREEN + "Refreshed all queues.");
                }
            } else {
                player.sendMessage(getCommandHelper().getMessage(1));
            }
        }
    }
}
