package pw.octane.practice.occupations;

import lombok.Data;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import pw.octane.manager.utils.Colors;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.arenas.Arena;
import pw.octane.practice.kits.Kit;
import pw.octane.practice.occupations.impl.Duel;
import pw.octane.practice.profiles.Profile;
import pw.octane.practice.profiles.ProfileManager;
import pw.octane.practice.queues.PracticeQueue;

import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public @Data class DuelRequest {

    private final UUID to, from;
    private final Kit kit;
    private final Arena arena;
    private PracticeModule module;
    private Date expires;

    public DuelRequest(PracticeModule module, UUID to, UUID from, Kit kit, Arena arena) {
        this.to = to;
        this.from = from;
        this.kit = kit;
        this.arena = arena;
        this.module = module;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, 30);
        this.expires = calendar.getTime();
    }

    public void start() {
        ProfileManager pm = PracticeModule.INSTANCE.getProfileManager();
        Player to = Bukkit.getPlayer(getTo());
        Player from = Bukkit.getPlayer(getFrom());
        if(to != null && from != null && !isExpired()) {
            Profile toProfile = pm.get(getTo());
            Profile fromProfile = pm.get(getFrom());
            if(toProfile != null && fromProfile != null && toProfile.getState().equals(Profile.State.LOBBY) && fromProfile.getState().equals(Profile.State.LOBBY)) {
                Duel duel = new Duel(PracticeModule.INSTANCE, UUID.randomUUID());
                duel.join(to);
                duel.join(from);
                duel.setQueueType(PracticeQueue.Type.UNRANKED);
                duel.setKit(kit);
                duel.setArena(arena);
                duel.start();
                PracticeModule.INSTANCE.getOccupationManager().getOccupations().put(duel.getUuid(), duel);

                this.expires = new Date();
            }
        }
    }

    public void send() {
        Player to = Bukkit.getPlayer(getTo());
        Player from = Bukkit.getPlayer(getFrom());

        StringBuilder sb = new StringBuilder();
        if(arena != null) {
            sb.append("&bYou received a " + kit.getDisplayName() + "&b duel request from &f" + from.getName() + "&b at the &f" + arena.getDisplayName() + "&b arena, click to accept.");
        } else {
            sb.append("&bYou received a " + Colors.get(kit.getDisplayName()) + "&b duel request from &f" + from.getName() + "&b, click to accept.");
        }

        TextComponent msg = new TextComponent(Colors.get(sb.toString()));
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/accept " + from.getName()));
        msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Colors.get("&aClick to accept!")).create()));
        to.spigot().sendMessage(msg);

        from.sendMessage(ChatColor.GREEN + "You sent a duel request to " + ChatColor.WHITE + to.getName() + ChatColor.GREEN + ".");
    }

    public boolean isExpired() {
        return expires.before(new Date());
    }
}
