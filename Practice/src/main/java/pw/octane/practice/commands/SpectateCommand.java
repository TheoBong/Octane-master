package pw.octane.practice.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.octane.manager.MCommand;
import pw.octane.manager.Module;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.occupations.Occupation;
import pw.octane.practice.profiles.Profile;

public class SpectateCommand extends MCommand {

    private PracticeModule module;

    public SpectateCommand(Module module, String name) {
        super(module, name);
        this.module = (PracticeModule) module;
        this.addAlias("spec");
    }

    @Override
    public void execute(CommandSender sender, String[] args, String alias) {
        if(sender instanceof Player && args.length > 0) {
            Player player = (Player) sender;
            Profile profile = module.getProfileManager().get(player.getUniqueId());
            if(profile.getState().equals(Profile.State.LOBBY)) {
                Player target = Bukkit.getPlayer(args[0]);
                if(target != null) {
                    Profile targetProfile = module.getProfileManager().get(target.getUniqueId());
                    Occupation occupation = targetProfile.getOccupation();
                    if(occupation != null) {
                        occupation.spectateStart(player, target);
                    } else {
                        player.sendMessage(ChatColor.RED + "The target you specified is not in a game.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "The target you specified is not on this server.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "You cannot spectate a match right now.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /spectate <target>");
        }
    }
}
