package pw.octane.practice.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.octane.manager.MCommand;
import pw.octane.manager.Module;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.profiles.Profile;

public class BuildCommand extends MCommand {

    private PracticeModule module;

    public BuildCommand(Module module, String name) {
        super(module, name);
        this.module = (PracticeModule) module;
        this.setPermission("practice.commands.build");
    }

    @Override
    public void execute(CommandSender sender, String[] args, String alias) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            Profile profile = module.getProfileManager().get(player.getUniqueId());

            profile.getSettings().setBuildMode(!profile.getSettings().isBuildMode());
            sender.sendMessage(ChatColor.GREEN + "Build mode has been set to " + ChatColor.WHITE + profile.getSettings().isBuildMode() + ChatColor.GREEN + ".");
        }
    }
}
