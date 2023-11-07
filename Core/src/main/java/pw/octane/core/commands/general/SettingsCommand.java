package pw.octane.core.commands.general;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.octane.core.CoreModule;
import pw.octane.core.profiles.Profile;
import pw.octane.core.profiles.Settings;
import pw.octane.manager.MCommand;
import pw.octane.manager.Module;

public class SettingsCommand extends MCommand {

    private CoreModule module;

    public SettingsCommand(Module module, String name) {
        super(module, name);
        this.module = (CoreModule) module;
        this.setAliases(
                "toggleglobalchat",
                "tgc",
                "togglepms",
                "tpm",
                "togglestaffmessages",
                "tsm",
                "togglestaffchat",
                "staffchat",
                "sc",
                "clearignored");
    }

    @Override
    public void execute(CommandSender sender, String[] args, String alias) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            Profile profile = module.getProfileManager().get(player.getUniqueId());
            Settings settings = profile.getSettings();
            switch (alias.toLowerCase()) {
                case "toggleglobalchat":
                case "tgc":
                    settings.setGlobalChat(!settings.isGlobalChat());
                    sender.sendMessage(ChatColor.GREEN + "Global chat is now " + ChatColor.WHITE + (settings.isGlobalChat() ? "enabled" : "disabled") + ChatColor.GREEN + ".");
                    break;
                case "togglepms":
                case "tpm":
                    settings.setPrivateMessages(!settings.isPrivateMessages());
                    sender.sendMessage(ChatColor.GREEN + "Private messages are now " + ChatColor.WHITE + (settings.isPrivateMessages() ? "enabled" : "disabled") + ChatColor.GREEN + ".");
                    break;
                case "togglestaffmessages":
                case "tsm":
                    settings.setStaffMessages(!settings.isStaffMessages());
                    sender.sendMessage(ChatColor.GREEN + "Staff messages are now " + ChatColor.WHITE + (settings.isStaffMessages() ? "enabled" : "disabled") + ChatColor.GREEN + ".");
                    break;
                case "togglestaffchat":
                case "staffchat":
                case "sc":
                    if(player.hasPermission("core.staff")) {
                        settings.setStaffChat(!settings.isStaffChat());
                        sender.sendMessage(ChatColor.GREEN + "Staff chat is now " + ChatColor.WHITE + (settings.isStaffChat() ? "enabled" : "disabled") + ChatColor.GREEN + ".");
                    } else {
                        sender.sendMessage(ChatColor.RED + "No permission.");
                    }
                    break;
                case "clearignored":
                    profile.getIgnored().clear();
                    sender.sendMessage(ChatColor.GREEN + "Your ignored player list has been cleared.");
                    break;
                default:
                    sender.sendMessage(ChatColor.RED + "Available commands: /toggleglobalchat, /togglepms, /clearignored.");
            }
        }
    }
}
