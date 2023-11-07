package pw.octane.practice.listeners.bukkit.entity;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.occupations.Occupation;
import pw.octane.practice.profiles.Profile;

public class EntityRegainHealthListener implements Listener {

    private PracticeModule module;
    public EntityRegainHealthListener(PracticeModule module) {
        this.module = module;
    }

    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Profile profile = module.getProfileManager().get(player.getUniqueId());
            if(event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED) && profile.getOccupation() != null) {
                Occupation occupation = profile.getOccupation();
                if(occupation.getKit() != null && !occupation.getKit().isRegen()) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
