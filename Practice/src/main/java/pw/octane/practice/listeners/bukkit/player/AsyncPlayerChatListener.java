package pw.octane.practice.listeners.bukkit.player;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.kits.CustomKit;
import pw.octane.practice.profiles.Profile;

public class AsyncPlayerChatListener implements Listener {

    private PracticeModule module;
    public AsyncPlayerChatListener(PracticeModule module) {
        this.module = module;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Profile profile = module.getProfileManager().get(player.getUniqueId());

        CustomKit renaming = profile.getRenaming();
        if(renaming != null) {
            renaming.setName(event.getMessage());
            player.sendMessage(ChatColor.GREEN + "Kit has been renamed!");
            profile.setRenaming(null);
            event.setCancelled(true);
        }
    }
}
