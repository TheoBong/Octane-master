package pw.octane.practice.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.octane.manager.MCommand;
import pw.octane.manager.Module;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.profiles.Profile;
import pw.octane.practice.profiles.ProfileSettings;
import pw.octane.practice.sidebar.SidebarView;
import xyz.leuo.gooey.button.Button;
import xyz.leuo.gooey.gui.GUI;

import java.util.ArrayList;
import java.util.List;

public class SettingsCommand extends MCommand {

    private PracticeModule module;

    public SettingsCommand(Module module, String name) {
        super(module, name);
        this.module = (PracticeModule) module;
    }

    @Override
    public void execute(CommandSender sender, String[] args, String alias) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            Profile profile = module.getProfileManager().get(player.getUniqueId());
            GUI gui = new GUI("Settings", 9);
            gui.setAutoRefresh(false);
            gui.setUpdate(g -> {
                g.getButtons().clear();
                ProfileSettings settings = profile.getSettings();

                Button time = new Button(settings.getTime().getIcon(), "&b&lPlayer Time");
                time.setButtonAction((player1, gui1, button, event) -> {
                    ProfileSettings.Time pTime = null;
                    for(ProfileSettings.Time t : ProfileSettings.Time.values()) {
                        if(settings.getTime().ordinal() + 1 == t.ordinal()) {
                            pTime = t;
                        }
                    }

                    if(pTime == null) {
                        pTime = ProfileSettings.Time.values()[0];
                    }

                    settings.setTime(pTime);
                    profile.playerUpdateTime();
                    gui1.update();
                });
                List<String> timeLore = new ArrayList<>();
                timeLore.add("&bOptions:");
                for(ProfileSettings.Time t : ProfileSettings.Time.values()) {
                    if (t.equals(settings.getTime())) {
                        timeLore.add("&b&o" + StringUtils.capitalize(t.toString().toLowerCase()) + " &7(selected)");
                    } else {
                        timeLore.add("&7" + StringUtils.capitalize(t.toString().toLowerCase()));
                    }
                }
                time.setLore(timeLore);

                Button sidebar = new Button(Material.EMPTY_MAP, "&b&lSidebar View");
                sidebar.setButtonAction((player1, gui1, button, event) -> {
                    SidebarView sv = null;
                    for(SidebarView s : SidebarView.values()) {
                        if(settings.getSidebarView().ordinal()+ 1 == s.ordinal()) {
                            sv = s;
                        }
                    }

                    if(sv == null) {
                        sv = SidebarView.values()[0];
                    }

                    settings.setSidebarView(sv);
                    gui1.update();
                });
                List<String> sidebarLore = new ArrayList<>();
                sidebarLore.add("&bOptions:");
                for(SidebarView s : SidebarView.values()) {
                    String replace = s.toString().toLowerCase().replace('_', ' ');
                    if (s.equals(settings.getSidebarView())) {
                        sidebarLore.add("&b&o" + StringUtils.capitalize(replace) + " &7(selected)");
                    } else {
                        sidebarLore.add("&7" + StringUtils.capitalize(replace));
                    }
                }
                sidebar.setLore(sidebarLore);

                Button duelRequests = new Button(Material.DIAMOND_SWORD, "&b&lDuel Requests");
                duelRequests.setLore(
                        "&7Do you want to receive duel requests?",
                        "&bCurrent Setting: &f" + (settings.isReceiveDuelRequests() ? "Yes" : "No"
                        ));
                duelRequests.setButtonAction((player1, gui1, button, inventoryClickEvent) -> {
                    settings.setReceiveDuelRequests(!settings.isReceiveDuelRequests());
                    gui1.update();
                });

                Button partyRequests = new Button(Material.NETHER_STAR, "&b&lParty Invites");
                partyRequests.setLore(
                        "&7Do you want to receive party invites?",
                        "&bCurrent Setting: &f" + (settings.isReceivePartyRequests() ? "Yes" : "No"
                        ));
                partyRequests.setButtonAction((player1, gui1, button, inventoryClickEvent) -> {
                    settings.setReceivePartyRequests(!settings.isReceivePartyRequests());
                    gui1.update();
                });

                Button fpsMaps = new Button(Material.GRASS, "&b&lFPS Maps Only");
                fpsMaps.setLore(
                        "&7When you queue, do you only",
                        "&7want to play on FPS maps?",
                        "&bCurrent Setting: &f" + (settings.isFpsMapsOnly() ? "Yes" : "No"
                        ));
                fpsMaps.setButtonAction((player1, gui1, button, inventoryClickEvent) -> {
                    settings.setFpsMapsOnly(!settings.isFpsMapsOnly());
                    gui1.update();
                });

                Button playerVisibility = new Button(Material.BEACON, "&b&lPlayer Visibility");
                playerVisibility.setLore(
                        "&7Do you want to see other",
                        "&7players in the lobby?",
                        "&bCurrent Setting: &f" + (settings.isPlayerVisibility() ? "Yes" : "No"
                        ));
                playerVisibility.setButtonAction((player1, gui1, button, inventoryClickEvent) -> {
                    settings.setPlayerVisibility(!settings.isPlayerVisibility());
                    profile.playerUpdateVisibility();
                    gui1.update();
                });

                Button debugMode = new Button(Material.DIAMOND_SWORD, "&b&lDebug Mode");
                debugMode.setLore(
                        "&7Aren't you special, you get to see",
                        "&7this special button that does special things. :O",
                        "&bCurrent Setting: &f" + (settings.isDebugMode() ? "Yes" : "No"
                        ));
                debugMode.setButtonAction((player1, gui1, button, inventoryClickEvent) -> {
                    settings.setDebugMode(!settings.isDebugMode());
                    gui1.update();
                });

                gui.setButton(0, time);
                gui.setButton(1, sidebar);
                gui.setButton(2, duelRequests);
                gui.setButton(3, partyRequests);
                gui.setButton(4, fpsMaps);
                gui.setButton(5, playerVisibility);

                if(player.hasPermission("practice.staff")) {
                    gui.setButton(8, debugMode);
                }
            });

            gui.open(player);
        }
    }
}
