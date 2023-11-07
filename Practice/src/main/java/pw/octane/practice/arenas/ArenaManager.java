package pw.octane.practice.arenas;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.bukkit.ChatColor;
import pw.octane.manager.networking.mongo.Mongo;
import pw.octane.manager.networking.mongo.MongoDeserializedResult;
import pw.octane.manager.networking.mongo.MongoUpdate;
import pw.octane.manager.networking.redis.RedisMessage;
import pw.octane.manager.utils.Colors;
import pw.octane.practice.PracticeModule;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArenaManager {

    private PracticeModule module;
    private @Getter Map<UUID, Arena> arenas;
    public ArenaManager(PracticeModule module) {
        this.module = module;
        this.arenas = new HashMap<>();

        Mongo mongo = module.getManager().getMongo();
        mongo.createCollection(false, "practice_arenas");
        mongo.getCollectionIterable(false, "practice_arenas",
                iterable -> iterable.forEach(
                document ->
                pull(false, document.get("_id", UUID.class),
                obj -> {})));
    }

    public Arena get(UUID uuid) {
        return arenas.get(uuid);
    }

    public Arena get(String s) {
        for(Arena arena : arenas.values()) {
            if(arena.getName().equalsIgnoreCase(s)) {
                return arena;
            }
        }

        return null;
    }

    public Arena createArena(String name) {
        for(Arena arena : arenas.values()) {
            if(arena.getName().equalsIgnoreCase(name)) {
                return null;
            }
        }

        Arena arena = new Arena(UUID.randomUUID());
        arena.setName(Colors.strip(name.toLowerCase()));
        arena.setDisplayName(name);
        arenas.put(arena.getUuid(), arena);
        push(true, arena);
        return arena;
    }

    public void pull(boolean async, UUID uuid, MongoDeserializedResult mdr) {
        module.getManager().getMongo().getDocument(async, "practice_arenas", uuid, document -> {
            if(document != null) {
                Arena arena = new Arena(uuid);
                arena.importFromDocument(document);
                arenas.put(arena.getUuid(), arena);
                mdr.call(arena);
            } else {
                mdr.call(null);
            }
        });
    }

    public void push(boolean async, Arena arena) {
        MongoUpdate mu = new MongoUpdate("practice_arenas", arena.getUuid());
        mu.setUpdate(arena.export());
        module.getManager().getMongo().massUpdate(async, mu);
    }

    public void remove(boolean async, Arena arena) {
        module.getManager().getMongo().deleteDocument(async, "practice_arenas", arena.getUuid());
        arenas.remove(arena.getUuid());
    }

}
