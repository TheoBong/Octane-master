package pw.octane.practice.parties;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

public enum PartyTeam {
    BLUE, RED, RANDOM;

    public ChatColor getColor() {
        switch(this) {
            case BLUE:
            case RED:
                return ChatColor.valueOf(this.name());
            default:
                return ChatColor.WHITE;
        }
    }

    @Override
    public String toString() {
        return StringUtils.capitalize(this.name().toLowerCase());
    }
}
