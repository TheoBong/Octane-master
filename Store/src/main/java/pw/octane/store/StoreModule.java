package pw.octane.store;

import pw.octane.manager.Module;
import pw.octane.store.commands.DepositCommand;
import pw.octane.store.paypal.CaptureTask;
import pw.octane.store.paypal.PaypalClient;

public class StoreModule extends Module {
    public static StoreModule INSTANCE;

    private PaypalClient paypalClient;

    @Override
    public void onEnable() {
        INSTANCE = this;

        this.saveDefaultConfig();
        this.paypalClient = new PaypalClient(this);

        getManager().registerCommand(new DepositCommand(this));

        getPlugin().getServer().getScheduler().runTaskTimerAsynchronously(getPlugin(), new CaptureTask(getManager()), 20L * 60 * 5, 20L * 60L * 5);
    }

    @Override
    public void onDisable() {

    }
}
