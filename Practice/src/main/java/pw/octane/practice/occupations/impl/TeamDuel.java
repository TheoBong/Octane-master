package pw.octane.practice.occupations.impl;

import pw.octane.practice.PracticeModule;
import pw.octane.practice.occupations.Occupation;
import pw.octane.practice.profiles.Profile;

import java.util.List;
import java.util.UUID;

public class TeamDuel extends Occupation {

    public TeamDuel(PracticeModule module, UUID uuid) {
        super(module, uuid);
    }

    @Override
    public void start() {

    }

    @Override
    public void end() {

    }

    @Override
    public void forceEnd() {

    }

    @Override
    public List<String> getScoreboard(Profile profile) {
        return null;
    }

    @Override
    public List<String> getSpectatorScoreboard(Profile profile) {
        return null;
    }
}
