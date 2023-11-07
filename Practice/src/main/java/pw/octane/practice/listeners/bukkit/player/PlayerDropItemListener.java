package pw.octane.practice.listeners.bukkit.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.scheduler.BukkitRunnable;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.occupations.Occupation;
import pw.octane.practice.profiles.Profile;
import pw.octane.practice.utils.EntityHider;

import java.util.Arrays;
import java.util.List;

public class PlayerDropItemListener implements Listener {

    private PracticeModule module;
    private EntityHider entityHider;
    public PlayerDropItemListener(PracticeModule module) {
        this.module = module;
        this.entityHider = module.getEntityHider();
    }

    private List<Material> items = Arrays.asList(
            Material.DIAMOND_SWORD,
            Material.IRON_SWORD,
            Material.STONE_SWORD,
            Material.GOLD_SWORD,
            Material.WOOD_SWORD
    );

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Profile profile = module.getProfileManager().get(player.getUniqueId());
        Occupation occupation = profile.getOccupation();

        if(occupation != null && occupation.getAlivePlayers().contains(player)) {

            Item item = event.getItemDrop();

            if (items.contains(item.getItemStack().getType())) {
                player.sendMessage(ChatColor.RED + "You cannot drop your sword.");
                event.setCancelled(true);
                return;
            }

            occupation.addEntity(item);

            if (item.getItemStack().getType().equals(Material.GLASS_BOTTLE) || item.getItemStack().getType().equals(Material.BOWL)) {
                item.remove();
            }

            new BukkitRunnable() {
                public void run() {
                    item.remove();
                }
            }.runTaskLater(module.getPlugin(), 400L);
        } else if(!profile.getSettings().isBuildMode()) {
            event.setCancelled(true);
        }
    }
}
