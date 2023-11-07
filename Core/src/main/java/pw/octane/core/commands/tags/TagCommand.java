package pw.octane.core.commands.tags;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import pw.octane.core.CoreModule;
import pw.octane.core.tags.Tag;
import pw.octane.manager.MCommand;
import pw.octane.manager.Module;
import pw.octane.manager.utils.Colors;

import java.util.*;

public class TagCommand extends MCommand {

    private CoreModule module;

    public TagCommand(Module module, String name) {
        super(module, name, "&a&lTag Manager Help &7(Page <page_number>)");
        this.module = (CoreModule) module;
        this.setPermission("core.commands.tag");

        getCommandHelper()
                .addEntry("&e/tag list &7- &fLists all tags.")
                .addEntry("&e/tag info <name> &7- &fGets a tag's information.")
                .addEntry("&e/tag create <name> <tag> &7- &fCreates a new tag.")
                .addEntry("&e/tag delete <name> &7- &fDeletes an existing tag.")
                .addEntry("&e/tag rename <name> <new name> &7- &fRenames a tag.")
                .addEntry("&e/tag settag <name> <prefix> &7- &fSets a tag's, tag!")
                .addEntry("&e/tag setcolor <name> <color> &7- &fSets a tag's color.")
                .addEntry("&e/tag setdisplayname <name> <display name> &7- &fSets a tag's display name.")
                .addEntry("&e/tag setdescription <name> <description> &7- &fSets a tag's description.")
                .addEntry("&e/tag setvisible <name> <boolean> &7- &fSets a tag's visibility.");
    }

