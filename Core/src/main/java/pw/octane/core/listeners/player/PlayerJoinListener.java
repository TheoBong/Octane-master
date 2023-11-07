package pw.octane.core.listeners.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pw.octane.core.CoreModule;
import pw.octane.core.networking.CoreRedisAction;
import pw.octane.core.profiles.CorePermissibleBase;
import pw.octane.core.profiles.PermissionInjector;
import pw.octane.core.profiles.Profile;
import pw.octane.manager.networking.redis.RedisMessage;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PlayerJoinListener implements Listener {

    private CoreModule module;
    public PlayerJoinListener(CoreModule module) {
        this.module = module;
        module.getManager().registerListener(this, module);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Profile profile = module.getProfileManager().get(player.getUniqueId());

        if(profile == null) {
            player.kickPlayer(ChatColor.RED + "Your profile did not load properly, please relog.");
            return;
        }

        profile.setName(player.getName());
        profile.addIp(player.getAddress().getAddress().getHostAddress());

        PermissionInjector.inject(player, new CorePermissibleBase(player));
        profile.update();

        if(player.hasPermission("core.staff")) {
            JsonObject json = new JsonObject();
            json.addProperty("action", CoreRedisAction.STAFF_BROADCAST.toString());
            json.addProperty("message", "&7[Staff] &f" + player.getName() + "&a joined server &f" + module.getConfig().getString("general.server_name") + "&a.");
            RedisMessage rm = new RedisMessage("core", json);
            module.getManager().getRedisPublisher().getMessageQueue().add(rm);
        }

        event.setJoinMessage(null);

        if(module.getConfig().getBoolean("general.namemc_check")) {
            try {
                InputStream input = new URL(module.getConfig().getString("general.namemc_api".replace("<uuid>", player.getUniqueId().toString()))).openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));

                StringBuilder sb = new StringBuilder();
                int cp;
                while ((cp = reader.read()) != -1) {
                    sb.append((char) cp);
                }

                profile.setNameMc(Boolean.parseBoolean(sb.toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
