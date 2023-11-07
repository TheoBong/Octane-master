package pw.octane.core.profiles;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class CorePermissibleBase extends PermissibleBase {

    private Player player;

    public CorePermissibleBase(Player player) {
        super(player);
        this.player = player;
    }

    @Override
    public boolean hasPermission(String inName) {
        boolean b = isOp() || super.hasPermission(inName);
        String perm = inName.toLowerCase();

        for(PermissionAttachmentInfo permission : player.getEffectivePermissions()) {
            String p = permission.getPermission().toLowerCase();
            if(p.endsWith("*")) {
                String subP = perm.substring(0, perm.length() == 1 ? 0 : perm.length() - 2);
                if(perm.startsWith(subP)) {
                    b = true;
                }
            }
        }

        return b;
    }
}
