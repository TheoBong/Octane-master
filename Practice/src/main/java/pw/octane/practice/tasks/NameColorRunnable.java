package pw.octane.practice.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import pw.octane.manager.utils.Colors;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.occupations.Spectator;
import pw.octane.practice.occupations.tournaments.Tournament;
import pw.octane.practice.parties.Party;
import pw.octane.practice.profiles.Profile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NameColorRunnable implements Runnable{

    private PracticeModule module;
    private List<String> teams;
    public NameColorRunnable(PracticeModule module) {
        this.module = module;
        this.teams = Arrays.asList("playing", "enemies", "friendly", "spectators", "lobby", "party", "tournament");
    }

    @Override
    public void run() {
        for(Profile profile : module.getProfileManager().getProfiles().values()) {
            Player player = profile.getPlayer();
            if(player != null && player.isOnline()) {
                Scoreboard scoreboard = player.getScoreboard();
                for(Team team : scoreboard.getTeams()) {
                    if(teams.contains(team.getName())) {
                        List<String> entries = new ArrayList<>(team.getEntries());
                        for (String s : entries) {
                            team.removeEntry(s);
                        }
                    }
                }

                Team playingTeam = scoreboard.getTeam("playing");
                Team enemyTeam = scoreboard.getTeam("enemies");
                Team friendlyTeam = scoreboard.getTeam("friendly");
                Team spectatorTeam = scoreboard.getTeam("spectators");
                Team lobbyTeam = scoreboard.getTeam("lobby");
                Team partyTeam = scoreboard.getTeam("party");
                Team tournamentTeam = scoreboard.getTeam("tournament");

                if(playingTeam == null) {
                    playingTeam = scoreboard.registerNewTeam("playing");
                    playingTeam.setPrefix(Colors.get("&e"));
                }

                if(enemyTeam == null) {
                    enemyTeam = scoreboard.registerNewTeam("enemies");
                    enemyTeam.setPrefix(Colors.get("&c"));
                }

                if(friendlyTeam == null) {
                    friendlyTeam = scoreboard.registerNewTeam("friendly");
                    friendlyTeam.setPrefix(Colors.get("&a"));
                    friendlyTeam.setNameTagVisibility(NameTagVisibility.ALWAYS);
                    friendlyTeam.setAllowFriendlyFire(false);
                    friendlyTeam.setCanSeeFriendlyInvisibles(true);
                }

                if(spectatorTeam == null) {
                    spectatorTeam = scoreboard.registerNewTeam("spectators");
                    spectatorTeam.setPrefix(Colors.get("&7&o"));
                    spectatorTeam.setNameTagVisibility(NameTagVisibility.ALWAYS);
                    spectatorTeam.setCanSeeFriendlyInvisibles(true);
                }

                if(lobbyTeam == null) {
                    lobbyTeam = scoreboard.registerNewTeam("lobby");
                    lobbyTeam.setPrefix(Colors.get("&b"));
                }

                if(partyTeam == null) {
                    partyTeam = scoreboard.registerNewTeam("party");
                    partyTeam.setPrefix(Colors.get("&b[Party] &f"));
                }

                if(tournamentTeam == null) {
                    tournamentTeam = scoreboard.registerNewTeam("tournament");
                    tournamentTeam.setPrefix(Colors.get("&6&l* &r&6"));
                }

                if (profile.getOccupation() != null) {
                    if(profile.getState().equals(Profile.State.SPECTATING)) {
                        Spectator spectator = profile.getOccupation().getSpectators().get(player.getUniqueId());
                        for (Player p : profile.getOccupation().getAlivePlayers()) {
                            if(spectator != null && spectator.getTarget() != null && !spectator.getTarget().equals(p)) {
                                enemyTeam.addEntry(p.getName());
                            } else {
                                playingTeam.addEntry(p.getName());
                            }
                        }
                    } else {
                        for (Player p : profile.getOccupation().getAlivePlayers()) {
                            if(!p.equals(player)) {
                                enemyTeam.addEntry(p.getName());
                            }
                        }
                    }

                    for (Player p : profile.getOccupation().getSpectatorsPlayers()) {
                        spectatorTeam.addEntry(p.getName());
                    }
                } else {
                    Party party = profile.getParty();
                    Tournament tournament = profile.getTournament();
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if(party != null && party.getMembers().containsKey(p)) {
                            partyTeam.addEntry(p.getName());
                        } else {
                            lobbyTeam.addEntry(p.getName());
                        }
                    }
                }
            }
        }
    }
}
