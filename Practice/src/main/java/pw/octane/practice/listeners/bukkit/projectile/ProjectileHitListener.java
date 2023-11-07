package pw.octane.practice.listeners.bukkit.projectile;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.occupations.Occupation;
import pw.octane.practice.profiles.Profile;
import pw.octane.practice.utils.EntityHider;

public class ProjectileHitListener implements Listener {

    private PracticeModule module;
    private EntityHider entityHider;
    public ProjectileHitListener(PracticeModule plugin) {
        this.module = plugin;
        this.entityHider = plugin.getEntityHider();
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if(event.getEntity().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();
            Profile profile = module.getProfileManager().get(player.getUniqueId());
            Occupation occupation = profile.getOccupation();
            if(occupation != null) {
                occupation.addEntity(event.getEntity());
            }

            PacketListener particleListener = new PacketAdapter(module.getPlugin(), PacketType.Play.Server.NAMED_SOUND_EFFECT) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    PacketContainer packet = event.getPacket();
                    Player p = event.getPlayer();
                    String sound = packet.getStrings().read(0);
                    if(sound.equalsIgnoreCase("random.bowhit")) {
                        event.setCancelled(!p.canSee(player));
                    }
                }
            };
            module.getProtocolManager().addPacketListener(particleListener);
            Bukkit.getScheduler().scheduleSyncDelayedTask(module.getPlugin(),
                    () -> module.getProtocolManager().removePacketListener(particleListener), 2L);
        }
    }
}
