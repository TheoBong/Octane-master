package pw.octane.practice.listeners.bukkit.entity;

import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.occupations.Occupation;
import pw.octane.practice.profiles.Profile;

public class EntityDamageByEntityListener implements Listener {

    private PracticeModule module;
    public EntityDamageByEntityListener(PracticeModule module) {
        this.module = module;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Player player = null;
        Player attacker = null;
        boolean b = false;
        if(event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            player = (Player) event.getEntity();
            attacker = (Player) event.getDamager();
        }

        if(event.getDamager() instanceof Arrow && event.getEntity() instanceof Player) {
            Arrow arrow = (Arrow) event.getDamager();
            player = (Player) event.getEntity();
            if(arrow.getShooter() instanceof Player) {
                attacker = (Player) arrow.getShooter();
                attacker.sendMessage(ChatColor.WHITE + player.getName() + ChatColor.GREEN + " is now at " + ChatColor.WHITE + Math.round(player.getHealth()) + " HP" + ChatColor.GREEN + ".");
                b = true;
            }
        }

        if(player != null && attacker != null) {
            Profile playerProfile = module.getProfileManager().get(player.getUniqueId());
            Profile attackerProfile = module.getProfileManager().get(attacker.getUniqueId());
            Occupation occupation = attackerProfile.getOccupation();
            if(occupation != null && playerProfile.getOccupation() != null) {
                occupation.handleHit(player, attacker, event);
            } else {
                event.setCancelled(true);
            }
        }
    }
}
