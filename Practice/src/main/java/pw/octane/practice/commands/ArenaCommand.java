package pw.octane.practice.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.octane.manager.MCommand;
import pw.octane.manager.Module;
import pw.octane.manager.utils.Colors;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.arenas.Arena;
import pw.octane.practice.arenas.ArenaManager;
import pw.octane.practice.kits.Kit;

import java.util.ArrayList;
import java.util.List;

public class ArenaCommand extends MCommand {

    private PracticeModule module;

    public ArenaCommand(Module module, String name) {
        super(module, name, "&a&lArena Manager Help &7(Page <page_number>)");
        this.module = (PracticeModule) module;
        this.setPermission("practice.commands.arena");
        getCommandHelper()
                .addEntry("&e/arena list &7- &fLists all existing arenas.")
                .addEntry("&e/arena create <arena> &7- &fCreates a new arena.")
                .addEntry("&e/arena delete <arena> &7- &fDeletes an existing arena.")
                .addEntry("&e/arena rename <arena> <new name> &7- &fRenames an existing arena.")
                .addEntry("&e/arena setdisplayname <arena> <display name> &7- &fSets the display name for an arena.")
                .addEntry("&e/arena setpos <arena> <1, 2, 3, c1, c2> &7- &fSets a position for an arena.")
                .addEntry("&e/arena settype <arena> <type> &7- &fSets the type for the arena.")
                .addEntry("&e/arena setenabled <arena> <boolean> &7- &fEnables this map to be played on.")
                .addEntry("&e/arena setbuildlimit <arena> <min, max> <int> &7- &fSets the build limit.");
                // TODO: Add arena copy paste stuff later
    }

    @Override
    public void execute(CommandSender sender, String[] args, String alias) {
        if(sender instanceof Player && args.length > 0) {
            Player player = (Player) sender;
            ArenaManager am = module.getArenaManager();
            switch(args[0].toLowerCase()) {
                case "list":
                    List<Arena> list = new ArrayList<>(am.getArenas().values());
                    if(!list.isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("&aArenas &7(" + list.size() + ")&a: ");
                        while(!list.isEmpty()) {
                            final Arena arena = list.get(0);
                            sb.append("&f" + arena.getName());
                            list.remove(arena);
                            if(list.isEmpty()) {
                                sb.append("&7.");
                            } else {
                                sb.append("&7, ");
                            }
                        }

                        sender.sendMessage(Colors.get(sb.toString()));
                    } else {
                        sender.sendMessage(ChatColor.RED + "There are no arenas.");
                    }
                    break;
                case "create":
                    if(args.length > 1) {
                        Arena arena = am.get(args[1]);
                        if (arena == null) {
                            arena = am.createArena(args[1]);
                            sender.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arena.getName() + ChatColor.GREEN + " has been created.");
                        } else {
                            sender.sendMessage(ChatColor.RED + "The arena you specified already exists.");
                        }
                    } else {
                        sender.sendMessage(getCommandHelper().getMessage(1));
                    }
                    break;
                case "delete":
                    if(args.length > 1) {
                        Arena arena = am.get(args[1]);
                        if(arena != null) {
                            module.getArenaManager().remove(true, arena);
                            sender.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arena.getName() + ChatColor.GREEN + " has been removed.");
                        } else {
                            sender.sendMessage(ChatColor.RED + "The arena you specified does not exist.");
                        }
                    } else {
                        sender.sendMessage(getCommandHelper().getMessage(1));
                    }
                    break;
                case "rename":
                    if(args.length > 2) {
                        Arena arena = am.get(args[1]);
                        Arena newName = am.get(args[2]);
                        if(arena != null && newName == null) {
                            sender.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arena.getName() + ChatColor.GREEN + " has been renamed to " + ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GREEN + ".");
                            arena.setName(Colors.strip(args[2].toLowerCase()));
                            am.push(true, arena);
                        } else {
                            sender.sendMessage(ChatColor.RED + "The kit you specified either does not exist or the new name you specified belongs to an existing kit.");
                        }
                    } else {
                        sender.sendMessage(getCommandHelper().getMessage(1));
                    }
                    break;
                case "setdisplayname":
                    if(args.length > 2) {
                        Arena arena = am.get(args[1]);
                        if(arena != null) {
                            sender.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arena.getName() + ChatColor.GREEN + " has been renamed to " + ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GREEN + ".");
                            arena.setDisplayName(args[2]);
                            am.push(true, arena);
                        } else {
                            sender.sendMessage(ChatColor.RED + "The kit you specified does not exist.");
                        }
                    } else {
                        sender.sendMessage(getCommandHelper().getMessage(1));
                    }
                    break;
                case "setpos":
                    if(args.length > 2) {
                        Arena arena = am.get(args[1]);
                        if(arena != null) {
                            Location location = player.getLocation();
                            switch(args[2].toLowerCase()) {
                                case "1":
                                    arena.setPos1(location);
                                    break;
                                case "2":
                                    arena.setPos2(location);
                                    break;
                                case "3":
                                    arena.setPos3(location);
                                    break;
                                case "c1":
                                case "corner1":
                                    arena.setCorner1(location);
                                    break;
                                case "c2":
                                case "corner2":
                                    arena.setCorner2(location);
                                    break;
                                default:
                                    sender.sendMessage(getCommandHelper().getMessage(1));
                                    return;
                            }

                            sender.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arena.getName() + ChatColor.GREEN + " position " + ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GREEN + " has been set to your current location.");
                            am.push(true, arena);
                        } else {
                            sender.sendMessage(ChatColor.RED + "The kit you specified does not exist.");
                        }
                    } else {
                        sender.sendMessage(getCommandHelper().getMessage(1));
                    }
                    break;
                case "settype":
                    if(args.length > 2) {
                        Arena arena = am.get(args[1]);
                        if(arena != null) {
                            Kit.Type type = null;
                            for(Kit.Type t : Kit.Type.values()) {
                                if(t.name().equalsIgnoreCase(args[2].toLowerCase())) {
                                    type = t;
                                }
                            }

                            if(type != null) {
                                sender.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arena.getName() + ChatColor.GREEN + " type has been set to " + ChatColor.WHITE + type.name().toLowerCase() + ChatColor.GREEN + ".");
                                arena.setType(type);
                                am.push(true, arena);
                            } else {
                                sender.sendMessage(ChatColor.RED + "The kit type you specified is invalid.");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "The kit you specified does not exist.");
                        }
                    } else {
                        sender.sendMessage(getCommandHelper().getMessage(1));
                    }
                    break;
                case "setenabled":
                    if(args.length > 2) {
                        Arena arena = am.get(args[1]);
                        if(arena != null) {
                            boolean b = Boolean.parseBoolean(args[2]);

                            sender.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arena.getName() + ChatColor.GREEN + " type has been set to " + ChatColor.WHITE + b + ChatColor.GREEN + ".");
                            arena.setEnabled(b);
                            am.push(true, arena);
                        } else {
                            sender.sendMessage(ChatColor.RED + "The kit you specified does not exist.");
                        }
                    } else {
                        sender.sendMessage(getCommandHelper().getMessage(1));
                    }
                case "setbuildlimit":
                default:
                    sender.sendMessage(getCommandHelper().getMessage(1));
            }
        } else {
            sender.sendMessage(getCommandHelper().getMessage(1));
        }
    }
}
