package pw.octane.core.networking;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pw.octane.core.CoreModule;
import pw.octane.core.profiles.Profile;
import pw.octane.manager.networking.redis.RedisMessage;
import pw.octane.manager.networking.redis.RedisMessageListener;
import pw.octane.manager.utils.Colors;

import java.util.UUID;

public class CoreRedisMessageListener implements RedisMessageListener {

    private CoreModule module;
    private String serverName;
    public CoreRedisMessageListener(CoreModule module) {
        this.module = module;
        module.getManager().getRedisSubscriber().getListeners().add(this);

        this.serverName = module.getConfig().getString("general.server_name");
    }

    @Override
    public void onReceive(RedisMessage redisMessage) {
        JsonObject json = redisMessage.getElements();
        if(redisMessage.getInternalChannel().equals("core")) {
            CoreRedisAction action = CoreRedisAction.valueOf(json.get("action").getAsString());
            String fromServer = json.get("fromServer") == null ? null : json.get("fromServer").getAsString();
            if(fromServer != null) {
                boolean thisServer = fromServer.equals(serverName);
                switch(action) {
                    case PROFILE_UPDATE:
                        if(!thisServer) {
                            UUID uuid = UUID.fromString(json.get("uuid").getAsString());
                            Player player = Bukkit.getPlayer(uuid);
                            if (player != null && player.isOnline()) {
                                module.getProfileManager().pull(true, uuid, true, obj -> {
                                });
                            } else {
                                Bukkit.getScheduler().runTaskLater(module.getPlugin(), () -> {
                                    Player p = Bukkit.getPlayer(uuid);
                                    if (p != null && p.isOnline()) {
                                        module.getProfileManager().pull(true, uuid, true, obj -> {
                                        });
                                    }
                                }, 10);
                            }
                        }
                        break;
                    case RANK_UPDATE:
                        if(!thisServer) {
                            module.getRankManager().pull(true, UUID.fromString(json.get("rank").getAsString()), obj -> { });
                        }
                        break;
                    case RANK_DELETE:
                        if(!thisServer) {
                            module.getRankManager().getRanks().remove(UUID.fromString(json.get("rank").getAsString()));
                        }
                        break;
                    case TAG_UPDATE:
                        if(!thisServer) {
                            module.getTagManager().pull(true, UUID.fromString(json.get("tag").getAsString()), obj -> {});
                        }
                        break;
                    case TAG_DELETE:
                        if(!thisServer) {
                            module.getTagManager().getTags().remove(UUID.fromString(json.get("tag").getAsString()));
                        }
                }
            } else {
                switch(action) {
                    case BROADCAST:
                        Bukkit.broadcastMessage(Colors.get(json.get("message").getAsString()));
                        break;
                    case STAFF_BROADCAST:
                        for(Profile profile : module.getProfileManager().getProfiles().values()) {
                            Player player = profile.getPlayer();
                            if(player != null && player.isOnline() && player.hasPermission("core.staff") && profile.getSettings().isStaffMessages()) {
                                player.sendMessage(Colors.get(json.get("message").getAsString()));
                            }
                        }
                        break;
                }
            }
        }
    }

    public void close() {
        module.getManager().getRedisSubscriber().getListeners().remove(this);
    }
}
