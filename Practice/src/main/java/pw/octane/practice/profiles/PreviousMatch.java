package pw.octane.practice.profiles;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.arenas.Arena;
import pw.octane.practice.kits.Kit;

import java.util.UUID;

public @Data class PreviousMatch {

    private final Profile profile;
    private final UUID uuid;
    private final String name;
    private final Kit kit;
    private final Arena arena;
    private boolean expired;
    private BukkitTask task;
    public PreviousMatch(Profile profile, UUID uuid, String name, Kit kit, Arena arena) {
        this.profile = profile;
        this.uuid = uuid;
        this.name = name;
        this.kit = kit;
        this.arena = arena;
        this.expired = false;

        this.task = Bukkit.getScheduler().runTaskLater(PracticeModule.INSTANCE.getPlugin(), ()-> {
            expired = true;
            if(profile.getPreviousMatch().equals(this)) {
                if (profile.getState().equals(Profile.State.LOBBY)) {
                    profile.playerItems();
                }
            }
        }, 400);
    }

    public void terminate() {
        task.cancel();
    }
}
