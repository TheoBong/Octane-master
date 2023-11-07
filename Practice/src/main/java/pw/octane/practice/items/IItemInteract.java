package pw.octane.practice.items;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.profiles.Profile;

public interface IItemInteract {
    void onInteract(PracticeModule module, Player player, Profile profile, PlayerInteractEvent event);
}
