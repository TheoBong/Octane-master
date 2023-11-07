package pw.octane.store.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.octane.manager.MCommand;
import pw.octane.store.StoreModule;
import pw.octane.store.paypal.DepositHandler;

public class DepositCommand extends MCommand {

    public DepositCommand(StoreModule storeModule) {
        super(storeModule, "deposit");
        this.setDescription("Deposit money into your account via PayPal.");
        this.setUsage(ChatColor.RED + "Usage: /deposit <amount>");
    }

    @Override
    public void execute(final CommandSender sender, final String[] args, String alias) {

        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You cannot perform this command.");
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            sender.sendMessage(usageMessage);
            return;
        }

        try {
            final int amount = Integer.parseInt(args[0]);
            if (amount < 1) {
                sender.sendMessage(ChatColor.RED + "You must deposit at least $1.");
                return;
            }

            new DepositHandler((StoreModule) getModule()).createDepositOrder(amount, player.getUniqueId().toString(), player);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "You must supply a positive integer as the amount.");
        }

    }
}
