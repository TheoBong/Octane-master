package pw.octane.core.listeners.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import pw.octane.core.CoreModule;
import pw.octane.core.profiles.Profile;
import pw.octane.core.punishments.Punishment;

import java.util.UUID;

public class PlayerPreLoginListener implements Listener {

    private CoreModule module;
    public PlayerPreLoginListener(CoreModule module) {
        this.module = module;
        module.getManager().registerListener(this, module);
    }

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();

        Profile profile = module.getProfileManager().find(uuid, true);
        if(profile == null) {
            profile = module.getProfileManager().createProfile(uuid);
        }

        Punishment blacklist = profile.getActivePunishment(Punishment.Type.BLACKLIST);
        Punishment ban = profile.getActivePunishment(Punishment.Type.BAN);
        if(blacklist != null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    ChatColor.RED + "You are blacklisted.");
        }

        if(ban != null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    ChatColor.RED + "You are banned.");
        }

        profile.addIp(event.getAddress().getHostAddress());

        if(ban == null && blacklist == null) {
            event.allow();
        }
    }
}
