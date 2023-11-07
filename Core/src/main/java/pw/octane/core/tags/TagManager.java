package pw.octane.core.tags;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.bukkit.ChatColor;
import pw.octane.core.CoreModule;
import pw.octane.core.networking.CoreRedisAction;
import pw.octane.core.ranks.Rank;
import pw.octane.manager.networking.mongo.Mongo;
import pw.octane.manager.networking.mongo.MongoDeserializedResult;
import pw.octane.manager.networking.mongo.MongoUpdate;
import pw.octane.manager.networking.redis.RedisMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TagManager {

    private CoreModule module;
    private @Getter Map<UUID, Tag> tags;
    public TagManager(CoreModule module) {
        this.module = module;
        this.tags = new HashMap<>();

        Mongo mongo = module.getManager().getMongo();
        mongo.createCollection(false, "core_tags");
        mongo.getCollectionIterable(false, "core_tags",
                iterable -> iterable.forEach(
                document ->
                pull(false, document.get("_id", UUID.class),
                obj -> {})));
    }

    public Tag getTag(UUID uuid) {
        return tags.get(uuid);
    }

    public Tag getTag(String name) {
        for(Tag tag : tags.values()) {
            if(tag.getName().equalsIgnoreCase(name)) {
                return tag;
            }
        }
        return null;
    }

    public Tag createTag(String name, String tagSuffix) {
        for(Tag tag : tags.values()) {
            if(tag.getName().equalsIgnoreCase(name)) {
                return null;
            }
        }

        Tag tag = new Tag(UUID.randomUUID());
        tag.setName(name.toLowerCase());
        tag.setDisplayName(name);
        tag.setTag(tagSuffix);

        tags.put(tag.getUuid(), tag);

        push(true, tag);

        return tag;
    }

    public void pull(boolean async, UUID uuid, MongoDeserializedResult mdr) {
        module.getManager().getMongo().getDocument(async, "core_tags", uuid, document -> {
            if(document != null) {
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                Tag tag = gson.fromJson(document.getString("elements"), Tag.class);
                tags.put(uuid, tag);
                mdr.call(tag);
            } else {
                mdr.call(null);
            }
        });

        module.getProfileManager().update();
    }

    public void push(boolean async, Tag tag) {
        MongoUpdate mu = new MongoUpdate("core_tags", tag.getUuid());
        mu.put("elements", tag.serialize());
        module.getManager().getMongo().massUpdate(async, mu);

        JsonObject json = new JsonObject();
        json.addProperty("action", CoreRedisAction.TAG_UPDATE.toString());
        json.addProperty("fromServer", module.getConfig().getString("general.server_name"));
        json.addProperty("tag", tag.getUuid().toString());

        module.getManager().getRedisPublisher().getMessageQueue().add(new RedisMessage("core", json));

        module.getProfileManager().update();
    }

    public void remove(boolean async, Tag tag) {
        module.getManager().getMongo().deleteDocument(async, "core_tags", tag.getUuid());

        JsonObject json = new JsonObject();
        json.addProperty("action", CoreRedisAction.TAG_DELETE.toString());
        json.addProperty("fromServer", module.getConfig().getString("general.server_name"));
        json.addProperty("tag", tag.getUuid().toString());

        module.getManager().getRedisPublisher().getMessageQueue().add(new RedisMessage("core", json));

        tags.remove(tag.getUuid());

        module.getProfileManager().update();
    }
}
