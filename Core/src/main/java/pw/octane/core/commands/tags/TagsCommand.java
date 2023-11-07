package pw.octane.core.commands.tags;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import pw.octane.core.CoreModule;
import pw.octane.core.profiles.Profile;
import pw.octane.core.ranks.Rank;
import pw.octane.core.tags.Tag;
import pw.octane.manager.MCommand;
import pw.octane.manager.Module;
import pw.octane.manager.utils.Colors;
import xyz.leuo.gooey.button.Button;
import xyz.leuo.gooey.gui.PaginatedGUI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TagsCommand extends MCommand {

    private CoreModule module;

    public TagsCommand(Module module, String name) {
        super(module, name);
        this.module = (CoreModule) module;
    }

    @Override
    public void execute(CommandSender sender, String[] args, String alias) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            Profile profile = module.getProfileManager().get(player.getUniqueId());
            Tag tag = profile.getAppliedTag();
            PaginatedGUI gui;
            boolean b;

            List<Tag> ownedTags = new ArrayList<>(profile.getAllTags());
            ownedTags.sort(Comparator.comparing(Tag::getName));

            List<Tag> allTags = new ArrayList<>(module.getTagManager().getTags().values());
            allTags.sort(Comparator.comparing(Tag::getName));

            if(args.length > 0) {
                gui = new PaginatedGUI("Tags Search", 36);
                StringBuilder sb = new StringBuilder();
                for(int i = 0; i < args.length; i++) {
                    sb.append(args[i]);
                    if(i + 1 != args.length) {
                        sb.append(" ");
                    }
                }

                if(tag != null && tag.getName().contains(sb.toString().toLowerCase())) {
                    Button applied = getButton(tag, profile);
                    gui.addButton(applied);
                }

                for(Tag t : allTags) {

                    b = tag == null || !tag.equals(t);

                    if(!t.getName().contains(sb.toString().toLowerCase())) {
                        b = false;
                    }

                    if(!profile.getTags().contains(t.getUuid()) && !t.isVisible() && !profile.getPlayer().hasPermission("core.tags.all")) {
                        b = false;
                    }

                    if(b) {
                        gui.addButton(getButton(t, profile));
                    }
                }

            } else {
                gui = new PaginatedGUI("Tags", 36);
                if(tag != null) {
                    Button applied = getButton(tag, profile);
                    gui.addButton(applied);
                }

                for(Tag t : ownedTags) {

                    b = tag == null || !tag.equals(t);

                    if (b) {
                        gui.addButton(getButton(t, profile));
                    }
                }

                for(Tag t : allTags) {

                    b = tag == null || !tag.equals(t);

                    if(!profile.getTags().contains(t.getUuid()) && !t.isVisible() && !profile.getPlayer().hasPermission("core.tags.all")) {
                        b = false;
                    }

                    if(b) {
                        gui.addButton(getButton(t, profile));
                    }
                }
            }

            gui.open(player);
        }
    }

    public Button getButton(Tag tag, Profile profile) {
        Button button = new Button(Material.NAME_TAG, tag.getColor() + tag.getDisplayName() + (profile.getAppliedTag() != null ? (profile.getAppliedTag().equals(tag) ? " &7(applied)" : "") : ""));
        if(profile.getAppliedTag() != null && profile.getAppliedTag().equals(tag)) {
            button.getMeta().addEnchant(Enchantment.DURABILITY, 1, true);
        }

        Rank rank = profile.getHighestRank();
        button.setLore(
                "",
                "&aTag:",
                tag.getColor() + tag.getTag(),
                "",
                "&aPreview:",
                "&f" + (rank == null ? "" : (rank.getPrefix() == null ? "" : rank.getPrefix() + " ") + rank.getColor()) + profile.getName() + " " + tag.getTag() + "&7: &fHi!",
                "",
                profile.getAppliedTag() == null || (profile.getAppliedTag() != null && !profile.getAppliedTag().equals(tag))
                        ? (profile.getTags().contains(tag.getUuid()) || profile.getPlayer().hasPermission("core.tags.all") ? "&7Click to apply." : "&cYou cannot apply this tag.") : "&aThis tag is already applied."
        );
        button.setCloseOnClick(false);

        button.setButtonAction((player, gui, b, event) -> {
            if(module.getTagManager().getTag(tag.getUuid()) != null) {
                if (profile.getAppliedTag() != null && profile.getAppliedTag().equals(tag)) {
                    profile.setAppliedTag(null);
                    player.sendMessage(ChatColor.GREEN + "You no longer have a tag applied.");
                    player.closeInventory();
                } else if(profile.getTags().contains(tag.getUuid()) || player.hasPermission("core.tags.all")) {
                    profile.setAppliedTag(tag.getUuid());
                    player.sendMessage(ChatColor.GREEN + "Your tag has been set to " + Colors.get(tag.getColor() + tag.getDisplayName()) + ChatColor.GREEN + ".");
                    player.closeInventory();
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to apply this tag.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "The tag you interacted with is no longer available.");
                player.closeInventory();
            }
        });

        return button;
    }
}
