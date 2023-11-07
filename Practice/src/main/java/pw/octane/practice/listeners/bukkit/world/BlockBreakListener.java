package pw.octane.practice.listeners.bukkit.world;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.occupations.BrokenBlock;
import pw.octane.practice.occupations.Occupation;
import pw.octane.practice.profiles.Profile;

public class BlockBreakListener implements Listener {

    private PracticeModule module;
    public BlockBreakListener(PracticeModule module) {
        this.module = module;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();
        Profile profile = module.getProfileManager().get(player.getUniqueId());
        Block block = event.getBlock();
        Occupation occupation = profile.getOccupation();

        if(occupation != null) {
            if(occupation.isBuild()) {
                if (occupation.getPlacedBlocks().contains(block)) {
                    occupation.getPlacedBlocks().remove(block);
                    for (ItemStack item : block.getDrops()) {
                        Item i = block.getLocation().getWorld().dropItemNaturally(block.getLocation(), item);
                        occupation.addEntity(i);
                    }

                    block.getDrops().clear();
                } else {
                    player.sendMessage(ChatColor.RED + "You cannot break this block.");
                    event.setCancelled(true);
                }
            } else if(occupation.isSpleef()) {
                occupation.getBrokenBlocks().add(new BrokenBlock(block, block.getType(), block.getData()));
            }
        } else if(!profile.getSettings().isBuildMode()) {
            event.setCancelled(true);
        }
    }
}
