package pw.octane.practice.kits;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import pw.octane.manager.utils.Colors;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.profiles.Profile;
import xyz.leuo.gooey.action.ButtonAction;
import xyz.leuo.gooey.button.Button;
import xyz.leuo.gooey.gui.GUI;
import xyz.leuo.gooey.gui.GUIUpdate;

import java.util.HashMap;

public class KitEditorGUI extends GUI {
    public KitEditorGUI(PracticeModule module, Profile profile) {
        super("Kit Editor", 9);

        for(Kit kit : module.getKitManager().getKits().values()) {
            if(kit.isEditable()) {
                Button button = new Button(kit.getIcon(), kit.getDisplayName());
                button.setButtonAction((player1, gui1, button1, event1) -> {
                    GUI kitsGui = new GUI("Edit a Kit", 9);
                    kitsGui.setUpdate(new GUIUpdate() {
                        @Override
                        public void onUpdate(GUI gui) {
                            kitsGui.getButtons().clear();
                            for(int i = 0; i < 5; i++) {
                                CustomKit customKit = profile.getCustomKits().computeIfAbsent(kit.getUuid(), uuid -> new HashMap<>()).get(i + 1);
                                Button kitButton;
                                if(customKit != null) {
                                    kitButton = new Button(Material.ENCHANTED_BOOK, ChatColor.GREEN + Colors.get(customKit.getName()));
                                    kitButton.setLore(
                                            "&6Left Click &7- &fEdit this kit.",
                                            "&aRight Click &7- &fRename this kit.",
                                            "&cMiddle Click &7- &fDelete this kit.");
                                    int finalI = i;
                                    kitButton.setButtonAction((player, gui2, button2, inventoryClickEvent) -> {
                                        switch(inventoryClickEvent.getClick()) {
                                            case LEFT:
                                                GUI editingGui = new EditingGUI(profile, customKit);
                                                editingGui.open(player);
                                                break;
                                            case RIGHT:
                                                profile.setRenaming(customKit);
                                                player.closeInventory();
                                                player.sendMessage(ChatColor.GREEN + "Please type the new name of your kit in chat.");
                                                break;
                                            case MIDDLE:
                                                profile.getCustomKits().get(kit.getUuid()).remove(finalI + 1);
                                                kitsGui.update();
                                                break;
                                        }
                                    });

                                } else {
                                    kitButton = new Button(Material.BOOK, Colors.get(kit.getDisplayName()) + " Kit " + (i + 1));
                                    kitButton.setLore(
                                            "&6Left Click &7- &fCreate this kit.");
                                    int finalI = i;
                                    kitButton.setButtonAction(new ButtonAction() {
                                        @Override
                                        public void run(Player player, GUI gui, Button button, InventoryClickEvent inventoryClickEvent) {
                                            CustomKit customKit1 = new CustomKit(kit, finalI + 1);
                                            profile.getCustomKits().computeIfAbsent(kit.getUuid(), uuid -> new HashMap<>()).put(finalI + 1, customKit1);
                                            kitsGui.update();
                                        }
                                    });
                                }

                                int slot = i + 2;
                                kitsGui.setButton(slot, kitButton);
                            }
                        }
                    });
                    kitsGui.update();
                    kitsGui.open(profile.getPlayer());
                });
                this.addButton(button);
            }
        }
    }
}
