package pw.octane.practice.listeners.bukkit.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.occupations.Occupation;
import pw.octane.practice.profiles.Profile;

public class InventoryMoveItemListener implements Listener {

    private PracticeModule module;
    public InventoryMoveItemListener(PracticeModule module) {
        this.module = module;
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        Inventory source = event.getSource();
        Inventory destination = event.getDestination();

        if(source.getHolder() instanceof Player) {
            Player player = (Player) source.getHolder();
            Profile profile = module.getProfileManager().get(player.getUniqueId());
            Occupation occupation = profile.getOccupation();

            if(occupation != null) {
                 if(!occupation.getCurrentPlaying().contains(player)) {
                     event.setCancelled(true);
                 }
            } else if(!profile.getSettings().isBuildMode() || profile.getEditing() == null) {
                event.setCancelled(true);
            }
        }
    }
}
