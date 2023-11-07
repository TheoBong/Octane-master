package pw.octane.practice.listeners.bukkit.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.profiles.Profile;

public class PlayerItemConsumeListener implements Listener {

    private PracticeModule module;
    public PlayerItemConsumeListener(PracticeModule module) {
        this.module = module;
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        Profile profile = module.getProfileManager().get(player.getUniqueId());
        ItemStack item = event.getItem();

        if(item.getItemMeta().getDisplayName() != null && item.getItemMeta().getDisplayName().contains("Golden Head")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 2));
        }
    }
}
