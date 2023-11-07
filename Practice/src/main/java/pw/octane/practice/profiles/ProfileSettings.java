package pw.octane.practice.profiles;

import com.google.gson.annotations.Expose;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pw.octane.practice.sidebar.SidebarView;

public @Data class ProfileSettings {

    public enum Time {
        SUNRISE, DAY, SUNSET, NIGHT;

        public long getTime() {
            switch(this) {
                case SUNRISE:
                    return 0;
                case DAY:
                    return 6000;
                case SUNSET:
                    return 13000;
                case NIGHT:
                    return 18000;
                default:
                    return 69;
            }
        }

        public ItemStack getIcon() {
            switch(this) {
                case SUNRISE:
                    return new ItemStack(Material.BEACON);
                case DAY:
                    return new ItemStack(Material.GLOWSTONE);
                case SUNSET:
                    return new ItemStack(Material.YELLOW_FLOWER);
                case NIGHT:
                    return new ItemStack(Material.OBSIDIAN);
                default:
                    return null;
            }
        }
    }

    private @Expose Time time = Time.DAY;
    private @Expose SidebarView sidebarView = SidebarView.ALWAYS;
    private @Expose boolean
            receiveDuelRequests = true,
            receivePartyRequests = true,
            fpsMapsOnly = false,
            spectatorVisibility = false,
            playerVisibility = false,
            debugMode = false,
            staffMode = false,
            buildMode = false;
}
