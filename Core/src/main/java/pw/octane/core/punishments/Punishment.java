package pw.octane.core.punishments;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import pw.octane.core.CoreModule;
import pw.octane.core.networking.CoreRedisAction;
import pw.octane.manager.networking.redis.RedisMessage;

import java.util.*;

public @Data class Punishment {

    public enum Type {
        BAN, BLACKLIST, KICK, MUTE;

        public String pastMessage() {
            switch(this) {
                case BAN:
                    return "banned";
                case BLACKLIST:
                    return "blacklisted";
                case KICK:
                    return "kicked";
                case MUTE:
                    return "muted";
                default:
                    return null;
            }
        }
    }

    private final UUID uuid;
    private UUID victim, issuer, pardoner;
    private String issueReason, pardonReason;
    private Date issued, expires, pardoned;
    private Type type;
    private boolean silentIssue, silentPardon;

    public boolean isActive() {
        boolean b = !type.equals(Type.KICK);

        if(expires != null) {
            if(expires.before(new Date())) {
                b = false;
            }
        }

        if(pardoned != null) {
            b = false;
        }

        return b;
    }

    public void execute() {
        Player player = Bukkit.getPlayer(victim);
        String victimName, issuerName;

        if(isActive() || type.equals(Type.KICK)) {
            if (issuer != null) {
                Player p = Bukkit.getPlayer(issuer);
                issuerName = p.getName();
            } else {
                issuerName = "&cConsole";
            }
        } else {
            if (pardoner != null) {
                Player p = Bukkit.getPlayer(pardoner);
                issuerName = p.getName();
            } else {
                issuerName = "&cConsole";
            }
        }

        if(player != null && player.isOnline()) {
            victimName = player.getName();
            if(isActive() || type.equals(Type.KICK)) {
                switch (type) {
                    case BAN:
                        player.kickPlayer(ChatColor.RED + "Your account has been banned.");
                        break;
                    case BLACKLIST:
                        player.kickPlayer(ChatColor.DARK_RED + "Your account has been blacklisted.");
                        break;
                    case MUTE:
                        player.sendMessage(ChatColor.RED + "You have been muted for: " + ChatColor.WHITE + this.issueReason);
                        break;
                    case KICK:
                        player.kickPlayer(ChatColor.RED + "You have been kicked for: " + ChatColor.WHITE + this.issueReason);
                        break;
                }
            }
        } else {
            OfflinePlayer op = Bukkit.getOfflinePlayer(victim);
            victimName = op.getName();
        }

        StringBuilder sb = new StringBuilder();
        if(isActive() || type.equals(Type.KICK)) {
            sb.append("&f&l * &c&l" + StringUtils.capitalize(type.toString().toLowerCase()) + (silentIssue ? " &7[Silent]" : ""));
            sb.append("\n&cVictim: &f" + victimName);
            sb.append("\n&cIssuer: &f" + issuerName);
            sb.append("\n&cReason: &f" + this.issueReason);
            if(!type.equals(Type.KICK)) {
                sb.append("\n&cExpires: &f" + (this.expires == null ? "Never" : this.expires.toString()));
            }
        } else {
            sb.append("&f&l * &c&lUn" + type.toString().toLowerCase() + (silentIssue ? " &7[Silent]" : ""));
            sb.append("\n&cVictim: &f" + victimName);
            sb.append("\n&cReason: &f" + issueReason);
            sb.append("\n&cPardoner: &f" + issuerName);
            sb.append("\n&cPardon Reason: &f" + this.pardonReason);
        }

        JsonObject json = new JsonObject();
        json.addProperty("action", CoreRedisAction.STAFF_BROADCAST.toString());
        json.addProperty("message", sb.toString());
        RedisMessage staffMessage = new RedisMessage("core", json);

        String message;
        if(type.equals(Type.KICK)) {
            message = "&f" + victimName + "&a was " + type.pastMessage() + " by " + issuerName + "&a.";
        } else {
            message = "&f" + victimName + "&a was " + (this.isActive() ? (this.expires == null ? "permanently " : "temporarily ") : "un") + type.pastMessage() + " by " + issuerName + "&a.";
        }
        JsonObject j = new JsonObject();
        RedisMessage rm = new RedisMessage("core", j);
        if(silentIssue) {
            j.addProperty("action", CoreRedisAction.STAFF_BROADCAST.toString());
            j.addProperty("message", "&7[Silent] " + message);
        } else {
            j.addProperty("action", CoreRedisAction.BROADCAST.toString());
            j.addProperty("message", message);
        }

        Queue<RedisMessage> queue = CoreModule.INSTANCE.getManager().getRedisPublisher().getMessageQueue();
        queue.add(rm);
        queue.add(staffMessage);
    }

    public void importFromDocument(Document d) {
        setVictim(d.get("victim", UUID.class));
        setIssuer(d.get("issuer", UUID.class));
        setPardoner(d.get("pardoner", UUID.class));

        setIssueReason(d.getString("issue_reason"));
        setPardonReason(d.getString("pardon_reason"));
        setIssued(d.getDate("issued"));
        setExpires(d.getDate("expires"));
        setPardoned(d.getDate("pardoned"));
        setType(Punishment.Type.valueOf(d.getString("type")));
        setSilentIssue(d.getBoolean("silent_issue"));
        setSilentPardon(d.getBoolean("silent_pardon"));
    }

    public Map<String, Object> export() {
        Map<String, Object> map = new HashMap<>();
        map.put("victim", victim);
        map.put("issuer", issuer);
        map.put("pardoner", pardoner);

        map.put("issue_reason", issueReason);
        map.put("pardon_reason", pardonReason);
        map.put("issued", issued);
        map.put("expires", expires);
        map.put("pardoned", pardoned);
        map.put("type", type.toString());
        map.put("silent_issue", silentIssue);
        map.put("silent_pardon", silentPardon);
        return map;
    }
}
