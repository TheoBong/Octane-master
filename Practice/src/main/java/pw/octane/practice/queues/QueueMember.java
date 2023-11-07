package pw.octane.practice.queues;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import pw.octane.manager.utils.Colors;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.kits.Kit;
import pw.octane.practice.profiles.Profile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class QueueMember {

    private @Getter PracticeQueue queue;
    private final UUID uuid;
    private final int elo;
    private int low, high;
    private BukkitTask task;

    public QueueMember(PracticeQueue queue, UUID uuid) {
        this(queue, uuid, 0);
    }

    public QueueMember(PracticeQueue queue, UUID uuid, int elo) {
        this.queue = queue;
        this.uuid = uuid;
        this.elo = elo;

        if(elo != 0) {
            task = Bukkit.getScheduler().runTaskTimerAsynchronously(PracticeModule.INSTANCE.getPlugin(), () -> {
                low += 10;
                high += 10;
                message(ChatColor.GREEN + "Now searching in ELO range " + ChatColor.WHITE + getLow() + " ELO -> " + getHigh() + " ELO" + ChatColor.GREEN + ".");
            }, 100, 40);
        }
    }

    public int getHigh() {
        if(elo + high >= 2500) {
            return 3000;
        }
        return elo + high;
    }

    public int getLow() {
        if(elo + low < 250) {
            return 0;
        }
        return elo + low;
    }

    public List<Player> getPlayers() {
        Player player = Bukkit.getPlayer(uuid);
        if(player != null) {
            return Collections.singletonList(player);
        } else {
            // TODO: Party queues
            return null;
        }
    }

    public void message(String s) {
        for(Player player : getPlayers()) {
            player.sendMessage(Colors.get(s));
        }
    }

    public void leave() {
        Kit kit = queue.getKit();
        queue.getQueueMembers().remove(this);
        message("&aYou left the queue for &f" + kit.getColor() + queue.getType().toString() + " " + kit.getDisplayName() + "&a.");
        terminate();

        for(Player player : getPlayers()) {
            Profile profile = PracticeModule.INSTANCE.getProfileManager().get(player.getUniqueId());
            profile.playerUpdate();
        }
    }

    public void terminate() {
        if(task != null) {
            task.cancel();
        }
    }
}
