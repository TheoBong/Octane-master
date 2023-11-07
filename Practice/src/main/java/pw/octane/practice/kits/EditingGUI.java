package pw.octane.practice.kits;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import pw.octane.manager.utils.Colors;
import pw.octane.practice.profiles.Profile;
import xyz.leuo.gooey.action.ButtonAction;
import xyz.leuo.gooey.button.Button;
import xyz.leuo.gooey.gui.GUI;
import xyz.leuo.gooey.gui.GUICloseUpdate;

public class EditingGUI extends GUI {

    private Profile profile;
    private CustomKit kit;

    public EditingGUI(Profile profile, CustomKit customKit) {
        super("Editing Kit", 9);
        this.profile = profile;
        this.kit = customKit;

        profile.setEditing(customKit);
        customKit.apply(profile.getPlayer(), false);

        Button save = new Button(Material.EMERALD, "&6Save Kit");
        save.setCloseOnClick(true);
        save.setButtonAction(new ButtonAction() {
            @Override
            public void run(Player player, GUI gui, Button button, InventoryClickEvent inventoryClickEvent) {
                customKit.save(player.getInventory());
                player.sendMessage(ChatColor.GREEN + "Custom kit " + Colors.get(kit.getName()) + ChatColor.GREEN + " has been saved!");
            }
        });

        Button reset = new Button(Material.REDSTONE, "&cReset Kit");
        reset.setButtonAction(new ButtonAction() {
            @Override
            public void run(Player player, GUI gui, Button button, InventoryClickEvent inventoryClickEvent) {
                kit.reset();
                kit.apply(player, false);
            }
        });

        Button garbage = new Button(Material.TNT, "&4Garbage");
        garbage.setLore("&7Put an item you don't want here.");
        garbage.setButtonAction(new ButtonAction() {
            @Override
            public void run(Player player, GUI gui, Button button, InventoryClickEvent inventoryClickEvent) {
                player.setItemOnCursor(null);
            }
        });

        this.setBackground(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        this.setGuiCloseUpdate(new GUICloseUpdate() {
            @Override
            public void onClose(Player player, GUI gui, InventoryCloseEvent inventoryCloseEvent) {
                profile.setEditing(null);
                profile.playerUpdate();
            }
        });

        this.setButton(3, save);
        this.setButton(5, reset);
        this.setButton(8, garbage);
    }

    @Override
    public void open(Player player) {
        super.open(player);
        kit.apply(player, false);
    }
}
