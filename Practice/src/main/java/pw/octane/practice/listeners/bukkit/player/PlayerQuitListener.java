package pw.octane.practice.listeners.bukkit.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.occupations.Occupation;
import pw.octane.practice.occupations.tournaments.Tournament;
import pw.octane.practice.parties.Party;
import pw.octane.practice.profiles.Profile;
import pw.octane.practice.queues.QueueMember;

public class PlayerQuitListener implements Listener {

    private PracticeModule module;
    public PlayerQuitListener(PracticeModule module) {
        this.module = module;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Profile profile = module.getProfileManager().get(player.getUniqueId());

        if(profile != null) {
            Occupation occupation = profile.getOccupation();
            Party party = profile.getParty();
            Tournament tournament = profile.getTournament();
            QueueMember queueMember = profile.getQueue();

            if (profile.getOccupation() != null) {
                occupation.leave(player);
            }

            if (queueMember != null) {
                queueMember.leave();
            }

            if (profile.getPreviousMatch() != null) {
                profile.getPreviousMatch().terminate();
            }

            module.getProfileManager().push(true, profile, true);
        }
    }
}
