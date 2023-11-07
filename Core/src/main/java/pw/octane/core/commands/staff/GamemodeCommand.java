package pw.octane.core.commands.staff;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.octane.manager.MCommand;
import pw.octane.manager.Module;

public class GamemodeCommand extends MCommand {

    public GamemodeCommand(Module module, String name) {
        super(module, name);
        this.setPermission("core.commands.gamemode");
        this.setAliases("gmc", "gms", "gm", "survival", "creative", "adventure");
    }

    @Override
    public void execute(CommandSender sender, String[] args, String alias) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            Player target = null;
            String label = alias.toLowerCase();
            GameMode gameMode = null;
            int search = 0;

            switch(label) {
                case "creative":
                case "gmc":
                    gameMode = GameMode.CREATIVE;
                    break;
                case "survival":
                case "gms":
                    gameMode = GameMode.SURVIVAL;
                    break;
                case "adventure":
                    gameMode = GameMode.ADVENTURE;
                    break;
                case "gamemode":
                case "gm":
                    if (args.length > 0) {
                        search = 1;
                        String gm = args[0].toLowerCase();
                        switch (gm) {
                            case "1":
                            case "c":
                            case "creative":
                                gameMode = GameMode.CREATIVE;
                                break;
                            case "0":
                            case "s":
                            case "survival":
                                gameMode = GameMode.SURVIVAL;
                                break;
                            case "2":
                            case "a":
                            case "adventure":
                                gameMode = GameMode.ADVENTURE;
                                break;
                            default:
                                player.sendMessage(ChatColor.RED + "Invalid gamemode.");
                                return;
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You must specify a gamemode.");
                        return;
                    }
            }


            if(search == 0 && args.length == 0 || search == 1 && args.length == 1) {
                target = player;
            } else {
                if(search == 1 && args.length == 2 || search == 0 && args.length == 1) {
                    target = Bukkit.getPlayer(args[search]);
                }

                if(target != null) {
                    player.sendMessage(ChatColor.GREEN + "You have updated " + ChatColor.WHITE + target.getName() + "'s " + ChatColor.GREEN + " gamemode to " + ChatColor.WHITE + gameMode.toString().toLowerCase());
                } else {
                    player.sendMessage(ChatColor.RED + "The target you specified is not on this server.");
                    return;
                }
            }

            target.setGameMode(gameMode);
            target.sendMessage(ChatColor.GREEN + "Your gamemode has been updated to " + ChatColor.WHITE + player.getGameMode().toString().toLowerCase() + ChatColor.GREEN + ".");
        }
    }
}
