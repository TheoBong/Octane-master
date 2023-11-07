package pw.octane.practice.occupations.impl;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import pw.octane.manager.utils.Colors;
import pw.octane.manager.utils.ItemBuilder;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.arenas.Arena;
import pw.octane.practice.kits.CustomKit;
import pw.octane.practice.kits.Kit;
import pw.octane.practice.occupations.*;
import pw.octane.practice.profiles.*;
import pw.octane.practice.queues.PracticeQueue;
import pw.octane.practice.utils.PlayerUtils;
import pw.octane.practice.utils.TimeUtil;

import java.util.*;

public class Duel extends Occupation {

    private @Getter @Setter PracticeQueue.Type queueType;

    public Duel(PracticeModule module, UUID uuid) {
        super(module, uuid);
    }

    @Override
    public void start() {
        List<Arena> list = new ArrayList<>();
        if(getArena() == null) {
            for(Arena a : PracticeModule.INSTANCE.getArenaManager().getArenas().values()) {
                if(a.isEnabled()) {
                    if(a.getType().equals(Kit.Type.NORMAL)) {
                        list.add(a);
                    }
                }
            }

            if(list.isEmpty()) {
                for(Player p : getAlivePlayers()) {
                    Profile profile = PracticeModule.INSTANCE.getProfileManager().get(p.getUniqueId());
                    p.sendMessage(ChatColor.RED + "There are no arenas currently available for the ladder selected. Please notify a staff member.");
                    profile.setOccupation(null);
                    profile.playerUpdate();
                }
                return;
            } else {
                Collections.shuffle(list);
                this.setArena(list.get(0));
            }
        }

        this.setState(State.STARTING);

        StringBuilder stringBuilder = new StringBuilder();
        List<Player> players = new ArrayList<>(this.getAlivePlayers());
        int s = 0;
        while(s != this.getAlive().size()) {
            Player p = players.get(0);
            stringBuilder.append(ChatColor.WHITE + p.getName());

            players.remove(p);
            s++;
            if(s == this.getAlivePlayers().size()) {
                stringBuilder.append(ChatColor.GRAY + ".");
            } else {
                stringBuilder.append(ChatColor.GRAY + ", ");
            }
        }

        for(Player p : this.getAlivePlayers()) {
            p.sendMessage(" ");
            p.sendMessage(Colors.get("&b&lMatch starting in 5 seconds."));
            p.sendMessage(Colors.get(" &7● &bMode: &f" + getQueueType().toString()));
            p.sendMessage(Colors.get(" &7● &bMap: &f" + Colors.get(getArena().getDisplayName())));
            p.sendMessage(Colors.get(" &7● &bParticipants: &f" + stringBuilder.toString()));
            p.sendMessage(" ");
        }

        Map<Player, Location> locations = new HashMap<>();

        int position = 1;
        for(Map.Entry<UUID, Participant> entry : this.getParticipants().entrySet()) {
            Participant participant = entry.getValue();
            Player p = Bukkit.getPlayer(entry.getKey());
            Location location = null;
            if(position == 1) {
                location = getArena().getPos1();
            }

            if(position == 2) {
                location = getArena().getPos2();
            }

            locations.put(p, location);

            if(p != null) {
                Profile profile = PracticeModule.INSTANCE.getProfileManager().get(p.getUniqueId());
                profile.playerUpdateVisibility();
                profile.playerReset();
                p.teleport(locations.get(p));
                boolean b = false;
                Map<Integer, CustomKit> customKits = profile.getCustomKits().get(getKit().getUuid());
                if(customKits != null) {
                    for(Map.Entry<Integer, CustomKit> e : customKits.entrySet()) {
                        ItemStack item = new ItemBuilder(Material.ENCHANTED_BOOK, ChatColor.WHITE + Colors.get(e.getValue().getName())).create();
                        p.getInventory().setItem(e.getKey() - 1, item);
                        b = true;
                    }
                }

                if(b) {
                    ItemStack item = new ItemBuilder(Material.BOOK, Colors.get("&aDefault Kit")).create();
                    p.getInventory().setItem(8, item);
                } else {
                    getKit().apply(p);
                    participant.setKitApplied(true);
                }
            }
            position++;
        }

        new BukkitRunnable() {
            int i = 5;
            public void run() {
                if(Duel.this.getState().equals(Occupation.State.ENDED)) {
                    cancel();
                }

                if (i == 0) {
                    for(Player p : Duel.this.getAlivePlayers()) {
                        if(p != null) {
                            p.playSound(p.getLocation(), Sound.NOTE_PIANO, 1, 1);
                            p.sendMessage(ChatColor.GREEN + "The game has started, good luck!");
                        }
                    }

                    Duel duel = Duel.this;
                    duel.setStarted(new Date());
                    duel.setState(State.ACTIVE);

                    cancel();
                } else {
                    if (i > 0) {
                        for (Player p : Duel.this.getAlivePlayers()) {
                            p.playSound(p.getLocation(), Sound.CLICK, 1, 1);
                            p.sendMessage(ChatColor.GREEN.toString() + i + "...");
                        }
                    }

                    i -= 1;
                }
            }
        }.runTaskTimer(PracticeModule.INSTANCE.getPlugin(), 20, 20);
    }

