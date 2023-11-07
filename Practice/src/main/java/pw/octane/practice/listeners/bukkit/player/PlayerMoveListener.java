package pw.octane.practice.listeners.bukkit.player;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.kits.Kit;
import pw.octane.practice.occupations.Occupation;
import pw.octane.practice.profiles.Profile;

public class PlayerMoveListener implements Listener {

    private PracticeModule module;
    public PlayerMoveListener(PracticeModule module) {
        this.module = module;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Profile profile = module.getProfileManager().get(player.getUniqueId());
        Occupation occupation = profile.getOccupation();

        Location from = event.getFrom();
        Location to = event.getTo();

        if(occupation != null) {
            if(event.getTo().getBlock().isLiquid() && occupation.getCurrentPlaying().contains(player)) {
                if (occupation.getKit() != null && occupation.getKit().getType().equals(Kit.Type.SUMO) && occupation.getState().equals(Occupation.State.ACTIVE)) {
                    occupation.eliminate(player);
                }
            }

            if(!occupation.isMoveOnStart()) {
                if(occupation.getState().equals(Occupation.State.STARTING) && occupation.getCurrentPlaying().contains(player)) {
                    if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) {
                        player.teleport(from);
                    }
                }
            }
        }
    }
}
