package pw.octane.practice.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import pw.octane.manager.utils.Colors;
import pw.octane.manager.utils.ItemBuilder;
import pw.octane.practice.kits.Kit;
import pw.octane.practice.kits.CustomKit;
import pw.octane.practice.kits.EditingGUI;
import pw.octane.practice.kits.KitEditorGUI;
import pw.octane.practice.occupations.DuelRequest;
import pw.octane.practice.profiles.PreviousMatch;
import pw.octane.practice.profiles.Profile;
import pw.octane.practice.queues.PracticeQueue;
import pw.octane.practice.queues.QueueGUI;
import xyz.leuo.gooey.action.ButtonAction;
import xyz.leuo.gooey.button.Button;
import xyz.leuo.gooey.gui.GUI;
import xyz.leuo.gooey.gui.GUIUpdate;

import java.util.HashMap;

public enum InteractableItem {
    UNRANKED, RANKED, PARTY_CREATE, COSMETICS, KIT_EDITOR, SETTINGS, REMATCH,
    TOURNAMENT_STATUS, TOURNAMENT_LEAVE,
    PARTY_EVENT, UNRANKED2V2, RANKED2V2, PARTY_LEAVE, PARTY_SETTINGS, PARTY_GAME_JOIN,
    EVENT_LEAVE,
    QUEUE_LEAVE,
    STOP_SPECTATING, TOGGLE_SPECTATORS;

    public IItemInteract getInteract(){
        switch(this) {
            case UNRANKED:
                return (module, player, profile, event) -> {
                    GUI queueGUI = new QueueGUI(module, profile, PracticeQueue.Type.UNRANKED);
                    queueGUI.open(player);
                };
            case KIT_EDITOR:
                return (module, player, profile, event) -> {
                    GUI gui = new KitEditorGUI(module, profile);
                    gui.open(player);
                };
            case SETTINGS:
                return ((module, player, profile, event) -> player.performCommand("settings"));
            case REMATCH:
                return (module, player, profile, event) -> {
                    PreviousMatch previousMatch = profile.getPreviousMatch();
                    if(previousMatch != null) {
                        DuelRequest dr = profile.getDuelRequests().get(previousMatch.getUuid());
                        if(dr != null && !dr.isExpired()) {
                            dr.start();
                        } else {
                            Profile targetProfile = module.getProfileManager().get(previousMatch.getUuid());
                            if(targetProfile != null) {
                                dr = targetProfile.getDuelRequests().get(player.getUniqueId());
                                if(dr == null || dr.isExpired()) {
                                    if(targetProfile.getSettings().isReceiveDuelRequests()) {
                                        dr = new DuelRequest(module, previousMatch.getUuid(), player.getUniqueId(), previousMatch.getKit(), previousMatch.getArena());
                                        dr.send();
                                        targetProfile.getDuelRequests().put(player.getUniqueId(), dr);
                                    } else {
                                        player.sendMessage(ChatColor.RED + "This player is not accepting rematch requests.");
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "You already sent a rematch request to this player.");
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "This player is no longer online.");
                            }
                        }
                    }
                };
            case QUEUE_LEAVE:
                return ((module, player, profile, event) -> {
                    profile.getQueue().leave();
                });
            case STOP_SPECTATING:
                return (module, player, profile, event) -> {
                    profile.getOccupation().spectateEnd(player);
                };
            case TOGGLE_SPECTATORS:
                return (module, player, profile, event) -> {
                    profile.getSettings().setSpectatorVisibility(!profile.getSettings().isSpectatorVisibility());
                    profile.playerUpdateVisibility();

                    if(profile.getState().equals(Profile.State.SPECTATING)) {
                        player.setItemInHand(this.getItem(profile).getItemStack());
                    }

                    if(profile.getSettings().isSpectatorVisibility()) {
                        player.sendMessage(ChatColor.GREEN + "You can now see other spectators.");
                    } else {
                        player.sendMessage(ChatColor.RED + "You can no longer see other spectators.");
                    }
                };
            default:
                return (module, player, profile, event) -> {
                    player.sendMessage(ChatColor.GREEN + "Coming soon!");
                };
        }
    }

