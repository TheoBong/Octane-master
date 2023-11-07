package pw.octane.practice.items;

import lombok.Data;
import org.bukkit.inventory.ItemStack;

public @Data class IItem {

    private final ItemStack itemStack;
    private final int slot;
}
