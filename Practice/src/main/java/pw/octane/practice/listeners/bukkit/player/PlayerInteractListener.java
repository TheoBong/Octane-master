package pw.octane.practice.listeners.bukkit.player;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.*;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.items.InteractableItem;
import pw.octane.practice.kits.CustomKit;
import pw.octane.practice.occupations.Occupation;
import pw.octane.practice.occupations.Participant;
import pw.octane.practice.profiles.Cooldown;
import pw.octane.practice.profiles.Profile;

public class PlayerInteractListener implements Listener {

    private PracticeModule module;
    public PlayerInteractListener(PracticeModule module) {
        this.module = module;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = module.getProfileManager().get(player.getUniqueId());
        Action action = event.getAction();
        Block block = event.getClickedBlock();

        ItemStack item = event.getItem();

        if(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            InteractableItem ii = InteractableItem.getItem(profile, item, player.getInventory().getHeldItemSlot());
            if(ii != null) {
                ii.getInteract().onInteract(module, player, profile, event);
                event.setCancelled(true);
            }

            Occupation occupation = profile.getOccupation();
            if (occupation != null) {
                Participant participant = occupation.getAlive().get(player.getUniqueId());
                switch(player.getItemInHand().getType()) {
                    case ENDER_PEARL:
                        Cooldown cooldown = profile.getCooldowns().get(Cooldown.Type.ENDER_PEARL);
                        if(cooldown != null){
                            if(!cooldown.isExpired()) {
                                player.sendMessage(cooldown.getBlockedMessage());
                                player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
                            }
                        }
                        break;
                    case ENCHANTED_BOOK:
                        if(participant != null && !participant.isKitApplied()) {
                            int slot = player.getInventory().getHeldItemSlot();
                            CustomKit customKit = profile.getCustomKits().get(occupation.getKit().getUuid()).get(slot + 1);
                            if(customKit != null) {
                                customKit.apply(player, true);
                                occupation.getKit().applyArmor(player);
                                participant.setKitApplied(true);
                            }
                        }
                        break;
                    case BOOK:
                        if(participant != null && !participant.isKitApplied()) {
                            occupation.getKit().apply(player);
                            participant.setKitApplied(true);
                        }
                        break;
                }

                if(profile.getOccupation().getCurrentPlaying().contains(player)) {
                    if(block != null) {
                        BlockState state = block.getState();
                        MaterialData data = state.getData();
                        if (data instanceof Door) {
                            event.setCancelled(true);
                        } else if (data instanceof TrapDoor) {
                            event.setCancelled(true);
                        } else if (data instanceof Gate) {
                            event.setCancelled(true);
                        } else if(data instanceof Lever) {
                            event.setCancelled(true);
                        }
                    }
                } else {
                    event.setCancelled(true);
                }
            } else if(!profile.getSettings().isBuildMode()) {
                event.setCancelled(true);
            }
        }
    }
}
