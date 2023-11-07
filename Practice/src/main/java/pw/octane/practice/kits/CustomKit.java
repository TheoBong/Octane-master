package pw.octane.practice.kits;

import com.google.gson.annotations.Expose;
import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import pw.octane.manager.utils.Colors;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.kits.Kit;
import pw.octane.practice.utils.ItemUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public @Data class CustomKit {

    private int number;
    private UUID kit;
    private String name;
    private Map<Integer, ItemStack> items;

    public CustomKit(Kit kit, int number) {
        this(kit.getUuid());
        this.number = number;
        this.name = kit.getDisplayName() + " Kit " + number;
        this.items = kit.getItems();
    }

    public CustomKit(UUID uuid) {
        this.kit = uuid;
        this.items = new HashMap<>();
    }

    public void apply(Player player, boolean msg) {
        PracticeModule.INSTANCE.getProfileManager().get(player.getUniqueId()).playerReset();
        PlayerInventory inventory = player.getInventory();

        inventory.clear();
        for(Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue());
        }

        if(msg) {
            player.sendMessage(ChatColor.GREEN + "Applied " + Colors.get(getName()) + ChatColor.GREEN + " kit.");
        }
    }

    public void save(PlayerInventory inventory) {
        for(int i = 0; i < 36; i++) {
            items.put(i, inventory.getItem(i));
        }
    }

    public void reset() {
        Kit kit = PracticeModule.INSTANCE.getKitManager().get(getKit());
        if(kit != null) {
            this.items = kit.getItems();
        }
    }

    public void importFromMap(Map<String, Object> map) {
        this.number = (int) map.get("number");
        this.kit = (UUID) map.get("kit");
        this.name = (String) map.get("name");

        Map<Integer, ItemStack> i = new HashMap<>();
        for(Map.Entry<String, String> entry : ((Map<String, String>) map.get("items")).entrySet()) {
            i.put(Integer.parseInt(entry.getKey()), ItemUtils.convert(entry.getValue()));
        }

        this.items = i;
    }

    public Map<String, Object> export() {
        Map<String, Object> map = new HashMap<>();
        map.put("number", number);
        map.put("kit", kit);
        map.put("name", getName());
        Map<String, String> i = new HashMap<>();
        for(Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
            i.put(String.valueOf(entry.getKey()), ItemUtils.convert(entry.getValue()));
        }

        map.put("items", i);
        return map;
    }
}
