package pw.octane.practice.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.octane.manager.MCommand;
import pw.octane.manager.Module;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.occupations.GameInventory;
import pw.octane.practice.occupations.Occupation;
import pw.octane.practice.occupations.Participant;

import java.util.UUID;

public class InventoryCommand extends MCommand {

    private PracticeModule module;

    public InventoryCommand(Module module, String name) {
        super(module, name);
        this.module = (PracticeModule) module;
    }

    @Override
    public void execute(CommandSender sender, String[] args, String alias) {
        if(sender instanceof Player && args.length > 0) {
            Player player = (Player) sender;
            try {
                UUID uuid = UUID.fromString(args[0]);
                for(Occupation occupation : module.getOccupationManager().getOccupations().values()) {
                    for(Participant participant : occupation.getParticipants().values()) {
                        GameInventory gi = participant.getGameInventory();
                        if(gi.getUuid().equals(uuid)) {
                            gi.open(player);
                            return;
                        }
                    }
                }

                player.sendMessage(ChatColor.RED + "Inventory not found.");
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Invalid inventory id.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /inventory <id>");
        }
    }
}
