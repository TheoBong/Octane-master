package pw.octane.practice.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import pw.octane.manager.MCommand;
import pw.octane.manager.Module;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.kits.Kit;
import pw.octane.practice.occupations.DuelRequest;
import pw.octane.practice.profiles.Profile;
import xyz.leuo.gooey.action.ButtonAction;
import xyz.leuo.gooey.button.Button;
import xyz.leuo.gooey.gui.GUI;

public class DuelCommand extends MCommand {

    private PracticeModule module;

    public DuelCommand(Module module, String name) {
        super(module, name);
        this.module = (PracticeModule) module;
    }

    @Override
    public void execute(CommandSender sender, String[] args, String alias) {
        if(sender instanceof Player && args.length > 0) {
            Player player = (Player) sender;
            Profile profile = module.getProfileManager().get(player.getUniqueId());
            Player target = Bukkit.getPlayer(args[0]);
            if(target != null) {
                if(target.getUniqueId().equals(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "All I'm going to say is, bruh.");
                    return;
                }

                Profile targetProfile = module.getProfileManager().get(target.getUniqueId());
                DuelRequest duelRequest = targetProfile.getDuelRequests().get(player.getUniqueId());

                if(targetProfile.getState().equals(Profile.State.LOBBY) && profile.getState().equals(Profile.State.LOBBY)) {
                    GUI kitGui = new GUI("Duel Request", 9);
                    for(Kit kit : module.getKitManager().getKits().values()) {
                        Button button = new Button(kit.getIcon(), kit.getDisplayName());
                        button.setButtonAction((player1, gui1, button1, event) -> {
                            DuelRequest dr = new DuelRequest(module, target.getUniqueId(), player.getUniqueId(), kit, null);
                            dr.send();
                            targetProfile.getDuelRequests().put(player.getUniqueId(), dr);
                        });
                        kitGui.setButton(kit.getUnrankedPosition(), button);
                    }

                    kitGui.open(player);
                } else {
                    player.sendMessage(ChatColor.RED + "You cannot duel this player right now.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "The target you specified is not on this server.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /duel <player>");
        }
    }
}
