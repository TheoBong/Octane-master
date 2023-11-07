package pw.octane.practice.occupations;

import lombok.Data;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pw.octane.manager.utils.Colors;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.arenas.Arena;
import pw.octane.practice.kits.Kit;
import pw.octane.practice.profiles.Cooldown;
import pw.octane.practice.profiles.Profile;
import pw.octane.practice.profiles.ProfileManager;
import pw.octane.practice.utils.EntityHider;

import java.util.*;

public @Data abstract class Occupation {

    public enum State {
        CREATED, LOBBY, STARTING, ACTIVE, ENDED, STOPPED;
    }

    private final UUID uuid;
    private PracticeModule module;
    private Date created, started, ended;
    private State state;
    private Kit kit;
    private Arena arena;
    private int round;
    private Map<UUID, Participant> participants;
    private Map<UUID, Spectator> spectators;
    private List<Entity> entities;
    private List<Block> placedBlocks;
    private List<BrokenBlock> brokenBlocks;

    public Occupation(PracticeModule module, UUID uuid) {
        this.module = module;
        this.uuid = uuid;
        this.participants = new HashMap<>();
        this.spectators = new HashMap<>();
        this.entities = new ArrayList<>();
        this.brokenBlocks = new ArrayList<>();
        this.placedBlocks = new ArrayList<>();
        this.state = State.CREATED;
    }

    public abstract void start();

//    public abstract void nextRound();

    public abstract void end();

    public abstract void forceEnd();

    public abstract List<String> getScoreboard(Profile profile);

    public abstract List<String> getSpectatorScoreboard(Profile profile);

    public void eliminate(Player player) {
        Participant participant = getParticipants().get(player.getUniqueId());
        if(participant != null) {
            participant.setAlive(false);

            if(getAlive().size() > 1) {
                for (ItemStack item : player.getInventory()) {
                    if (item != null && !item.getType().equals(Material.AIR)) {
                        Item i = player.getWorld().dropItem(player.getLocation(), item);
                        this.addEntity(i);
                    }
                }
            }

            Location location = player.getLocation();
            Profile profile = PracticeModule.INSTANCE.getProfileManager().get(player.getUniqueId());
            profile.getCosmetics().getEliminateAnimation().runAnimation(location, this);

            for(Cooldown cooldown : profile.getCooldowns().values()) {
                cooldown.expire();
            }

            for(Spectator spectator : getSpectators().values()) {
                if(spectator.getTarget().equals(player)) {
                    spectator.setTarget(null);
                }
            }

            participant.setGameInventory(new GameInventory(participant));
            this.spectateStart(player);
            this.announce("&f" + player.getName() + "&a has been eliminated" + (participant.getAttacker() == null ? "." : " by &f" + Bukkit.getOfflinePlayer(participant.getAttacker()).getName() + "&a."));
        }
    }

    public void handleDamage(Player victim, EntityDamageEvent event) {
        Participant participant = this.getParticipants().get(victim.getUniqueId());
        participant.setLastDamageCause(event.getCause());
        if(this.getState().equals(Occupation.State.ACTIVE)) {
            double damage = event.getFinalDamage();
            participant.setCurrentCombo(0);
            if(kit != null && kit.getType().equals(Kit.Type.SUMO)) {
                event.setDamage(0);
                victim.setHealth(20);
            }

            if(event.getCause().equals(EntityDamageEvent.DamageCause.FIRE) || event.getCause().equals(EntityDamageEvent.DamageCause.LAVA) || event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK)) {
                damage += 1;
            }

            if(victim.getHealth() - damage < 0) {
                victim.setHealth(victim.getMaxHealth());
                this.eliminate(victim);
                Bukkit.getScheduler().runTaskLater(PracticeModule.INSTANCE.getPlugin(), ()-> victim.setHealth(20), 1);
            }
        } else {
            event.setCancelled(true);
        }
    }

    public void handleHit(Player victim, Player attacker, EntityDamageByEntityEvent event) {
        Participant victimParticipant = this.getParticipants().get(victim.getUniqueId());
        Participant participant = this.getParticipants().get(attacker.getUniqueId());
        if(victimParticipant != null && participant != null) {
            victimParticipant.setAttacker(attacker.getUniqueId());
            participant.hits++;
            participant.currentCombo++;

            if(participant.currentCombo > participant.longestCombo) {
                participant.longestCombo = participant.currentCombo;
            }
        } else {
            event.setCancelled(true);
        }
    }

    public void join(Player player) {
        Profile profile = PracticeModule.INSTANCE.getProfileManager().get(player.getUniqueId());
        profile.setOccupation(this);

        Participant participant = new Participant(player.getUniqueId(), player.getName());
        this.participants.put(player.getUniqueId(), participant);
    }

    public void spectateStart(Player player) {
        spectateStart(player, (Location) null);
    }

    public void spectateStart(Player player, Player target) {
        spectateStart(player, target.getLocation());
        this.getSpectators().get(player.getUniqueId()).setTarget(target);
    }

    public void spectateStart(Player player, Location location) {
        ProfileManager pm = PracticeModule.INSTANCE.getProfileManager();
        Profile profile = pm.get(player.getUniqueId());

        this.getSpectators().put(player.getUniqueId(), new Spectator(player.getUniqueId(), player.getName()));

        if(!this.getParticipants().containsKey(player.getUniqueId())) {
            String message = "&b" + player.getName() + "&f has started spectating.";
            player.sendMessage(ChatColor.GREEN + "You have started spectating.");
            if(player.hasPermission("practice.staff")) {
                this.staffAnnounce("&7[SILENT] " + message);
            } else {
                this.announce(message);
            }
        }

        profile.setOccupation(this);
        profile.playerItems();

        pm.playerUpdateVisibility();
        updateEntities();

        player.setAllowFlight(true);
        player.setFlying(true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 1, true, false));
        if(location != null) {
            player.teleport(location);
        }
    }

    public void spectateEnd(Player player) {
        ProfileManager pm = PracticeModule.INSTANCE.getProfileManager();
        Profile profile = pm.get(player.getUniqueId());

        if(!this.getState().equals(State.ENDED)) {
            if (!this.getParticipants().containsKey(player.getUniqueId())) {
                String message = "&b" + player.getName() + "&f has stopped spectating.";
                if (player.hasPermission("practice.staff")) {
                    this.staffAnnounce("&7[SILENT] " + message);
                } else {
                    this.announce(message);
                }
            }
        }

        this.getSpectators().remove(player.getUniqueId());

        profile.setOccupation(null);
        profile.playerUpdate();

        pm.playerUpdateVisibility();
        updateEntities();
    }

    public void leave(Player player) {
        if(getAlive().containsKey(player.getUniqueId())) {
            eliminate(player);
        } else {
            spectateEnd(player);
        }
    }

    public List<Player> getAllPlayers() {
        List<Player> players = new ArrayList<>(getAlivePlayers());
        players.addAll(getSpectatorsPlayers());
        return players;
    }

    public Map<UUID, Participant> getAlive() {
        Map<UUID, Participant> alive = new HashMap<>();
        for(Map.Entry<UUID, Participant> entry : participants.entrySet()) {
            if(entry.getValue().isAlive()) {
                alive.put(entry.getKey(), entry.getValue());
            }
        }
        return alive;
    }

    public List<Player> getAlivePlayers() {
        List<Player> players = new ArrayList<>();
        for(UUID uuid : getAlive().keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if(player != null) {
                players.add(player);
            }
        }
        return players;
    }

    public List<Player> getCurrentPlaying() {
        return getAlivePlayers();
    }

    public List<Player> getSpectatorsPlayers() {
        List<Player> players = new ArrayList<>();
        for(UUID uuid : spectators.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if(player != null) {
                players.add(player);
            }
        }
        return players;
    }

    public <T> void playEffect(Location location, Effect effect, T t) {
        for(Player player : getAllPlayers()) {
            player.playEffect(location, effect, t);
        }
    }

    public void playSound(Location location, Sound sound, float v1, float v2) {
        for(Player player : getAllPlayers()) {
            player.playSound(location, sound, v1, v2);
        }
    }

    public void addEntity(Entity entity) {
        getEntities().add(entity);
        updateEntities();
    }

    public void updateEntities() {
        EntityHider eh = PracticeModule.INSTANCE.getEntityHider();
        List<Player> players = getAllPlayers();
        for(Entity entity : getEntities()) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(players.contains(player)) {
                    eh.showEntity(player, entity);
                } else {
                    eh.hideEntity(player, entity);
                }
            }
        }
    }

    public void announce(String s) {
        for(Player p : getAllPlayers()) {
            p.sendMessage(Colors.get(s));
        }
    }

    public void staffAnnounce(String s) {
        for(Player p : getAllPlayers()) {
            if(p.hasPermission("practice.staff")) {
                p.sendMessage(Colors.get(s));
            }
        }
    }

    public boolean isBuild() {
        return kit.getType().isBuild();
    }

    public boolean isMoveOnStart() {
        return !kit.getType().equals(Kit.Type.SUMO);
    }

    public boolean isSpleef() {
        return kit.getType().equals(Kit.Type.SPLEEF);
    }

    public boolean seeEveryone() {
        return false;
    }
}
