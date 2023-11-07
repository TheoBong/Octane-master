package pw.octane.core.server;

import org.bukkit.configuration.file.FileConfiguration;

public class CoreServer {

    private int chatCooldown;
    private boolean chatMuted;

    public CoreServer(FileConfiguration config) {
        this.chatCooldown = config.getInt("general.chat_cooldown");
        this.chatMuted = config.getBoolean("general.chat_muted");
    }

    public void export(FileConfiguration config) {
        config.set("general.chat_cooldown", chatCooldown);
        config.set("general.chat_muted", chatMuted);
    }
}
