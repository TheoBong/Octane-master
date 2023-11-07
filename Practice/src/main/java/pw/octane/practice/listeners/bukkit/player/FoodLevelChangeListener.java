package pw.octane.practice.listeners.bukkit.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.kits.Kit;
import pw.octane.practice.occupations.Occupation;
import pw.octane.practice.profiles.Profile;

public class FoodLevelChangeListener implements Listener {

    private PracticeModule module;
    public FoodLevelChangeListener(PracticeModule module) {
        this.module = module;
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Profile profile = module.getProfileManager().get(player.getUniqueId());
            Occupation occupation = profile.getOccupation();
            if(occupation != null) {
                if(occupation.getKit() != null && !occupation.getKit().isHunger()) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }
}