    public IItem getItem(Profile profile) {
        switch(this) {
            case UNRANKED:
                return new IItem(new ItemBuilder(Material.IRON_SWORD, "&bUnranked Queue &7(Right Click)").unbreakable(true).create(), 0);
            case RANKED:
                return new IItem(new ItemBuilder(Material.DIAMOND_SWORD, "&bRanked Queue &7(Right Click)").unbreakable(true).create(), 1);
            case REMATCH:
                if(profile.getPreviousMatch() != null) {
                    ItemStack skullItem = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                    SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();
                    skullMeta.setOwner(profile.getPreviousMatch().getName());
                    skullMeta.setDisplayName(Colors.get("&bRematch " + profile.getPreviousMatch().getName() + " &7(Right Click)"));
                    skullItem.setItemMeta(skullMeta);
                    return new IItem(skullItem, 2);
                } else {
                    return new IItem(new ItemBuilder(Material.SKULL_ITEM, "&cInvalid Rematch").create(), 2);
                }
            case PARTY_CREATE:
                return new IItem(new ItemBuilder(Material.NETHER_STAR, "&bCreate a Party &7(Right Click)").create(), 4);
            case COSMETICS:
                return new IItem(new ItemBuilder(Material.CHEST, "&b&lNEW! &bCosmetics &7(Right Click)").create(), 6);
            case KIT_EDITOR:
                return new IItem(new ItemBuilder(Material.BOOK, "&bEdit Kits &7(Right Click)").create(), 7);
            case SETTINGS:
                return new IItem(new ItemBuilder(Material.ANVIL, "&bSettings &7(Right Click)").create(), 8);
            case QUEUE_LEAVE:
                return new IItem(new ItemBuilder(Material.REDSTONE, "&cLeave Queue &7(Right Click)").create(), 4);
            case STOP_SPECTATING:
                return new IItem(new ItemBuilder(Material.REDSTONE, "&cStop Spectating &7(Right Click)").create(), 4);
            case TOGGLE_SPECTATORS:
                ItemStack item = new ItemStack(Material.INK_SACK, 1);
                ItemMeta meta = item.getItemMeta();
                if(profile.getSettings().isSpectatorVisibility()) {
                    item.setDurability((short) 10);
                    meta.setDisplayName(Colors.get("&aHide Spectators &7(Right Click)"));
                } else {
                    item.setDurability((short) 8);
                    meta.setDisplayName(Colors.get("&cShow Spectators &7(Right Click)"));
                }

                item.setItemMeta(meta);
                return new IItem(item, 8);
            default:
                return new IItem(new ItemBuilder(Material.GRASS, "invalid").create(), 9);
        }
    }

    public Profile.State forState() {
        switch(this) {
            case UNRANKED:
            case RANKED:
            case PARTY_CREATE:
            case COSMETICS:
                return Profile.State.LOBBY;
            case TOURNAMENT_STATUS:
            case TOURNAMENT_LEAVE:
                return Profile.State.TOURNAMENT;
            case UNRANKED2V2:
            case RANKED2V2:
            case PARTY_LEAVE:
            case PARTY_GAME_JOIN:
            case PARTY_EVENT:
            case PARTY_SETTINGS:
                return Profile.State.PARTY;
            case EVENT_LEAVE:
                return Profile.State.EVENT;
            case QUEUE_LEAVE:
                return Profile.State.QUEUE;
            case STOP_SPECTATING:
            case TOGGLE_SPECTATORS:
                return Profile.State.SPECTATING;
            default:
                return Profile.State.NONE;
        }
    }

    public static InteractableItem getItem(Profile profile, ItemStack item, int slot) {
        for(InteractableItem ii : InteractableItem.values()) {
            IItem iitem = ii.getItem(profile);
            if(iitem.getItemStack().isSimilar(item) && iitem.getSlot() == slot) {
                return ii;
            }
        }

        return null;
    }
}
