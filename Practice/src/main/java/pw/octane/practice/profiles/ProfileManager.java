package pw.octane.practice.profiles;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import org.bukkit.entity.Player;
import pw.octane.manager.networking.mongo.Mongo;
import pw.octane.manager.networking.mongo.MongoDeserializedResult;
import pw.octane.manager.networking.mongo.MongoUpdate;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.cosmetics.ProfileCosmetics;
import pw.octane.practice.queues.PracticeQueue;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileManager {

    private PracticeModule module;
    private @Getter Map<UUID, Profile> profiles;
    public ProfileManager(PracticeModule module) {
        this.module = module;
        this.profiles = new HashMap<>();

        Mongo mongo = module.getManager().getMongo();
        mongo.createCollection(false, "practice_profiles");
    }

    public Profile createProfile(UUID uuid) {
        Profile profile = new Profile(uuid);
        profiles.put(profile.getUuid(), profile);

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
        module.getManager().getMongo().getDocument(async, "practice_profiles", uuid, document -> {
            if(document != null) {
                Profile profile = new Profile(uuid);
                profile.importFromDocument(document);

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
        MongoUpdate mu = new MongoUpdate("practice_profiles", profile.getUuid());
        mu.setUpdate(profile.export());
        module.getManager().getMongo().massUpdate(async, mu);

        if(unload) {
            profiles.remove(profile.getUuid());
        }
    }

    public void playerUpdateVisibility() {
        for(Profile profile : profiles.values()) {
            profile.playerUpdateVisibility();
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
