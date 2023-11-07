package pw.octane.practice.queues;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.kits.Kit;
import pw.octane.practice.occupations.impl.Duel;
import pw.octane.practice.occupations.impl.TeamDuel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

public class PracticeQueue {

    public enum Type {
        UNRANKED, RANKED, PREMIUM, TOURNAMENT, UNRANKED2V2, RANKED2V2, PRIVATE;

        @Override
        public String toString() {
            switch(this) {
                case UNRANKED:
                    return "Unranked";
                case RANKED:
                    return "Ranked";
                case PREMIUM:
                    return "Premium";
                case TOURNAMENT:
                    return "Tournament";
                case UNRANKED2V2:
                    return "Unranked 2v2";
                case RANKED2V2:
                    return "Ranked 2v2";
                case PRIVATE:
                    return "Private";
                default:
                    return null;
            }
        }

        public boolean is2v2() {
            switch(this) {
                case UNRANKED2V2:
                case RANKED2V2:
                    return true;
                default:
                    return false;
            }
        }
    }

    private PracticeModule module;
    private @Getter Kit kit;
    private @Getter PracticeQueue.Type type;
    private @Getter Queue<QueueMember> queueMembers;
    private BukkitTask task;

    public PracticeQueue(PracticeModule module, Kit kit, PracticeQueue.Type type) {
        this.module = module;
        this.kit = kit;
        this.type = type;
        this.queueMembers = new LinkedList<>();

        task = Bukkit.getScheduler().runTaskTimer(module.getPlugin(), ()-> {
            if(queueMembers.size() > 1) {
                QueueMember qm1, qm2;
                qm1 = queueMembers.poll();
                qm2 = queueMembers.poll();

                if(type.is2v2()) {
                    TeamDuel teamDuel = new TeamDuel(module, UUID.randomUUID());
                    // start teams duel
                } else {
                    Duel duel = new Duel(module, UUID.randomUUID());
                    duel.join(qm1.getPlayers().get(0));
                    duel.join(qm2.getPlayers().get(0));
                    duel.setQueueType(type);
                    duel.setKit(kit);
                    duel.start();
                    PracticeModule.INSTANCE.getOccupationManager().getOccupations().put(duel.getUuid(), duel);
                    // start normal duel
                }
            }
        }, 1, 1);
    }

    public void join(QueueMember qm) {
        qm.message("&aYou have joined the " + kit.getColor() + type.toString() + " " + kit.getDisplayName() + " &aqueue.");
        queueMembers.add(qm);
    }

    public void leave(UUID uuid) {
        for(QueueMember qm : queueMembers) {
            for(Player player : qm.getPlayers()) {
                if(player.getUniqueId().equals(uuid)) {
                    qm.leave();
                }
            }
        }
    }

    public int size() {
        int i = 0;
        for(QueueMember qm : queueMembers) {
            i += qm.getPlayers().size();
        }

        return i;
    }

    public void terminate() {
        task.cancel();
        for(QueueMember qm : queueMembers) {
            qm.leave();
        }
    }
}