    @Override
    public void execute(CommandSender sender, String[] args, String alias) {
        if(args.length > 0) {
            switch(args[0].toLowerCase()) {
                case "list":
                    if(module.getTagManager().getTags().size() > 0) {
                        TreeMap<String, Tag> map = new TreeMap<>();
                        for (Tag tag : module.getTagManager().getTags().values()) {
                            map.put(tag.getName(), tag);
                        }

                        StringBuilder sb = new StringBuilder();
                        List<Tag> tags = new ArrayList<>(map.descendingMap().values());
                        sb.append("&aTags &7(" + tags.size() + ")&a: ");
                        while (!tags.isEmpty()) {
                            final Tag tag = tags.get(0);
                            tags.remove(tag);
                            sb.append(tag.getColor() + tag.getName());
                            if (tags.isEmpty()) {
                                sb.append("&7.");
                            } else {
                                sb.append("&7, ");
                            }
                        }

                        sender.sendMessage(Colors.get(sb.toString()));
                    } else {
                        sender.sendMessage(ChatColor.RED + "There are no tags.");
                    }
                    break;
                case "info":
                    break;
                case "create":
                    if(args.length > 2) {
                        Tag tag = module.getTagManager().getTag(args[1]);
                        if(tag == null) {

                            tag = module.getTagManager().createTag(Colors.strip(args[1]), args[2]);
                            sender.sendMessage(ChatColor.GREEN + "Tag " + ChatColor.WHITE + tag.getName() + ChatColor.GREEN + " with suffix " + Colors.get(tag.getTag()) + ChatColor.GREEN + " has been created.");
                        } else {
                            sender.sendMessage(ChatColor.RED + "There is already a tag with the name specified.");
                        }
                    } else {
                        sender.sendMessage(getCommandHelper().getMessage(1));
                    }
                    break;
                case "delete":
                    if(args.length > 1) {
                        Tag tag = module.getTagManager().getTag(args[1]);
                        if(tag != null) {
                            module.getTagManager().remove(true, tag);
                            sender.sendMessage(ChatColor.GREEN + "Tag " + ChatColor.WHITE + tag.getName() + ChatColor.GREEN + " has been removed.");
                        } else {
                            sender.sendMessage(ChatColor.RED + "There is not a tag with the name specified.");
                        }
                    } else {
                        sender.sendMessage(getCommandHelper().getMessage(1));
                    }
                    break;
                case "rename":
                    if(args.length > 2) {
                        Tag tag = module.getTagManager().getTag(args[1]);
                        Tag newName = module.getTagManager().getTag(args[2]);
                        if(tag != null && newName == null) {
                            sender.sendMessage(ChatColor.GREEN + "Tag " + ChatColor.WHITE + tag.getName() + ChatColor.GREEN + " has been renamed to " + ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GREEN + ".");
                            tag.setName(Colors.strip(args[2].toLowerCase()));
                            module.getTagManager().push(true, tag);
                        } else {
                            sender.sendMessage(ChatColor.RED + "One of the tag names you specified is invalid or already exists (new name).");
                        }
                    } else {
                        sender.sendMessage(getCommandHelper().getMessage(1));
                    }
                    break;
                case "settag":
                    if(args.length > 2) {
                        Tag tag = module.getTagManager().getTag(args[1]);
                        if(tag != null) {
                            StringBuilder sb = new StringBuilder();
                            for(int i = 2; i < args.length; i++) {
                                sb.append(args[i]);
                                if(i + 1 != args.length) {
                                    sb.append(" ");
                                }
                            }
                            sender.sendMessage(ChatColor.GREEN + "Tag " + ChatColor.WHITE + tag.getName() + ChatColor.GREEN + " suffix has been set to " + Colors.get(sb.toString()) + ChatColor.GREEN + ".");
                            tag.setTag(sb.toString());
                            module.getTagManager().push(true, tag);
                        } else {
                            sender.sendMessage(ChatColor.RED + "There is not a tag with the name specified.");
                        }
                    } else {
                        sender.sendMessage(getCommandHelper().getMessage(1));
                    }
                    break;
                case "setcolor":
                    if(args.length > 2) {
                        Tag tag = module.getTagManager().getTag(args[1]);
                        if(tag != null) {
                            sender.sendMessage(ChatColor.GREEN + "Tag " + ChatColor.WHITE + tag.getName() + ChatColor.GREEN + " color has been set to " + Colors.get(args[2]) + "color" + ChatColor.GREEN + ".");
                            tag.setColor(args[2]);
                            module.getTagManager().push(true, tag);
                        } else {
                            sender.sendMessage(ChatColor.RED + "There is not a tag with the name specified.");
                        }
                    } else {
                        sender.sendMessage(getCommandHelper().getMessage(1));
                    }
                    break;
                case "setdisplayname":
                    if(args.length > 2) {
                        Tag tag = module.getTagManager().getTag(args[1]);
                        if(tag != null) {
                            StringBuilder sb = new StringBuilder();
                            for(int i = 2; i < args.length; i++) {
                                sb.append(args[i]);
                                if(i + 1 != args.length) {
                                    sb.append(" ");
                                }
                            }
                            sender.sendMessage(ChatColor.GREEN + "Tag " + ChatColor.WHITE + tag.getName() + ChatColor.GREEN + " display name has been set to " + Colors.get(tag.getColor() + sb.toString()) + ChatColor.GREEN + ".");
                            tag.setDisplayName(sb.toString());
                            module.getTagManager().push(true, tag);
                        } else {
                            sender.sendMessage(ChatColor.RED + "There is not a tag with the name specified.");
                        }
                    } else {
                        sender.sendMessage(getCommandHelper().getMessage(1));
                    }
                    break;
                case "setdescription":
                    if(args.length > 2) {
                        Tag tag = module.getTagManager().getTag(args[1]);
                        if(tag != null) {
                            StringBuilder sb = new StringBuilder();
                            for(int i = 2; i < args.length; i++) {
                                sb.append(args[i]);
                                if(i + 1 != args.length) {
                                    sb.append(" ");
                                }
                            }
                            sender.sendMessage(ChatColor.GREEN + "Tag " + ChatColor.WHITE + tag.getName() + ChatColor.GREEN + " description has been set to " + ChatColor.WHITE + description + ChatColor.GREEN + ".");
                            tag.setDisplayName(sb.toString());
                            module.getTagManager().push(true, tag);
                        } else {
                            sender.sendMessage(ChatColor.RED + "There is not a tag with the name specified.");
                        }
                    } else {
                        sender.sendMessage(getCommandHelper().getMessage(1));
                    }
                    break;
                case "setvisible":
                    if(args.length > 2) {
                        Tag tag = module.getTagManager().getTag(args[1]);
                        if(tag != null) {
                            boolean v = Boolean.parseBoolean(args[2]);
                            sender.sendMessage(ChatColor.GREEN + "Tag " + ChatColor.WHITE + tag + ChatColor.GREEN + " visibility has been set to " + ChatColor.WHITE + (v ? "true" : "false") + ChatColor.GREEN + ".");
                            tag.setVisible(v);
                            module.getTagManager().push(true, tag);
                        } else {
                            sender.sendMessage(ChatColor.RED + "There is not a tag with the name specified.");
                        }
                    } else {
                        sender.sendMessage(getCommandHelper().getMessage(1));
                    }
                default:
                    sender.sendMessage(getCommandHelper().getMessage(1));
            }
        } else {
            sender.sendMessage(getCommandHelper().getMessage(1));
        }
    }
}
