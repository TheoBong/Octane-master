package pw.octane.practice.occupations.impl.events;

import pw.octane.practice.PracticeModule;
import pw.octane.practice.occupations.Occupation;

import java.util.UUID;

public abstract class GameEvent extends Occupation {

    public GameEvent(PracticeModule module, UUID uuid) {
        super(module, uuid);
    }
}
