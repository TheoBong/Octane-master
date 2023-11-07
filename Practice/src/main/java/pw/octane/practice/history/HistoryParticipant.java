package pw.octane.practice.history;

import pw.octane.practice.queues.PracticeQueue;

import java.util.Date;
import java.util.UUID;

public class HistoryParticipant {

    private final UUID uuid;
    private String name;
    private UUID kit;
    private PracticeQueue.Type type;
    private int eloBefore, eloAfter;

    public HistoryParticipant(UUID uuid) {
        this.uuid = uuid;
    }

    public boolean isWinner() {
        return eloAfter > eloBefore;
    }
}
