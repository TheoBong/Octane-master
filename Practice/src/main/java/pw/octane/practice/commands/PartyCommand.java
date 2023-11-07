package pw.octane.practice.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.octane.manager.MCommand;
import pw.octane.manager.Module;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.profiles.Profile;

public class PartyCommand extends MCommand {

    private PracticeModule module;

    public PartyCommand(Module module, String name) {
        super(module, name, "&a&lParty Help &7(Page <page_number>)");
        this.module = (PracticeModule) module;
        this.setAliases("p");
    }

    @Override
    public void execute(CommandSender sender, String[] args, String label) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            Profile profile = module.getProfileManager().get(player.getUniqueId());

            if(args.length > 0) {
                switch(args[0].toLowerCase()) {
                    case "create":
                        break;
                    case "leave":
                        break;
                    case "disband":
                        break;
                    case "invite":
                        break;
                    case "settings":
                        break;
                }
            } else {
                player.sendMessage(getCommandHelper().getMessage(1));
            }
        }
    }
}
