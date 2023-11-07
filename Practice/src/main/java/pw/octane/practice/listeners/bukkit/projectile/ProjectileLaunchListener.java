package pw.octane.practice.listeners.bukkit.projectile;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.occupations.Occupation;
import pw.octane.practice.profiles.Cooldown;
import pw.octane.practice.profiles.Profile;
import pw.octane.practice.utils.EntityHider;

public class ProjectileLaunchListener implements Listener {

    private PracticeModule module;
    private EntityHider entityHider;
    public ProjectileLaunchListener(PracticeModule plugin) {
        this.module = plugin;
        this.entityHider = plugin.getEntityHider();
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if(event.getEntity().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();
            Profile profile = module.getProfileManager().get(player.getUniqueId());
            Occupation occupation = profile.getOccupation();
            for(Player p : Bukkit.getOnlinePlayers()) {
                if(!p.canSee(player)) {
                    entityHider.hideEntity(p, event.getEntity());
                }
            }

            if(occupation != null) {
                occupation.addEntity(event.getEntity());
                if(event.getEntity() instanceof EnderPearl) {
                    Cooldown cooldown = profile.getCooldowns().get(Cooldown.Type.ENDER_PEARL);
                    if(profile.getOccupation().getCurrentPlaying().contains(player)) {
                        if(profile.getOccupation().getState().equals(Occupation.State.ACTIVE)) {
                            if (cooldown != null && !cooldown.isExpired()) {
                                event.setCancelled(true);
                            } else {
                                cooldown = new Cooldown(Cooldown.Type.ENDER_PEARL, profile);
                                profile.getCooldowns().put(Cooldown.Type.ENDER_PEARL, cooldown);
                            }
                        } else {
                            event.setCancelled(true);
                            player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
                        }
                    }
                }
            }
        }
    }
}
