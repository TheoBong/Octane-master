package pw.octane.sg;

import pw.octane.manager.Module;

public class SGModule extends Module {

    public static SGModule INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;
    }

    @Override
    public void onDisable() {
        INSTANCE = null;
    }
}
