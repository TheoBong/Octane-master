package pw.octane.core.commands.tags;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.octane.core.CoreModule;
import pw.octane.core.profiles.Profile;
import pw.octane.core.tags.Tag;
import pw.octane.core.web.WebPlayer;
import pw.octane.manager.MCommand;
import pw.octane.manager.Module;
import pw.octane.manager.utils.Colors;

public class RemoveTagCommand extends MCommand {

    private CoreModule module;

    public RemoveTagCommand(Module module, String name) {
        super(module, name);
        this.module = (CoreModule) module;
        this.setPermission("core.commands.removetag");
    }

    @Override
    public void execute(CommandSender sender, String[] args, String alias) {
        if(args.length > 1) {

            Player target = Bukkit.getPlayer(args[0]);
            Profile profile;
            if(target != null) {
                profile = module.getProfileManager().get(target.getUniqueId());
            } else {
                WebPlayer wp = new WebPlayer(args[0]);
                if(wp.isValid()) {
                    profile = module.getProfileManager().find(wp.getUuid(), false);
                } else {
                    sender.sendMessage(ChatColor.RED + "The target you specified does not exist.");
                    return;
                }
            }

            if (profile == null) {
                sender.sendMessage(ChatColor.RED + "The target you specified has never joined the server.");
                return;
            }

            Tag tag = module.getTagManager().getTag(args[1]);
            if(tag != null) {
                if(profile.getTags().contains(tag.getUuid())) {
                    profile.getTags().remove(tag.getUuid());
                    sender.sendMessage(ChatColor.WHITE + profile.getName() + ChatColor.GREEN + " no longer owns the tag " + Colors.get(tag.getColor() + tag.getDisplayName()) + ChatColor.GREEN + ".");
                } else {
                    sender.sendMessage(ChatColor.RED + "The target you specified doesn't own that tag.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "The tag you specified does not exist.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /addtag <target> <tag>");
        }
    }
}
