package pw.octane.practice.cosmetics;

import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.occupations.Occupation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum EliminateAnimation {
    DEFAULT, BLOOD;

    public void runAnimation(Location location, Occupation occupation) {
        World world = location.getWorld();
        Random random = new Random();
        switch(this) {
            case BLOOD:
                Location l = new Location(world, location.getX(), location.getY() + 1, location.getZ());

                ItemStack woolItem = new ItemStack(Material.WOOL);
                woolItem.setDurability((short) 14);

                ItemStack dyeItem = new ItemStack(Material.INK_SACK);
                dyeItem.setDurability((short) 1);

                List<Item> items = new ArrayList<>();

                for(int i = 0; i < 12; i++) {
                    Item bloodItem = location.getWorld().dropItemNaturally(l, woolItem);
                    bloodItem.setPickupDelay(Integer.MAX_VALUE);
                    occupation.addEntity(bloodItem);
                    items.add(bloodItem);
                }

                for(int i = 0; i < 16; i++) {
                    Item bloodItem = location.getWorld().dropItemNaturally(l, dyeItem);
                    bloodItem.setPickupDelay(Integer.MAX_VALUE);
                    occupation.addEntity(bloodItem);
                    items.add(bloodItem);
                }

                occupation.playSound(location, Sound.LAVA_POP, 1F, 1F);

                Bukkit.getScheduler().runTaskLater(PracticeModule.INSTANCE.getPlugin(), ()-> {
                    for(Item i : items) {
                        i.remove();
                    }
                }, 40);

                break;
            default:
                occupation.playSound(location, Sound.SUCCESSFUL_HIT, 1, 1);
        }
    }
}
