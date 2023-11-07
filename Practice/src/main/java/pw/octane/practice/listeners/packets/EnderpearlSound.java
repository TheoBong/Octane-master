package pw.octane.practice.listeners.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.occupations.Occupation;
import pw.octane.practice.profiles.Cooldown;
import pw.octane.practice.profiles.Profile;

public class EnderpearlSound extends PacketAdapter {

    private PracticeModule module;

    public EnderpearlSound(PracticeModule module) {
        super(module.getPlugin(), PacketType.Play.Server.NAMED_SOUND_EFFECT);
        this.module = module;
    }

    @Override
    public void onPacketSending(PacketEvent e) {
        PacketContainer packet = e.getPacket();
        Player player = e.getPlayer();
        Location soundLocation = new Location(player.getWorld(), packet.getIntegers().read(0) / 8.0, packet.getIntegers().read(1) / 8.0, packet.getIntegers().read(2) / 8.0);
        String soundName = packet.getStrings().read(0);

        if(soundName.equalsIgnoreCase("random.bow")) {
            Profile profile = module.getProfileManager().get(player.getUniqueId());
            Player closest = null;
            double bestDistance = Double.MAX_VALUE;

            for (Player p : player.getWorld().getPlayers()) {
                if (p.getLocation().distance(soundLocation) < bestDistance) {
                    bestDistance = p.getLocation().distance(soundLocation);
                    closest = p;
                }
            }

            if (!player.canSee(closest)) {
                e.setCancelled(true);
            }

            if (player.getItemInHand().getType().equals(Material.ENDER_PEARL)) {
                Occupation occupation = profile.getOccupation();
                if(occupation != null && occupation.getState().equals(Occupation.State.ACTIVE)) {
                    Cooldown cooldown = profile.getCooldowns().get(Cooldown.Type.ENDER_PEARL);
                    if(cooldown != null) {
                        if(!cooldown.isExpired()) {
                            e.setCancelled(true);
                        }
                    }
                } else {
                    e.setCancelled(true);
                }
            }
        }
    }
}
