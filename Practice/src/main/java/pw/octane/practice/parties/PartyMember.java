package pw.octane.practice.parties;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pw.octane.practice.kits.Kit;

import java.util.UUID;

public @Data class PartyMember {

    private final UUID uuid;
    private String name;
    private Kit.Type kitType;
    private PartyTeam partyTeam;

    public PartyMember(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.kitType = Kit.Type.HCF_DIAMOND;
        this.partyTeam = PartyTeam.RANDOM;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
}
