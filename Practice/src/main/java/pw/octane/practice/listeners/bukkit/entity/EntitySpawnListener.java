package pw.octane.practice.listeners.bukkit.entity;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.occupations.Occupation;

public class EntitySpawnListener implements Listener {

    private PracticeModule module;
    public EntitySpawnListener(PracticeModule module) {
        this.module = module;
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if(event.getEntity() instanceof Item) {
            Item item = (Item) event.getEntity();
            Bukkit.getScheduler().runTaskLater(module.getPlugin(), () -> {
                for (Occupation occupation : module.getOccupationManager().getOccupations().values()) {
                    if(occupation.getEntities().contains(item)) {
                        if(item.getItemStack().getType().equals(Material.GLASS_BOTTLE) || item.getItemStack().getType().equals(Material.BOWL)) {
                            item.remove();
                        }

                        new BukkitRunnable() {
                            public void run() {
                                item.remove();
                            }
                        }.runTaskLater(module.getPlugin(), 500L);
                    }
                }
            }, 1);
        }
    }
}
