package pw.octane.practice.sidebar;

import io.github.thatkawaiisam.assemble.AssembleAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pw.octane.manager.utils.Colors;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.occupations.Occupation;
import pw.octane.practice.profiles.Profile;
import pw.octane.practice.queues.PracticeQueue;
import pw.octane.practice.queues.QueueMember;

import java.util.ArrayList;
import java.util.List;

public class SidebarAdapter implements AssembleAdapter {

    private PracticeModule module;
    public SidebarAdapter(PracticeModule module) {
        this.module = module;
    }

    @Override
    public String getTitle(Player player) {
        return "&b&lPractice";
    }

    @Override
    public List<String> getLines(Player player) {
        List<String> lines = new ArrayList<>();
        Profile profile = module.getProfileManager().get(player.getUniqueId());

        if(profile != null && !profile.getSettings().getSidebarView().equals(SidebarView.NONE)) {
            lines.add("&7&m------------------");

            switch (profile.getState()) {
                case EVENT:
                case IN_GAME:
                    Occupation occupation = profile.getOccupation();
                    lines.addAll(occupation.getScoreboard(profile));
                    break;
                case SPECTATING:
                    occupation = profile.getOccupation();
                    lines.addAll(occupation.getSpectatorScoreboard(profile));
                    break;
                default:
                    lines.add("&bOnline: &f" + Bukkit.getOnlinePlayers().size());
                    lines.add("&bPlaying: &f" + module.getOccupationManager().getInGame());
                    switch (profile.getState()) {
                        case QUEUE:
                            QueueMember qm = profile.getQueue();
                            PracticeQueue queue = qm.getQueue();
                            lines.add("&7&m------------------");
                            lines.add("&b" + queue.getType().toString() + " Queue");
                            lines.add("&bIn Queue: &f" + module.getQueueManager().getInQueue(queue.getKit(), queue.getType()));
                            lines.add("&bLadder: " + queue.getKit().getDisplayName());
                            break;
                        case PARTY:
                            lines.add("&7&m------------------");
                            lines.add("&f&oComing soon!");
                            break;
                        case TOURNAMENT:
                            lines.add("&7&m------------------");
                            lines.add("&f&oComing soon!");
                            break;
                        case FOLLOWING:
                            lines.add("&7&m------------------");
                            lines.add("&bFollowing: ");
                            lines.add("&f" + Bukkit.getPlayer(profile.getFollowing()).getName());
                    }
            }

            lines.add(" ");
            lines.add("&boctane.pw");
            lines.add("&7&m------------------");

            return lines;
        } else {
            return null;
        }
    }
}
