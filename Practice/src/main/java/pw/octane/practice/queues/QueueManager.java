package pw.octane.practice.queues;

import lombok.Getter;
import org.bukkit.entity.Player;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.kits.Kit;

import java.util.*;

public class QueueManager {

    private PracticeModule module;
    private @Getter Set<PracticeQueue> practiceQueues;
    public QueueManager(PracticeModule module) {
        this.module = module;
        this.practiceQueues = new HashSet<>();
    }

    public QueueMember find(UUID uuid) {
        for(PracticeQueue queue : practiceQueues) {
            for(QueueMember qm : queue.getQueueMembers()) {
                for(Player player : qm.getPlayers()) {
                    if(player.getUniqueId().equals(uuid)) {
                        return qm;
                    }
                }
            }
        }

        return null;
    }

    public int getInQueue(Kit kit, PracticeQueue.Type type) {
        for(PracticeQueue queue : practiceQueues) {
            boolean b = false;
            if(kit != null) {
                if(queue.getKit().equals(kit)) {
                    b = true;
                }
            } else {
                b = true;
            }

            if(type != null) {
                if(queue.getType().equals(type)) {
                    b = true;
                }
            } else {
                b = true;
            }

            if(b) {
                return queue.size();
            }
        }

        return -1;
    }

    public void refresh() {
        for(PracticeQueue queue : practiceQueues) {
            queue.terminate();
        }

        practiceQueues.clear();

        for(Kit kit : module.getKitManager().getKits().values()) {
            if(kit.isQueueable()) {
                PracticeQueue unranked, ranked, unranked2v2, ranked2v2;
                unranked = new PracticeQueue(module, kit, PracticeQueue.Type.UNRANKED);
                practiceQueues.add(unranked);

                if(kit.isRanked()) {
                    ranked = new PracticeQueue(module, kit, PracticeQueue.Type.RANKED);
                    practiceQueues.add(ranked);
                }

                if(kit.isAllow2v2()) {
                    unranked2v2 = new PracticeQueue(module, kit, PracticeQueue.Type.UNRANKED2V2);
                    practiceQueues.add(unranked2v2);
                    if(kit.isRanked()) {
                        ranked2v2 = new PracticeQueue(module, kit, PracticeQueue.Type.RANKED2V2);
                        practiceQueues.add(ranked2v2);
                    }
                }
            }
        }
    }
}
