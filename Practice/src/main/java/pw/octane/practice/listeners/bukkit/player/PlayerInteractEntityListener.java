package pw.octane.practice.listeners.bukkit.player;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.profiles.Profile;

public class PlayerInteractEntityListener implements Listener {

    private PracticeModule module;
    public PlayerInteractEntityListener(PracticeModule module) {
        this.module = module;
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (entity instanceof Player) {
            Player target = (Player) entity;
            Profile profile = module.getProfileManager().get(player.getUniqueId());
            Profile targetProfile = module.getProfileManager().get(target.getUniqueId());
            if(player.getItemInHand() == null || (player.getItemInHand() != null && player.getItemInHand().getType().equals(Material.AIR))) {
                if(!profile.getState().equals(Profile.State.LOBBY) && !targetProfile.getState().equals(Profile.State.LOBBY)) {
                    player.performCommand("duel " + target.getName());
                }
            }
        }
    }
}
