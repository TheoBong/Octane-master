package pw.octane.echo;

import lombok.Getter;
import pw.octane.echo.commands.AltsCommand;
import pw.octane.echo.commands.PinCommand;
import pw.octane.manager.Module;

public class EchoModule extends Module {
    public static EchoModule INSTANCE;

    @Getter String Key;

    @Override
    public void onEnable() {
        INSTANCE = this;
        Key = "MjcxNTI5Mjk5MTk2NzQ4NjkxMzI3NTM4MDg0ODQxODY2NTIx";

        getManager().registerCommand(new AltsCommand(this));
        getManager().registerCommand(new PinCommand(this));

        this.saveDefaultConfig();
    }

    @Override
    public void onDisable() {

    }
}