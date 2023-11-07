package pw.octane.practice.occupations.tournaments;

import lombok.Data;
import org.bukkit.entity.Player;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.kits.Kit;
import pw.octane.practice.occupations.Occupation;

import java.util.*;

public @Data class Tournament {

    public enum State {
        WAITING, STARTING, INGAME, ENDED;
    }

    private final UUID uuid;
    private PracticeModule module;
    private State state;
    private Kit kit;
    private List<Occupation> activeGames;
    private Map<UUID, TournamentParticipant> participants;
    private int round, minPlayers, maxPlayers, teamSize;

    public Tournament(PracticeModule module, UUID uuid) {
        this.module = module;
        this.uuid = uuid;
        this.activeGames = new ArrayList<>();
        this.participants = new HashMap<>();
        this.round = 0;
        this.minPlayers = 8;
        this.maxPlayers = 64;
        this.teamSize = 1;
    }

    public void join(Player player) {

    }

    public void leave(Player player) {

    }
}
