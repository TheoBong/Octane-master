package pw.octane.practice.listeners.bukkit.entity;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.occupations.Occupation;
import pw.octane.practice.profiles.Profile;

public class EntityDamageListener implements Listener {

    private PracticeModule module;
    public EntityDamageListener(PracticeModule module) {
        this.module = module;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Profile profile = module.getProfileManager().get(player.getUniqueId());
            if(profile.getState().equals(Profile.State.IN_GAME)) {
                Occupation occupation = profile.getOccupation();
                occupation.handleDamage(player, event);
            } else {
                event.setCancelled(true);
            }
        }
    }

}
