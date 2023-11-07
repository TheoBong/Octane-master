package pw.octane.core.punishments.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import pw.octane.core.profiles.Profile;
import pw.octane.core.punishments.Punishment;
import xyz.leuo.gooey.action.ButtonAction;
import xyz.leuo.gooey.button.Button;
import xyz.leuo.gooey.gui.GUI;
import xyz.leuo.gooey.gui.PaginatedGUI;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class PunishmentsGUI extends GUI {

    private Profile profile;

    public PunishmentsGUI(Profile profile) {
        super(profile.getName(), 9);

        List<Punishment> kicks = profile.getPunishments(Punishment.Type.KICK);
        List<Punishment> mutes = profile.getPunishments(Punishment.Type.MUTE);
        List<Punishment> bans = profile.getPunishments(Punishment.Type.BAN);
        List<Punishment> blacklists = profile.getPunishments(Punishment.Type.BLACKLIST);

        Button kicksButton = new Button(Material.WOOL, "&cKicks");
        kicksButton.addLore(
                "&cAmount: &f" + kicks.size(),
                "",
                "&7View " + profile.getName() + "'s kicks.");
        kicksButton.setButtonAction((player, gui, button, inventoryClickEvent) -> {
            get(kicks, profile.getName() + "'s Kicks").open(player);
        });
    }

    public GUI get(List<Punishment> punishments, String title) {
        PaginatedGUI gui = new PaginatedGUI(title, 36);
        TreeMap<Date, Punishment> map = new TreeMap<>();

        for(Punishment p : punishments) {
            if(p.isActive()) {
                gui.addButton(createButton(p));
            }

            map.put(p.getIssued(), p);
        }

        for(Punishment p : map.descendingMap().values()) {
           gui.addButton(createButton(p));
        }

        return gui;
    }

    public Button createButton(Punishment punishment) {
        return null;
    }
}
