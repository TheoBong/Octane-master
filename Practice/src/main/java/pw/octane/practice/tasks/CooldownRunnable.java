package pw.octane.practice.tasks;

import pw.octane.practice.PracticeModule;
import pw.octane.practice.profiles.Cooldown;
import pw.octane.practice.profiles.Profile;

import java.util.HashMap;
import java.util.Map;

public class CooldownRunnable implements Runnable {

    private PracticeModule module;
    public CooldownRunnable(PracticeModule module) {
        this.module = module;
    }

    @Override
    public void run() {
        for(Profile profile : module.getProfileManager().getProfiles().values()) {
            Map<Cooldown.Type, Cooldown> cooldowns = new HashMap<>(profile.getCooldowns());
            for(Cooldown cooldown : cooldowns.values()) {
                cooldown.check();
            }
        }
    }
}
