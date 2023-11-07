package pw.octane.practice.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.octane.manager.MCommand;
import pw.octane.manager.Module;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.occupations.DuelRequest;
import pw.octane.practice.profiles.Profile;

public class AcceptCommand extends MCommand {

    private PracticeModule module;

    public AcceptCommand(Module module, String name) {
        super(module, name);
        this.module = (PracticeModule) module;
    }

    @Override
    public void execute(CommandSender sender, String[] args, String alias) {
        if(sender instanceof Player && args.length > 0) {
            Player player = (Player) sender;
            Profile profile = module.getProfileManager().get(player.getUniqueId());
            Player target = Bukkit.getPlayer(args[0]);

            if(target != null) {
                if(target.getUniqueId().equals(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "All I'm going to say is, bruh.");
                    return;
                }

                DuelRequest duelRequest = profile.getDuelRequests().get(target.getUniqueId());
                Profile targetProfile = module.getProfileManager().get(target.getUniqueId());
                if(duelRequest == null || duelRequest.isExpired()) {
                    player.sendMessage(ChatColor.RED + "You do not have a duel request from this player.");
                    return;
                }

                if(targetProfile.getState().equals(Profile.State.LOBBY) && profile.getState().equals(Profile.State.LOBBY)) {
                    duelRequest.start();
                } else {
                    player.sendMessage(ChatColor.RED + "You cannot duel this player right now.");
                }

            } else {
                player.sendMessage(ChatColor.RED + "The target you specified is not on this server.");
            }

        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /accept <player>");
        }
    }
}
