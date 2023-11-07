package pw.octane.core.ranks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mongodb.client.FindIterable;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.ChatColor;
import pw.octane.core.CoreModule;
import pw.octane.core.networking.CoreRedisAction;
import pw.octane.manager.networking.mongo.Mongo;
import pw.octane.manager.networking.mongo.MongoDeserializedResult;
import pw.octane.manager.networking.mongo.MongoIterableResult;
import pw.octane.manager.networking.mongo.MongoUpdate;
import pw.octane.manager.networking.redis.RedisMessage;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class RankManager {

    private CoreModule module;
    private @Getter Map<UUID, Rank> ranks;
    public RankManager (CoreModule module) {
        this.module = module;
        this.ranks = new HashMap<>();

        Mongo mongo = module.getManager().getMongo();
        mongo.createCollection(false, "core_ranks");
        mongo.getCollectionIterable(false, "core_ranks",
                iterable -> iterable.forEach(
                document ->
                pull(false, document.get("_id", UUID.class),
                obj -> {})));
    }

    public Rank getRank(UUID uuid) {
        return ranks.get(uuid);
    }

    public Rank getRank(String name) {
        for(Rank rank : ranks.values()) {
            if(rank.getName().equalsIgnoreCase(name)) {
                return rank;
            }
        }

        return null;
    }

    public Rank getDefaultRank() {
        for(Rank rank : ranks.values()) {
            if(rank.isDefaultRank()) {
                return rank;
            }
        }

        return null;
    }

    public Rank createRank(String name, int weight) {
        for(Rank rank : ranks.values()) {
            if(rank.getName().equalsIgnoreCase(name) || rank.getWeight() == weight) {
                return null;
            }
        }

        Rank rank = new Rank(UUID.randomUUID());
        rank.setName(ChatColor.stripColor(name.toLowerCase()));
        rank.setDisplayName(name);
        rank.setWeight(weight);
        ranks.put(rank.getUuid(), rank);
        push(true, rank);
        return rank;
    }

    public void pull(boolean async, UUID uuid, MongoDeserializedResult mdr) {
        module.getManager().getMongo().getDocument(async, "core_ranks", uuid, document -> {
            if(document != null) {
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                Rank rank = gson.fromJson(document.getString("elements"), Rank.class);
                ranks.put(rank.getUuid(), rank);
                mdr.call(rank);
            } else {
                mdr.call(null);
            }
        });

        module.getProfileManager().update();
    }

    public void push(boolean async, Rank rank) {
        MongoUpdate mu = new MongoUpdate("core_ranks", rank.getUuid());
        mu.put("elements", rank.serialize());
        module.getManager().getMongo().massUpdate(async, mu);

        JsonObject json = new JsonObject();
        json.addProperty("action", CoreRedisAction.RANK_UPDATE.toString());
        json.addProperty("fromServer", module.getConfig().getString("general.server_name"));
        json.addProperty("rank", rank.getUuid().toString());

        module.getManager().getRedisPublisher().getMessageQueue().add(new RedisMessage("core", json));

        module.getProfileManager().update();
    }

    public void remove(boolean async, Rank rank) {
        module.getManager().getMongo().deleteDocument(async, "core_ranks", rank.getUuid());

        JsonObject json = new JsonObject();
        json.addProperty("action", CoreRedisAction.RANK_DELETE.toString());
        json.addProperty("fromServer", module.getConfig().getString("general.server_name"));
        json.addProperty("rank", rank.getUuid().toString());

        module.getManager().getRedisPublisher().getMessageQueue().add(new RedisMessage("core", json));

        ranks.remove(rank.getUuid());

        module.getProfileManager().update();
    }
}
