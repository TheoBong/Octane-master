package pw.octane.core.profiles;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import pw.octane.core.CoreModule;
import pw.octane.core.punishments.Punishment;
import pw.octane.core.ranks.Rank;
import pw.octane.core.tags.Tag;
import pw.octane.core.tags.TagManager;

import java.util.*;

public @Data class Profile {

    public enum Cooldown {
        CHAT, REPORT;
    }

    private final UUID uuid;
    private String name;
    private String currentIp;
    private boolean nameMc;
    private Settings settings;
    private UUID appliedTag;
    private UUID lastRecipient;
    private PermissionAttachment permissionAttachment;
    private List<String> ipHistory;
    private List<UUID> ignored, transactionIds, ranks, punishments, tags;
    private Map<Cooldown, Date> cooldowns;

    public Profile(UUID uuid) {
        this.uuid = uuid;
        this.settings = new Settings();
        this.ipHistory = new ArrayList<>();
        this.ignored = new ArrayList<>();
        this.transactionIds = new ArrayList<>();
        this.ranks = new ArrayList<>();
        this.punishments = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.cooldowns = new HashMap<>();
    }

    public Profile(Player player) {
        this(player.getUniqueId());
        this.name = player.getName();
        this.currentIp = player.getAddress().getAddress().getHostAddress();
    }

    public Tag getAppliedTag() {
        if(appliedTag != null) {
            return CoreModule.INSTANCE.getTagManager().getTag(appliedTag);
        }
        return null;
    }

    public List<Tag> getAllTags() {
        List<Tag> list = new ArrayList<>();
        TagManager tagManager = CoreModule.INSTANCE.getTagManager();
        for(UUID uuid : tags) {
            Tag tag = tagManager.getTag(uuid);
            if(tag != null) {
                list.add(tag);
            }
        }

        return list;
    }

    public Rank getHighestRank() {
        Rank rank = null;
        for(Rank r : getAllRanks()) {
            if(rank != null) {
                if(r.getWeight() > rank.getWeight()) {
                    rank = r;
                }
            } else {
                rank = r;
            }
        }
        return rank;
    }

    public List<Rank> getAllRanks() {
        List<Rank> ranks = new ArrayList<>();
        Map<UUID, Rank> allRanks = CoreModule.INSTANCE.getRankManager().getRanks();
        for(Rank rank : allRanks.values()) {
            if(getRanks().contains(rank.getUuid()) || (rank.isNameMc() && nameMc)) {
                ranks.add(rank);
            }
        }

        return ranks;
    }

    public Punishment getActivePunishment(Punishment.Type type) {
        for(Punishment punishment : getPunishments(type)) {
            if (punishment.isActive()) {
                return punishment;
            }
        }
        return null;
    }

    public List<Punishment> getPunishments(Punishment.Type type) {
        List<Punishment> punishments = new ArrayList<>();
        for(UUID uuid : this.punishments) {
            Punishment punishment = CoreModule.INSTANCE.getPunishmentManager().getPunishment(uuid);
            if(punishment != null && punishment.getType().equals(type)) {
                punishments.add(punishment);
            }
        }
        return punishments;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public String serialize() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }

    public void addIp(String ip) {
        this.currentIp = ip;
        if(!ipHistory.contains(ip)) {
            ipHistory.add(ip);
        }
    }

    public void addRank(UUID uuid) {
        ranks.add(uuid);
        update();
    }

    public void removeRank(UUID uuid) {
        ranks.remove(uuid);
        update();
    }

    public Punishment punish(Punishment.Type type, UUID issuer, String reason, Date expires, boolean silent) {
        Punishment punishment = CoreModule.INSTANCE.getPunishmentManager().create(type, this, issuer, reason, expires, silent);
        if(punishment != null) {
            punishment.execute();
        }
        return punishment;
    }

    public void update() {
        CoreModule module = CoreModule.INSTANCE;
        punishments.removeIf(uuid -> module.getPunishmentManager().getPunishment(uuid) == null);
        ranks.removeIf(uuid -> module.getRankManager().getRank(uuid) == null);
        tags.removeIf(uuid -> module.getTagManager().getTag(uuid) == null);

        Player player = getPlayer();
        if(player != null) {
            player.getEffectivePermissions().clear();
            this.permissionAttachment = player.addAttachment(module.getPlugin());
            for(Rank rank : getAllRanks()) {
                for(Map.Entry<String, Boolean> entry : rank.getAllPermissions(module.getConfig().getString("general.server_category")).entrySet()) {
                    permissionAttachment.setPermission(entry.getKey(), entry.getValue());
                }
            }
        }

        if(getAllRanks().isEmpty()) {
            Rank rank = CoreModule.INSTANCE.getRankManager().getDefaultRank();
            if(rank != null) {
                getRanks().add(rank.getUuid());
            }
        }
    }

    public void importFromDocument(Document d) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        setName(d.getString("name"));
        setCurrentIp(d.getString("current_ip"));
        setNameMc(d.getBoolean("name_mc"));
        setSettings(gson.fromJson(d.getString("settings"), Settings.class));

        String tag = d.getString("applied_tag");
        if(tag != null) {
            setAppliedTag(UUID.fromString(tag));
        }

        setIpHistory(d.getList("ip_history", String.class));
        setIgnored(d.getList("ignored", UUID.class));
        setTransactionIds(d.getList("transaction_ids", UUID.class));
        setRanks(d.getList("ranks", UUID.class));
        setTags(d.getList("tags", UUID.class));
        setPunishments(d.getList("punishments", UUID.class));
    }

    public Map<String, Object> export() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("current_ip", currentIp);
        map.put("name_mc", nameMc);
        map.put("settings", gson.toJson(settings));

        if(appliedTag != null) {
            map.put("tag", appliedTag.toString());
        }

        map.put("ip_history", ipHistory);
        map.put("ignored", ignored);
        map.put("transaction_ids", transactionIds);
        map.put("ranks", ranks);
        map.put("tags", tags);
        map.put("punishments", punishments);
        return map;
    }
}
