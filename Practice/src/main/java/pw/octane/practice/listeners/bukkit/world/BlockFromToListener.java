package pw.octane.practice.listeners.bukkit.world;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.occupations.Occupation;

public class BlockFromToListener implements Listener {

    private PracticeModule module;
    public BlockFromToListener(PracticeModule module) {
        this.module = module;
    }

    @EventHandler
    public void onBlockFormTo(BlockFromToEvent event) {
        Block from = event.getBlock();
        Block to = event.getToBlock();

        for(Occupation o : module.getOccupationManager().getOccupations().values()) {
            if(o.getPlacedBlocks().contains(from)) {
                o.getPlacedBlocks().add(to);
            }
        }
    }
}
