package pw.octane.core.profiles;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.bukkit.entity.Player;
import pw.octane.core.CoreModule;
import pw.octane.core.networking.CoreRedisAction;
import pw.octane.core.ranks.Rank;
import pw.octane.manager.networking.mongo.MongoDeserializedResult;
import pw.octane.manager.networking.mongo.MongoUpdate;
import pw.octane.manager.networking.redis.RedisMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileManager {

    private CoreModule module;
    private @Getter Map<UUID, Profile> profiles;
    public ProfileManager(CoreModule module) {
        this.module = module;
        this.profiles = new HashMap<>();
    }

    public Profile createProfile(Player player) {
        Profile profile = new Profile(player);
        profiles.put(profile.getUuid(), profile);

        Rank rank = module.getRankManager().getDefaultRank();
        if(rank != null) {
            profile.addRank(rank.getUuid());
        } else {
            module.getPlugin().getLogger().warning("No default rank! Please create a default rank.");
        }

        push(true, profile, false);
        return profile;
    }

    public Profile createProfile(UUID uuid) {
        Profile profile = new Profile(uuid);
        profiles.put(profile.getUuid(), profile);

        Rank rank = module.getRankManager().getDefaultRank();
        if(rank != null) {
            profile.addRank(rank.getUuid());
        } else {
            module.getPlugin().getLogger().warning("No default rank! Please create a default rank.");
        }

        push(true, profile, false);
        return profile;
    }

    public Profile find(UUID uuid, boolean store) {
        final Profile[] profile = {profiles.get(uuid)};
        if(profile[0] == null) {
            pull(false, uuid, store, mdr -> {
                if(mdr instanceof Profile) {
                    profile[0] = (Profile) mdr;
                }
            });
        }

        return profile[0];
    }

    public Profile get(UUID uuid) {
        return profiles.get(uuid);
    }

    public void pull(boolean async, UUID uuid, boolean store, MongoDeserializedResult mdr) {
        module.getManager().getMongo().getDocument(async, "core_profiles", uuid, document -> {
            if(document != null) {
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                Profile profile = new Profile(uuid);
                profile.importFromDocument(document);

                for(UUID u : profile.getPunishments()) {
                    module.getPunishmentManager().pull(false, u, true, obj -> {});
                }

                mdr.call(profile);
                if(store) {
                    profiles.put(profile.getUuid(), profile);
                }
            } else {
                mdr.call(null);
            }
        });
    }

    public void push(boolean async, Profile profile, boolean unload) {
        MongoUpdate mu = new MongoUpdate("core_profiles", profile.getUuid());
        mu.setUpdate(profile.export());
        module.getManager().getMongo().massUpdate(async, mu);

        JsonObject json = new JsonObject();
        json.addProperty("action", CoreRedisAction.PROFILE_UPDATE.toString());
        json.addProperty("fromServer", module.getConfig().getString("general.server_name"));
        json.addProperty("uuid", profile.getUuid().toString());

        module.getManager().getRedisPublisher().getMessageQueue().add(new RedisMessage("core", json));

        if(unload) {
            profiles.remove(profile.getUuid());
        }
    }

    public void update() {
        for(Profile profile : profiles.values()) {
            profile.update();
        }
    }

    public void shutdown() {
        for(Profile profile : profiles.values()) {
            Player player = profile.getPlayer();
            if(player != null && player.isOnline()) {
                push(false, profile, true);
            }
        }
    }
}
