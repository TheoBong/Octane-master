package pw.octane.practice.profiles;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.cosmetics.ProfileCosmetics;
import pw.octane.practice.items.IItem;
import pw.octane.practice.items.InteractableItem;
import pw.octane.practice.kits.CustomKit;
import pw.octane.practice.occupations.DuelRequest;
import pw.octane.practice.occupations.Occupation;
import pw.octane.practice.occupations.tournaments.Tournament;
import pw.octane.practice.parties.Party;
import pw.octane.practice.queues.PracticeQueue;
import pw.octane.practice.queues.QueueMember;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public @Data class Profile {

    public enum State {
        NONE, LOBBY, FOLLOWING, KIT_EDITOR, TOURNAMENT, PARTY, EVENT, QUEUE, WAITING_IN_GAME, IN_GAME, SPECTATING;
    }

    private final UUID uuid;
    private String name;
    private ProfileSettings settings;
    private ProfileCosmetics cosmetics;
    private Map<UUID, Map<Integer, CustomKit>> customKits;
    private Map<UUID, Map<PracticeQueue.Type, Integer>> elo;
    private Occupation occupation;
    private Tournament tournament;
    private Party party;
    private CustomKit editing, renaming;
    private UUID following;
    private PreviousMatch previousMatch;
    private Map<UUID, DuelRequest> duelRequests;
    private Map<Cooldown.Type, Cooldown> cooldowns;

    public Profile(UUID uuid) {
        this.uuid = uuid;
        this.settings = new ProfileSettings();
        this.cosmetics = new ProfileCosmetics();
        this.customKits = new HashMap<>();
        this.elo = new HashMap<>();
        this.duelRequests = new HashMap<>();
        this.cooldowns = new HashMap<>();
    }

    public State getState() {
        if(occupation != null) {
            if(occupation.getAlive().containsKey(uuid)) {
                return State.IN_GAME;
            } else {
                return State.SPECTATING;
            }
        } else {
            if(editing != null) {
                return State.KIT_EDITOR;
            } else if(getQueue() != null) {
                return State.QUEUE;
            } else if(tournament != null) {
                return State.TOURNAMENT;
            } else if(party != null) {
                return State.PARTY;
            }
        }

        return State.LOBBY;
    }

    public QueueMember getQueue() {
        return PracticeModule.INSTANCE.getQueueManager().find(uuid);
    }

    public int getElo(UUID uuid, PracticeQueue.Type type) {
        getElo().computeIfAbsent(uuid, k -> new HashMap<>());
        getElo().get(uuid).putIfAbsent(type, 1000);
        return getElo().get(uuid).get(type);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public void playerReset() {
        Player player = getPlayer();
        if(player != null) {
            player.setFoodLevel(20);
            player.setMaxHealth(20);
            player.setHealth(20);
            player.setGameMode(GameMode.SURVIVAL);
            player.setLevel(0);
            player.setExp(0);
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setWalkSpeed(0.2F);
            player.setFlySpeed(0.1F);
            player.setFireTicks(0);
            player.setSaturation(20);
            for(PotionEffect effect : player.getActivePotionEffects()){
                player.removePotionEffect(effect.getType());
            }
        }
    }

    public void playerItems() {
        Player player = getPlayer();
        if(player != null) {
            playerReset();

            PlayerInventory inventory = player.getInventory();
            State state = getState();
            IItem kitEditor = InteractableItem.KIT_EDITOR.getItem(this);
            IItem settings = InteractableItem.SETTINGS.getItem(this);
            IItem rematch = null;

            for (InteractableItem item : InteractableItem.values()) {
                if (item.forState().equals(state)) {
                    IItem i = item.getItem(this);
                    player.getInventory().setItem(i.getSlot(), i.getItemStack());
                }
            }

            switch(state) {
                case LOBBY:
                    if(previousMatch != null && !previousMatch.isExpired()) {
                        rematch = InteractableItem.REMATCH.getItem(this);
                        inventory.setItem(rematch.getSlot(), rematch.getItemStack());
                    }
                case PARTY:
                case TOURNAMENT:
                    inventory.setItem(kitEditor.getSlot(), kitEditor.getItemStack());
                    inventory.setItem(settings.getSlot(), settings.getItemStack());
            }
        }
    }

    public void playerUpdate() {
        Player player = getPlayer();
        if(player != null) {
            playerUpdateTime();
            playerUpdateVisibility();
            playerItems();
            setEditing(null);
            setRenaming(null);

            switch(getState()) {
                case LOBBY:
                case PARTY:
                case TOURNAMENT:
                case QUEUE:
                    Location location = PracticeModule.INSTANCE.getLobby();
                    if (location != null) {
                        player.teleport(PracticeModule.INSTANCE.getLobby());
                    } else {
                        player.sendMessage(ChatColor.RED + "You could not be teleported to the lobby. Please notify staff!");
                    }
            }
        }
    }

    public void playerUpdateVisibility() {
        Player player = getPlayer();
        if(player != null) {
            if(occupation != null) {
                if(occupation.getCurrentPlaying().contains(player)) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if(!occupation.seeEveryone() && !occupation.getCurrentPlaying().contains(p)) {
                            player.hidePlayer(p);
                        } else {
                            player.showPlayer(p);
                        }
                    }
                } else {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        boolean b = getSettings().isSpectatorVisibility() ? occupation.getAllPlayers().contains(p) : occupation.getCurrentPlaying().contains(p);
                        if (b) {
                            player.showPlayer(p);
                        } else {
                            player.hidePlayer(p);
                        }
                    }
                }
            } else {
                if(getSettings().isPlayerVisibility()) {
                    ProfileManager pm = PracticeModule.INSTANCE.getProfileManager();
                    for(Player p : Bukkit.getOnlinePlayers()) {
                        Profile profile = pm.get(p.getUniqueId());
                        if(profile.getOccupation() != null && profile.getOccupation().getSpectators().get(p.getUniqueId()) != null) {
                            player.hidePlayer(p);
                        } else {
                            player.showPlayer(p);
                        }
                    }
                } else {
                    for(Player p : Bukkit.getOnlinePlayers()) {
                        player.hidePlayer(p);
                    }
                }
            }
        }
    }

    public void playerUpdateTime() {
        Player player = getPlayer();
        if(player != null) {
            player.setPlayerTime(getSettings().getTime().getTime(), false);
        }
    }

    public void importFromDocument(Document d) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        setName(d.getString("name"));
        setSettings(gson.fromJson(d.getString("settings"), ProfileSettings.class));
        setCosmetics(gson.fromJson(d.getString("cosmetics"), ProfileCosmetics.class));

        Object customKits = d.get("custom_kits");
        if(customKits instanceof Map) {
            Map<String, Map<String, Map<String, Object>>> ck = (Map<String, Map<String, Map<String, Object>>>) customKits;
            for(Map.Entry<String, Map<String, Map<String, Object>>> kitEntry : ck.entrySet()) {
                UUID uuid = UUID.fromString(kitEntry.getKey());
                for(Map.Entry<String, Map<String, Object>> customKitEntry : kitEntry.getValue().entrySet()) {
                    int i = Integer.parseInt(customKitEntry.getKey());
                    Map<String, Object> map = customKitEntry.getValue();
                    CustomKit customKit = new CustomKit((UUID) map.get("kit"));
                    customKit.importFromMap(map);
                    getCustomKits().computeIfAbsent(uuid, v -> new HashMap<>());
                    getCustomKits().get(uuid).put(i, customKit);
                }
            }

        }

        Object elo = d.get("elo");
        if(elo != null) {
            setElo((Map<UUID, Map<PracticeQueue.Type, Integer>>) elo);
        }
    }

    public Map<String, Object> export() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("settings", gson.toJson(settings));
        map.put("cosmetics", gson.toJson(cosmetics));

        Map<String, Map<String, Map<String, Object>>> ck = new HashMap<>();
        for(Map.Entry<UUID, Map<Integer, CustomKit>> kitEntry : getCustomKits().entrySet()) {
            UUID uuid = kitEntry.getKey();
            for(Map.Entry<Integer, CustomKit> customKitEntry : kitEntry.getValue().entrySet()) {
                CustomKit customKit = customKitEntry.getValue();
                ck.computeIfAbsent(uuid.toString(), v -> new HashMap<>());
                ck.get(uuid.toString()).put(String.valueOf(customKit.getNumber()), customKit.export());
            }
        }

        map.put("custom_kits", ck);
        map.put("elo", elo);
        return map;
    }
}
