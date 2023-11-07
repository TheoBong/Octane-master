package pw.octane.practice.kits;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import pw.octane.manager.networking.mongo.Mongo;
import pw.octane.manager.networking.mongo.MongoDeserializedResult;
import pw.octane.manager.networking.mongo.MongoUpdate;
import pw.octane.practice.PracticeModule;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KitManager {

    private PracticeModule module;
    private @Getter Map<UUID, Kit> kits;
    public KitManager(PracticeModule module) {
        this.module = module;
        this.kits = new HashMap<>();

        Mongo mongo = module.getManager().getMongo();
        mongo.createCollection(false, "practice_kits");
        mongo.getCollectionIterable(false, "practice_kits",
                iterable -> iterable.forEach(
                document ->
                pull(false, document.get("_id", UUID.class),
                obj -> {})));
    }

    public Kit get(UUID uuid) {
        return kits.get(uuid);
    }

    public Kit get(String s) {
        for(Kit kit : kits.values()) {
            if(kit.getName().equalsIgnoreCase(s)) {
                return kit;
            }
        }

        return null;
    }

    public Kit createKit(String name) {
        for(Kit kit : kits.values()) {
            if(kit.getName().equalsIgnoreCase(name)) {
                return null;
            }
        }

        Kit kit = new Kit(UUID.randomUUID());
        kit.setName(name.toLowerCase());
        kit.setDisplayName(name);
        kits.put(kit.getUuid(), kit);
        push(true, kit);
        return kit;
    }

    public void pull(boolean async, UUID uuid, MongoDeserializedResult mdr) {
        module.getManager().getMongo().getDocument(async, "practice_kits", uuid, document -> {
            if(document != null) {
                Kit kit = new Kit(uuid);
                kit.importFromDocument(document);
                kits.put(kit.getUuid(), kit);
                mdr.call(kit);
            } else {
                mdr.call(null);
            }
        });
    }

    public void push(boolean async, Kit kit) {
        MongoUpdate mu = new MongoUpdate("practice_kits", kit.getUuid());
        mu.setUpdate(kit.export());
        module.getManager().getMongo().massUpdate(async, mu);
    }

    public void remove(boolean async, Kit kit) {
        module.getManager().getMongo().deleteDocument(async, "practice_kits", kit.getUuid());
        kits.remove(kit.getUuid());
    }
}
