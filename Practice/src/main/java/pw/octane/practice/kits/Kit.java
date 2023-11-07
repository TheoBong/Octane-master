package pw.octane.practice.kits;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import lombok.Data;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import pw.octane.manager.utils.Colors;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.utils.ItemUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public @Data class Kit {

    public enum Type {
        NORMAL, SUMO, SPLEEF, BUILD, BOXING, HCF_DIAMOND, HCF_BARD, HCF_ARCHER;

        public boolean isNoDamage() {
            switch(this) {
                case BOXING:
                case SPLEEF:
                case SUMO:
                    return true;
                default:
                    return false;
            }
        }

        public boolean isBreakAllowedBlocks() {
            switch(this) {
                case SPLEEF:
                    return true;
                default:
                    return false;
            }
        }

        public boolean isBuild() {
            switch(this) {
                case BUILD:
                case SPLEEF:
                    return true;
                default:
                    return false;
            }
        }
    }

    private final UUID uuid;
    private String name, displayName, color;
    private int unrankedPosition, rankedPosition, unranked2v2Position, ranked2v2Position, editPosition;
    private boolean queueable, allow2v2, ranked, editable, moreItems, regen, hunger, moveOnStart;
    private Type type;
    private ItemStack icon;
    private Map<Integer, ItemStack> armor;
    private Map<Integer, ItemStack> items;

    public Kit(UUID uuid) {
        this.uuid = uuid;
        this.color = "&a";
        this.type = Type.NORMAL;
        this.icon = new ItemStack(Material.WOOD_SWORD);
        this.regen = true;
        this.hunger = true;
        this.armor = new HashMap<>();
        this.items = new HashMap<>();
    }

    public void apply(Player player) {
        PracticeModule.INSTANCE.getProfileManager().get(player.getUniqueId()).playerReset();
        PlayerInventory inventory = player.getInventory();
        inventory.clear();

        applyArmor(player);

        for(Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue());
        }

        player.sendMessage(ChatColor.GREEN + "Applied default " + Colors.get(getColor() + getDisplayName()) + ChatColor.GREEN + " kit.");
    }

    public void applyArmor(Player player) {
        PlayerInventory inventory = player.getInventory();
        ItemStack[] kitArmor = new ItemStack[4];
        for(Map.Entry<Integer, ItemStack> entry : armor.entrySet()) {
            kitArmor[entry.getKey()] = entry.getValue();
        }

        inventory.setArmorContents(kitArmor);
    }

    public void importFromDocument(Document d) {
        setName(d.getString("name"));
        setDisplayName(d.getString("display_name"));
        setColor(d.getString("color"));
        setUnrankedPosition(d.getInteger("unranked_position"));
        setRankedPosition(d.getInteger("ranked_position"));
        setUnranked2v2Position(d.getInteger("unranked_2v2_position"));
        setRanked2v2Position(d.getInteger("ranked_2v2_position"));
        setQueueable(d.getBoolean("queueable"));
        setAllow2v2(d.getBoolean("allow_2v2"));
        setRanked(d.getBoolean("ranked"));
        setEditable(d.getBoolean("editable"));
        setMoreItems(d.getBoolean("more_items"));
        setRegen(d.getBoolean("regen"));
        setHunger(d.getBoolean("hunger"));
        setType(Kit.Type.valueOf(d.getString("type")));
        setIcon(ItemUtils.convert(d.getString("icon")));

        Object editPos = d.get("edit_position");
        if(editPos instanceof Integer) {
            setEditPosition((int) editPos);
        }

        Object armor, items;

        armor = d.get("armor");
        items = d.get("items");
        if(armor != null) {
            Map<String, String> a = (Map<String, String>) armor;
            for(Map.Entry<String, String> entry : a.entrySet()) {
                getArmor().put(Integer.parseInt(entry.getKey()), ItemUtils.convert(entry.getValue()));
            }
        }

        if(items != null) {
            Map<String, String> a = (Map<String, String>) items;
            for(Map.Entry<String, String> entry : a.entrySet()) {
                getItems().put(Integer.parseInt(entry.getKey()), ItemUtils.convert(entry.getValue()));
            }
        }
    }

    public Map<String, Object> export() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", getName());
        map.put("display_name", getDisplayName());
        map.put("color", getColor());
        map.put("unranked_position", getUnrankedPosition());
        map.put("ranked_position", getRankedPosition());
        map.put("unranked_2v2_position", getUnranked2v2Position());
        map.put("ranked_2v2_position", getRanked2v2Position());
        map.put("edit_position", getEditPosition());
        map.put("queueable", isQueueable());
        map.put("allow_2v2", isAllow2v2());
        map.put("ranked", isRanked());
        map.put("editable", isEditable());
        map.put("more_items", isMoreItems());
        map.put("regen", isRegen());
        map.put("hunger", isHunger());
        map.put("type", getType().toString());
        map.put("icon", ItemUtils.convert(getIcon()));

        Map<String, String> a = new HashMap<>();
        for(Map.Entry<Integer, ItemStack> entry : getArmor().entrySet()) {
            a.put(String.valueOf(entry.getKey()), ItemUtils.convert(entry.getValue()));
        }

        Map<String, String> i = new HashMap<>();
        for(Map.Entry<Integer, ItemStack> entry : getItems().entrySet()) {
            i.put(String.valueOf(entry.getKey()), ItemUtils.convert(entry.getValue()));
        }

        map.put("armor", a);
        map.put("items", i);
        return map;
    }
}
