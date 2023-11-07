package pw.octane.practice.history;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HistoryMatch {

    private final UUID uuid;
    private Map<UUID, HistoryParticipant> participants;
    private Date date;

    public HistoryMatch(UUID uuid) {
        this.uuid = uuid;
        this.participants = new HashMap<>();
    }
}
