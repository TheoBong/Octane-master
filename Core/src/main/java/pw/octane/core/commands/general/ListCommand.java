package pw.octane.core.commands.general;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import pw.octane.core.CoreModule;
import pw.octane.core.profiles.Profile;
import pw.octane.core.ranks.Rank;
import pw.octane.manager.MCommand;
import pw.octane.manager.Module;
import pw.octane.manager.utils.Colors;

import java.util.*;

public class ListCommand extends MCommand {

    private CoreModule module;

    public ListCommand(Module module, String name) {
        super(module, name);
        this.module = (CoreModule) module;
    }

    @Override
    public void execute(CommandSender sender, String[] args, String alias) {
        List<Profile> profiles = new ArrayList<>();
        for(Profile profile : module.getProfileManager().getProfiles().values()) {
            if(profile.getPlayer() != null && profile.getPlayer().isOnline()) {
                profiles.add(profile);
            }
        }

        profiles.sort(Comparator.comparing(Profile::getName));

        ArrayList<Rank> ranks = new ArrayList<>(module.getRankManager().getRanks().values());
        ranks.sort(Comparator.comparing(Rank::getWeight).reversed());

        StringBuilder sbRanks = new StringBuilder(), sbPlayers = new StringBuilder();
        sbPlayers.append("&aOnline (" + Bukkit.getOnlinePlayers().size() + "): ");

        while(!ranks.isEmpty()) {
            final Rank rank = ranks.get(0);
            List<Profile> list = new ArrayList<>(profiles);
            for(Profile profile : list) {
                if(profile.getHighestRank().equals(rank)) {
                    sbPlayers.append(rank.getColor() + profile.getName());
                    profiles.remove(profile);
                    if(profiles.size() > 0) {
                        sbPlayers.append("&7, ");
                    } else {
                        sbPlayers.append("&7.");
                    }
                }
            }

            sbRanks.append(rank.getColor() + rank.getDisplayName());
            ranks.remove(rank);
            if(ranks.isEmpty()) {
                sbRanks.append("&7.");
            } else {
                sbRanks.append("&7, ");
            }
        }

        sender.sendMessage(Colors.get(sbRanks.toString()));
        sender.sendMessage(Colors.get(sbPlayers.toString()));
    }
}
