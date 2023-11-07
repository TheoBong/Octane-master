package pw.octane.practice.listeners.bukkit.player;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.occupations.Occupation;
import pw.octane.practice.profiles.Profile;

public class PlayerBucketEmptyListener implements Listener {

    private PracticeModule module;
    public PlayerBucketEmptyListener(PracticeModule module) {
        this.module = module;
    }

    private BlockFace[] faces = {
            BlockFace.SELF,
            BlockFace.UP,
            BlockFace.DOWN,
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.SOUTH,
            BlockFace.WEST
    };

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        Profile profile = module.getProfileManager().get(player.getUniqueId());
        Block blockClicked = event.getBlockClicked();

        Bukkit.getScheduler().runTaskLater(module.getPlugin(), ()-> {
            Block block = null;
            for(BlockFace face : faces) {
                Block b = blockClicked.getRelative(face, 1);
                if(b.isLiquid()) {
                    block = b;
                    break;
                }
            }

            if(block != null) {
                Occupation occupation = profile.getOccupation();
                if (profile.getOccupation() != null) {
                    occupation.getPlacedBlocks().add(block);
                }
            }
        }, 1);
    }
}