    @Override
    public void end() {
        ProfileManager pm = PracticeModule.INSTANCE.getProfileManager();
        this.setEnded(new Date());
        this.setState(Occupation.State.ENDED);

        String winner = null;
        String loser = null;
        Profile winnerProfile = null;
        Profile loserProfile = null;

        for(Map.Entry<UUID, Participant> entry : this.getParticipants().entrySet()) {
            Participant participant = entry.getValue();
            if(participant.isAlive()) {
                winner = participant.getName();
                winnerProfile = pm.get(entry.getKey());
                participant.setGameInventory(new GameInventory(participant));
            } else {
                loser = participant.getName();
                loserProfile = pm.get(entry.getKey());
            }
        }

        if(winnerProfile != null) {
            winnerProfile.getCosmetics().getWinAnimation().runAnimation(winnerProfile.getPlayer(), this);
        }

        List<TextComponent> components = new ArrayList<>();
        for(Participant p : this.getParticipants().values()) {
            TextComponent text = new TextComponent(ChatColor.WHITE + p.getName());
            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inventory " + p.getGameInventory().getUuid()));
            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + "Click to view " + ChatColor.WHITE + p.getName() + "'s " + ChatColor.GREEN + "inventory.").create()));
            components.add(text);

            for(Participant p2 : this.getParticipants().values()) {
                Profile profile = pm.get(p2.getUuid());
                PreviousMatch previousMatch = new PreviousMatch(profile, p.getUuid(), p.getName(), getKit(), getArena());
                profile.setPreviousMatch(previousMatch);
                if(p2 != p) {
                    p.getGameInventory().setOpponentInventory(p2.getGameInventory());
                    break;
                }
            }
        }

        // Match Summary Message
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" ");
        stringBuilder.append(Colors.get("&b&lMatch ended."));
        stringBuilder.append(Colors.get("\n &7● &bWinner: &f" + winner));

        TextComponent text = new TextComponent(ChatColor.GRAY + " ● " + ChatColor.AQUA + "Inventories: ");
        final int size = components.size();
        int x = 0;
        while(x != size) {
            TextComponent t = components.get(0);
            text.addExtra(t);
            components.remove(t);
            x++;
            if(x == size) {
                text.addExtra(new TextComponent(ChatColor.GRAY + "."));
            } else {
                text.addExtra(new TextComponent(ChatColor.GRAY + ", "));
            }
        }

