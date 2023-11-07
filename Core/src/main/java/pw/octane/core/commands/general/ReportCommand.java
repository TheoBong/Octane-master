package pw.octane.core.commands.general;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.octane.core.CoreModule;
import pw.octane.core.networking.CoreRedisAction;
import pw.octane.core.profiles.Profile;
import pw.octane.manager.MCommand;
import pw.octane.manager.Module;
import pw.octane.manager.networking.redis.RedisMessage;

import java.util.Calendar;
import java.util.Date;

public class ReportCommand extends MCommand {

    private CoreModule module;

    public ReportCommand(Module module, String name) {
        super(module, name);
        this.module = (CoreModule) module;
    }

    @Override
    public void execute(CommandSender sender, String[] args, String alias) {
        if(sender instanceof Player) {
            if(args.length > 1) {
                Player player = (Player) sender;
                Profile profile = module.getProfileManager().get(player.getUniqueId());
                Player target = Bukkit.getPlayer(args[0]);

                Date date = profile.getCooldowns().get(Profile.Cooldown.REPORT);
                if(!(date == null || date.before(new Date()))) {
                    sender.sendMessage(ChatColor.RED + "You have to wait a minute after your last report to send another report.");
                }

                if(target != null) {

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.add(Calendar.MINUTE, 1);
                    profile.getCooldowns().put(Profile.Cooldown.REPORT, calendar.getTime());

                    StringBuilder sb = new StringBuilder();
                    for(int i = 1; i < args.length; i++) {
                        sb.append(args[i]);
                        if(i + 1 != args.length) {
                            sb.append(" ");
                        }
                    }

                    StringBuilder reportSb = new StringBuilder();
                    reportSb.append(" &f&l* &4&lReport");
                    reportSb.append("\n&f" + player.getName() + " &creported &f&l" + target.getName() + " &con server &f" + module.getConfig().getString("general.server_name") + "&c.");
                    reportSb.append("\n&cReport: &f" + sb.toString());

                    JsonObject json = new JsonObject();
                    json.addProperty("action", CoreRedisAction.STAFF_BROADCAST.toString());
                    json.addProperty("message", reportSb.toString());
                    module.getManager().getRedisPublisher().getMessageQueue().add(new RedisMessage("core", json));

                    player.sendMessage(ChatColor.GREEN + "You reported " + ChatColor.WHITE + target.getName() + ChatColor.GREEN + " for: " + ChatColor.WHITE + sb.toString() +
                            "\n&7Your report has been sent to all online staff members.");
                } else {
                    sender.sendMessage(ChatColor.RED + "The target you specified is not on this server.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /report <player> <reason>");
            }
        }
    }
}
