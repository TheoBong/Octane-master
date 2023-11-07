package pw.octane.practice.occupations;

import lombok.Data;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.kits.Kit;
import pw.octane.practice.occupations.impl.Duel;
import pw.octane.practice.occupations.tournaments.Tournament;
import pw.octane.practice.queues.PracticeQueue;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public @Data class OccupationManager {

    private PracticeModule module;
    private Tournament tournament;
    private Map<UUID, Occupation> occupations;
    public OccupationManager(PracticeModule module) {
        this.module = module;
        this.occupations = new HashMap<>();
    }

    public int getInGame() {
        int i = 0;
        for(Occupation occupation : occupations.values()) {
            if(!occupation.getState().equals(Occupation.State.ENDED) && !occupation.getState().equals(Occupation.State.STOPPED) && !occupation.getState().equals(Occupation.State.CREATED)) {
                i += occupation.getAllPlayers().size();
            }
        }

        return i;
    }

    public int getInGame(Kit kit, PracticeQueue.Type type) {
        int i = 0;
        for(Occupation occupation : occupations.values()) {
            if(!occupation.getState().equals(Occupation.State.ENDED) && !occupation.getState().equals(Occupation.State.STOPPED) && !occupation.getState().equals(Occupation.State.CREATED)) {
                if(occupation instanceof Duel) {
                    Duel duel = (Duel) occupation;
                    if(duel.getKit().equals(kit) && duel.getQueueType().equals(type)) {
                        i += duel.getAllPlayers().size();
                    }
                }
            }
        }

        return i;
    }
}
