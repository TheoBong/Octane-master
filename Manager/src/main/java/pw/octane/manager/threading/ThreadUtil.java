package pw.octane.manager.threading;

import pw.octane.manager.OctaneManager;

public class ThreadUtil {
    public static void runTask(boolean async, OctaneManager octaneManager, Runnable runnable) {
        if(async) {
            octaneManager.getServer().getScheduler().runTaskAsynchronously(octaneManager, runnable);
        } else {
            runnable.run();
        }
    }
}
