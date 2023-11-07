package pw.octane.practice.listeners.bukkit.player;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.occupations.Occupation;
import pw.octane.practice.profiles.Profile;
import pw.octane.practice.utils.EntityHider;

public class PlayerPickupItemListener implements Listener {

    private PracticeModule module;
    private EntityHider entityHider;
    public PlayerPickupItemListener(PracticeModule module) {
        this.module = module;
        this.entityHider = module.getEntityHider();
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        Profile profile = module.getProfileManager().get(player.getUniqueId());
        Occupation occupation = profile.getOccupation();
        Item item = event.getItem();

        if(occupation != null) {
            if(occupation.getCurrentPlaying().contains(player)) {
                if (!entityHider.canSee(player, item)) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        } else if(!profile.getSettings().isBuildMode()){
            event.setCancelled(true);
        }
    }
}
