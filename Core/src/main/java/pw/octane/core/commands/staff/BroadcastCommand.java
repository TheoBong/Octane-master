package pw.octane.core.commands.staff;

import com.google.gson.JsonObject;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import pw.octane.core.CoreModule;
import pw.octane.core.networking.CoreRedisAction;
import pw.octane.manager.MCommand;
import pw.octane.manager.Module;
import pw.octane.manager.networking.redis.RedisMessage;

public class BroadcastCommand extends MCommand {

    private CoreModule module;

    public BroadcastCommand(Module module, String name) {
        super(module, name);
        this.module = (CoreModule) module;
        this.setAliases("staffbroadcast");
        this.setPermission("core.commands.broadcast");
    }

    @Override
    public void execute(CommandSender sender, String[] args, String alias) {
        if(args.length > 0) {
            CoreRedisAction cra = CoreRedisAction.BROADCAST;
            if (alias.equalsIgnoreCase("staffbroadcast")) {
                cra = CoreRedisAction.STAFF_BROADCAST;
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                sb.append(args[i]);
                if(i + 1 != args.length) {
                    sb.append(" ");
                }
            }

            JsonObject json = new JsonObject();
            json.addProperty("action", cra.toString());
            json.addProperty("message", (cra == CoreRedisAction.STAFF_BROADCAST ? "&7[&4Staff Broadcast&7] &r": "&7[&cBroadcast&7] &r") + sb.toString());
            module.getManager().getRedisPublisher().getMessageQueue().add(new RedisMessage("core", json));
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /" + alias.toLowerCase() + " <message>");
        }
    }
}
