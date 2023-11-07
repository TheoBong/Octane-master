package pw.octane.practice.cosmetics;

import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.occupations.Occupation;

public enum WinAnimation {
    NONE, FIREWORKS;

    public void runAnimation(Player player, Occupation occupation) {
        BukkitTask task = null;
        switch(this) {
            case FIREWORKS:
                int[] i = {0};
//                task = Bukkit.getScheduler().runTaskTimer(PracticeModule.INSTANCE.getPlugin(), new BukkitRunnable() {
//                    @Override
//                    public void run() {
//                        if(i[0] == 5) {
//                            finalTask.cancel();
//                        }
//
//                        i[0] += 1;
//                        if(occupation.getCurrentPlaying().contains(player)) {
//                            Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
//                            FireworkMeta fireworkMeta = firework.getFireworkMeta();
//                            fireworkMeta.setPower(2);
//                            fireworkMeta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.STAR).flicker(true).build());
//                            occupation.addEntity(firework);
//                        }
//                    }
//                }, 0, 10);
        }
    }
}
