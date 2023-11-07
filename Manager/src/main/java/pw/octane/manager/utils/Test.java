package pw.octane.manager.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Test {

    @Override
    public void enable(ProtocolManager protocolManager, Plugin plugin) {
        protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.BLOCK_CHANGE) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player player = event.getPlayer();
                PacketContainer packet = event.getPacket();

                CompletableFuture<Boolean> cancel = isCancel(packet, player);

                try {
                    if (cancel.get()) event.setCancelled(true);
                } catch (InterruptedException | ExecutionException e) { e.printStackTrace(); }
            }
        });
    }

    private CompletableFuture<Boolean> isCancel(PacketContainer packet, Player player) {
        return CompletableFuture.supplyAsync(() -> {
            for (BlockPosition pos : packet.getBlockPositionModifier().getValues()) {
                Location location = new Location(player.getWorld(), pos.getX(), pos.getY(), pos.getZ());
                VisualBlock visualBlock = VisualModule.this.getVisualBlockAt(event.getPlayer(), location);

                if (visualBlock == null) return;
                return true;
            }

            return false;
        });
    }
}
