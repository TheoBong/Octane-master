package pw.octane.core.listeners.world;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import pw.octane.core.CoreModule;

public class LeavesDecayListener implements Listener {

    private CoreModule module;
    public LeavesDecayListener(CoreModule module) {
        this.module = module;
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }
}
