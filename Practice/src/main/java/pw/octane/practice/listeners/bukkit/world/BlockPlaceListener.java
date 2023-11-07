package pw.octane.practice.listeners.bukkit.world;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.arenas.Arena;
import pw.octane.practice.occupations.Occupation;
import pw.octane.practice.profiles.Profile;

public class BlockPlaceListener implements Listener {

    private PracticeModule module;
    public BlockPlaceListener(PracticeModule module) {
        this.module = module;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Profile profile = module.getProfileManager().get(player.getUniqueId());
        Block block = event.getBlock();
        Occupation occupation = profile.getOccupation();

        if(occupation != null && occupation.isBuild()) {
            Arena arena = occupation.getArena();
            if(block.getY() > arena.getBuildMax()) {
                player.sendMessage(ChatColor.RED + "You cannot place blocks above the build limit.");
                event.setCancelled(true);
            } else {
                occupation.getPlacedBlocks().add(block);
            }
        } else if(!profile.getSettings().isBuildMode()) {
            event.setCancelled(true);
        }
    }
}
