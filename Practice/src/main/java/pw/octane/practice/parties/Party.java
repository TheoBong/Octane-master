package pw.octane.practice.parties;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pw.octane.manager.utils.Colors;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.occupations.Occupation;

import java.util.*;

public @Data class Party {

    public enum Privacy {
        CLOSED, OPEN;
    }

    private final PracticeModule module;
    private final UUID uuid;
    private Privacy privacy;
    private Occupation occupation;
    private UUID leader;
    private Map<UUID, PartyMember> members;

    public void join(Player player) {
        PartyMember member = new PartyMember(player.getUniqueId(), player.getName());
        this.members.put(member.getUuid(), member);
        message("");
    }

    public void leave(Player player) {

    }

    public void disband() {
        message("");
        for(PartyMember member : members.values()) {
            leave(member.getPlayer());
        }
    }

    public void message(String s) {
        for(PartyMember member : getMembers().values()) {
            member.getPlayer().sendMessage(Colors.get("&b[" + getPlayerLeader().getName() + "'s Party] &r" + s));
        }
    }

    public Player getPlayerLeader() {
        return Bukkit.getPlayer(leader);
    }

    public List<Player> getPlayerMembers() {
        List<Player> list = new ArrayList<>();
        for(UUID uuid : members.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if(player != null && player.isOnline()) {
                list.add(player);
            }
        }

        return list;
    }
}
