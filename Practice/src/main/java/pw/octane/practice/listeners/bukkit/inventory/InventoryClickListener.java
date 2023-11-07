package pw.octane.practice.listeners.bukkit.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.occupations.Occupation;
import pw.octane.practice.occupations.Participant;
import pw.octane.practice.profiles.Profile;

public class InventoryClickListener implements Listener {

    private PracticeModule module;
    public InventoryClickListener(PracticeModule module) {
        this.module = module;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Profile profile = module.getProfileManager().get(player.getUniqueId());

        switch(profile.getState()) {
            case KIT_EDITOR:
                break;
            case IN_GAME:
                Occupation occupation = profile.getOccupation();
                if(occupation != null) {
                    Participant participant = occupation.getAlive().get(player.getUniqueId());
                    if(participant != null && !participant.isKitApplied()) {
                        event.setCancelled(true);
                    }
                }
                break;
            default:
                if(!profile.getSettings().isBuildMode()) {
                    event.setCancelled(true);
                }
        }
    }
}