//        switch (this.getType()) {
//            case UNRANKED:
//                winnerProfile.setUnrankedWins(winnerProfile.getUnrankedWins() + 1);
//                loserProfile.setUnrankedLosses(loserProfile.getUnrankedLosses() + 1);
//
//                break;
//            case RANKED:
//                winnerProfile.setRankedWins(winnerProfile.getRankedWins() + 1);
//                loserProfile.setRankedLosses(loserProfile.getRankedLosses() + 1);
//
//                double winnerElo = winnerProfile.getKitElo(this.getKit());
//                double loserElo = loserProfile.getKitElo(this.getKit());
//
//                // before it was 10                            ||
//                double multiplier = Math.pow(1 / (1 + Math.pow(24, (winnerElo - loserElo) / 400)), 2);
//
//                double difference = 50 * multiplier;
//                int newWinnerElo = (int) Math.round(winnerElo + difference);
//                int newLoserElo = (int) Math.round(loserElo - difference);
//
//                winnerProfile.setKitElo(this.getKit(), newWinnerElo);
//                loserProfile.setKitElo(this.getKit(), newLoserElo);
//
//                stringBuilder.append(Colors.get("\n &7● &bELO Changes: &f" + winnerProfile.getName() + " &7- &f" + newWinnerElo + " ELO &7(+" + Math.round(difference) + ")" +
//                        "&7, &f" + loserProfile.getName() + " &7- &f" + newLoserElo + " ELO &7(-" + Math.round(difference) + ")"));
//                break;
//            case TOURNAMENT:
//                if(Practice.instance.getTournament() != null) {
//                    Practice.instance.getTournament().leave(Bukkit.getPlayer(loser));
//                }
//                break;
//        }

        for(Player player : this.getAllPlayers()) {
            player.sendMessage(" ");
            player.sendMessage(stringBuilder.toString());
            player.spigot().sendMessage(text);
            player.sendMessage(" ");
        }

        Bukkit.getScheduler().runTaskLater(PracticeModule.INSTANCE.getPlugin(), () -> {
            for(Map.Entry<UUID, Participant> entry : this.getParticipants().entrySet()) {
                Player player = Bukkit.getPlayer(entry.getKey());
                Profile profile = pm.get(entry.getKey());
                if(player != null) {
                    if(entry.getValue().isAlive()) {
                        for(Cooldown cooldown : pm.get(player.getUniqueId()).getCooldowns().values()) {
                            cooldown.remove();
                        }

                        profile.setOccupation(null);
                        profile.playerUpdate();
                    }
                }
            }

            Set<UUID> spectators = new HashSet<>(this.getSpectators().keySet());
            for(UUID uuid : spectators) {
                Player player = Bukkit.getPlayer(uuid);
                this.spectateEnd(player);
            }

            this.getSpectators().clear();

            for(Entity entity: getEntities()) {
                entity.remove();
            }

            for(Block block : this.getPlacedBlocks()) {
                block.setType(Material.AIR);
            }

            for(BrokenBlock block : this.getBrokenBlocks()) {
                Block b = block.getBlock();
                b.setType(block.getMaterial());
                b.setData(block.getData());
            }

//            if(this.getKit().getType().equals(Kit.Type.BUILD)) {
//                this.getArena().setInUse(false);
//            }

            this.setState(State.STOPPED);
        }, 60);
    }

    @Override
    public void forceEnd() {
        // TODO: Force end games.
    }

    @Override
    public List<String> getScoreboard(Profile profile) {
        List<String> lines = new ArrayList<>();
        Player opponent = null;
        for(Player player : getCurrentPlaying()) {
            if(player.getUniqueId() != profile.getUuid()) {
                opponent = player;
            }
        }
        switch(this.getState()) {
            case STARTING:
                lines.add("&bArena: &f" + Colors.get(getArena().getDisplayName()));
                lines.add("&bQueue: &f" + getQueueType().toString());
                lines.add("&bKit: &f" + Colors.get(getKit().getDisplayName()));
                if(opponent != null) {
                    lines.add("&bEnemy: &f" + opponent.getName());
                }
                break;
            case ACTIVE:
                lines.add("&bYour Ping: " + PlayerUtils.getPing(profile.getPlayer()) + " ms");
                if(opponent != null) {
                    lines.add("&bEnemy Ping: " + PlayerUtils.getPing(opponent) + " ms");
                }
                break;
            case ENDED:
                lines.add("&bDuration: &f" + TimeUtil.get(getEnded(), getStarted()));

                Participant winner = new ArrayList<>(getAlive().values()).get(0);

                if(winner != null) {
                    lines.add("&bWinner: &f" + winner.getName());
                }
                break;
        }
        return lines;
    }

    @Override
    public List<String> getSpectatorScoreboard(Profile profile) {
        List<String> lines = new ArrayList<>();
        Spectator spectator = getSpectators().get(profile.getUuid());
        if(spectator != null) {
            switch (this.getState()) {
                case STARTING:
                    lines.add("&bPlayers:");
                    for (Participant participant : getParticipants().values()) {
                        if (participant.isAlive()) {
                            lines.add(" &a" + participant.getName());
                        } else {
                            lines.add(" &c&m" + participant.getName());
                        }
                    }
                    break;
                case ACTIVE:
                    lines.add("&bDuration: &f" + TimeUtil.get(new Date(), getStarted()));
                    if(spectator.getTarget() != null) {
                        lines.add("&bWatching: &f" + spectator.getTarget().getName());
                    }
                    lines.add("&bPlayers:");
                    for (Participant participant : getParticipants().values()) {
                        if (participant.isAlive()) {
                            lines.add(" &7- &a" + participant.getName());
                        } else {
                            lines.add(" &7- &c&m" + participant.getName());
                        }
                    }
                    break;
                case ENDED:
                    lines.add("&bDuration: &f" + TimeUtil.get(getEnded(), getStarted()));

                    Participant winner = new ArrayList<>(getAlive().values()).get(0);
                    if (winner != null) {
                        lines.add("&bWinner: &f" + winner.getName());
                    }
                    break;
            }
        }
        return lines;
    }

    @Override
    public void eliminate(Player player) {
        super.eliminate(player);
        if(this.getAlive().size() < 2) {
            this.end();
        }
    }
}
