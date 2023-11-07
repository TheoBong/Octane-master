package pw.octane.core.listeners.player;

import com.google.gson.JsonObject;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pw.octane.core.CoreModule;
import pw.octane.core.networking.CoreRedisAction;
import pw.octane.core.profiles.Profile;
import pw.octane.manager.networking.redis.RedisMessage;

public class PlayerQuitListener implements Listener {

    private CoreModule module;
    public PlayerQuitListener(CoreModule module) {
        this.module = module;
        module.getManager().registerListener(this, module);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Profile profile = module.getProfileManager().get(player.getUniqueId());

        if(player.hasPermission("core.staff")) {
            JsonObject json = new JsonObject();
            json.addProperty("action", CoreRedisAction.STAFF_BROADCAST.toString());
            json.addProperty("message", "&7[Staff] &f" + player.getName() + "&a left server &f" + module.getConfig().getString("general.server_name") + "&a.");
            RedisMessage rm = new RedisMessage("core", json);
            module.getManager().getRedisPublisher().getMessageQueue().add(rm);
        }

        if(profile != null) {
            module.getProfileManager().push(true, profile, true);
        }
        event.setQuitMessage(null);
    }
}
