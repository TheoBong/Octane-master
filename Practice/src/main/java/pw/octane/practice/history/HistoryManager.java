package pw.octane.practice.history;

import lombok.Getter;
import pw.octane.practice.PracticeModule;

import java.util.Map;
import java.util.UUID;

public class HistoryManager {

    private PracticeModule module;
    private @Getter Map<UUID, HistoryMatch> matches;
    public HistoryManager(PracticeModule module) {
        this.module = module;
    }
}
