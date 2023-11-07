package pw.octane.core.punishments;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import pw.octane.core.CoreModule;
import pw.octane.core.networking.CoreRedisAction;
import pw.octane.core.profiles.Profile;
import pw.octane.manager.networking.mongo.MongoDeserializedResult;
import pw.octane.manager.networking.mongo.MongoUpdate;
import pw.octane.manager.networking.redis.RedisMessage;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PunishmentManager {

    private CoreModule module;
    private Map<UUID, Punishment> punishments;
    public PunishmentManager(CoreModule module) {
        this.module = module;
        this.punishments = new HashMap<>();
    }

    public Punishment create(Punishment.Type type, Profile victim, UUID issuer, String reason, Date expires, boolean silent) {
        for(Punishment punishment : victim.getPunishments(type)) {
            if(punishment.isActive()) {
                return null;
            }
        }

        Punishment punishment = new Punishment(UUID.randomUUID());
        punishment.setType(type);
        punishment.setVictim(victim.getUuid());
        punishment.setIssuer(issuer);
        punishment.setIssueReason(reason);
        punishment.setIssued(new Date());
        punishment.setExpires(expires);
        punishment.setSilentIssue(silent);

        punishments.put(punishment.getUuid(), punishment);
        victim.getPunishments().add(punishment.getUuid());

        push(true, punishment, false);

        if(victim.getPlayer() == null) {
            module.getProfileManager().push(true, victim, false);
        }

        return punishment;
    }

    public Punishment getPunishment(UUID uuid) {
        return punishments.get(uuid);
    }

    public void pull(boolean async, UUID uuid, boolean store, MongoDeserializedResult mdr) {
        module.getManager().getMongo().getDocument(async, "core_punishments", uuid, d -> {
            if(d != null) {
                Punishment punishment = new Punishment(uuid);
                punishment.importFromDocument(d);

                mdr.call(punishment);
                if(store) {
                    punishments.put(punishment.getUuid(), punishment);
                }
            } else {
                mdr.call(null);
            }
        });
    }

    public void push(boolean async, Punishment punishment, boolean unload) {
        MongoUpdate mu = new MongoUpdate("core_punishments", punishment.getUuid());
        mu.setUpdate(punishment.export());
        module.getManager().getMongo().massUpdate(async, mu);

        JsonObject json = new JsonObject();
        json.addProperty("action", CoreRedisAction.PUNISHMENT_UPDATE.toString());
        json.addProperty("fromServer", module.getConfig().getString("general.server_name"));
        json.addProperty("punishment", punishment.getUuid().toString());

        module.getManager().getRedisPublisher().getMessageQueue().add(new RedisMessage("core", json));

        if(unload) {
            punishments.remove(punishment.getUuid());
        }
    }
}
