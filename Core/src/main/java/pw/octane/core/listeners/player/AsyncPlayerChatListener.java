package pw.octane.core.listeners.player;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pw.octane.core.CoreModule;
import pw.octane.core.networking.CoreRedisAction;
import pw.octane.core.profiles.Profile;
import pw.octane.core.punishments.Punishment;
import pw.octane.core.ranks.Rank;
import pw.octane.manager.networking.redis.RedisMessage;
import pw.octane.manager.utils.Colors;

public class AsyncPlayerChatListener implements Listener {

    private CoreModule module;
    public AsyncPlayerChatListener(CoreModule module) {
        this.module = module;
        module.getManager().registerListener(this, module);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Profile profile = module.getProfileManager().get(player.getUniqueId());

        Rank rank = profile.getHighestRank();
        String prefix, color, tag;
        if(rank != null) {
            prefix = rank.getPrefix();
            color = rank.getColor();
        } else {
            prefix = null;
            color = "&f";
        }

        tag = null;
        if(profile.getAppliedTag() != null) {
            tag = profile.getAppliedTag().getTag();
        }

        String format = (prefix == null ? "" : prefix + " ") + color + player.getName() + (tag == null ? "" : " " + tag) + "&7: &r" + event.getMessage();
        event.setFormat(Colors.get(format));

        if(profile.getSettings().isStaffChat() && player.hasPermission("core.staff")) {
            event.setCancelled(true);
            JsonObject json = new JsonObject();
            json.addProperty("action", CoreRedisAction.STAFF_BROADCAST.toString());
            json.addProperty("message", "&7[Staff Chat] (" + module.getConfig().getString("general.server_name") + ") &r" + format);
            module.getManager().getRedisPublisher().getMessageQueue().add(new RedisMessage("core", json));
        } else {
            if(!profile.getSettings().isGlobalChat()) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot type in global chat because you have it disabled.");
                return;
            }

            if(profile.getActivePunishment(Punishment.Type.MUTE) != null) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot chat as you are muted.");
                return;
            }

            for (Player p : Bukkit.getOnlinePlayers()) {
                Profile pr = module.getProfileManager().get(p.getUniqueId());
                if (pr.getIgnored().contains(player.getUniqueId()) || !pr.getSettings().isGlobalChat()) {
                    event.getRecipients().remove(p);
                }
            }
        }
    }
}
