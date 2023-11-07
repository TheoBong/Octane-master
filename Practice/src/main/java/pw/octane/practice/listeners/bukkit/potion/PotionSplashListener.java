package pw.octane.practice.listeners.bukkit.potion;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.occupations.Occupation;
import pw.octane.practice.occupations.Participant;
import pw.octane.practice.profiles.Profile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PotionSplashListener implements Listener {

    private PracticeModule module;
    public PotionSplashListener(PracticeModule plugin) {
        this.module = plugin;
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        ThrownPotion thrownPotion = event.getPotion();
        List<LivingEntity> remove = new ArrayList<>();
        if(thrownPotion.getShooter() instanceof Player) {
            Player player = (Player) thrownPotion.getShooter();
            Profile profile = module.getProfileManager().get(player.getUniqueId());
            Occupation occupation = profile.getOccupation();
            if(occupation != null) {
                if(occupation.getParticipants().containsKey(player.getUniqueId())) {
                    Participant participant = occupation.getParticipants().get(player.getUniqueId());
                    participant.thrownPotions++;
                    if(event.getIntensity(player) < 0.4) {
                        participant.missedPotions++;
                    }
                }
            }

            for(LivingEntity entity : event.getAffectedEntities()) {
                if(entity instanceof Player) {
                    Player p = (Player) entity;
                    Profile pr = module.getProfileManager().get(p.getUniqueId());;
                    Occupation o = pr.getOccupation();

                    if(!p.canSee(player)) {
                        remove.add(entity);
                    }

                    if(o != null) {
                        if(!o.getParticipants().containsKey(p.getUniqueId())) {
                            remove.add(entity);
                        }
                    } else {
                        remove.add(entity);
                    }
                }
            }

            for(LivingEntity entity : remove) {
                event.setIntensity(entity, 0);
            }

            PacketListener particleListener = new PacketAdapter(module.getPlugin(), PacketType.Play.Server.WORLD_EVENT) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    Player p = event.getPlayer();
                    event.setCancelled(!p.canSee(player));
                }
            };
            module.getProtocolManager().addPacketListener(particleListener);
            Bukkit.getScheduler().scheduleSyncDelayedTask(module.getPlugin(),
                    () -> module.getProtocolManager().removePacketListener(particleListener), 2L);
        }
    }
}
