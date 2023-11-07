package pw.octane.practice.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.octane.manager.MCommand;
import pw.octane.manager.Module;
import pw.octane.practice.PracticeModule;

public class TournamentCommand extends MCommand {

    private PracticeModule module;

    public TournamentCommand(Module module, String name) {
        super(module, name);
        this.module = (PracticeModule) module;
    }

    @Override
    public void execute(CommandSender sender, String[] args, String label) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
        }
    }
}
