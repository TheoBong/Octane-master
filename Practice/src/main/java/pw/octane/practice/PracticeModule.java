package pw.octane.practice;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.github.thatkawaiisam.assemble.Assemble;
import io.github.thatkawaiisam.assemble.AssembleStyle;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import pw.octane.manager.MCommand;
import pw.octane.manager.Module;
import pw.octane.practice.arenas.ArenaManager;
import pw.octane.practice.commands.*;
import pw.octane.practice.kits.Kit;
import pw.octane.practice.kits.KitManager;
import pw.octane.practice.listeners.bukkit.entity.EntityDamageByEntityListener;
import pw.octane.practice.listeners.bukkit.entity.EntityDamageListener;
import pw.octane.practice.listeners.bukkit.entity.EntityRegainHealthListener;
import pw.octane.practice.listeners.bukkit.entity.EntitySpawnListener;
import pw.octane.practice.listeners.bukkit.inventory.InventoryClickListener;
import pw.octane.practice.listeners.bukkit.inventory.InventoryMoveItemListener;
import pw.octane.practice.listeners.bukkit.player.*;
import pw.octane.practice.listeners.bukkit.potion.PotionSplashListener;
import pw.octane.practice.listeners.bukkit.projectile.ProjectileHitListener;
import pw.octane.practice.listeners.bukkit.projectile.ProjectileLaunchListener;
import pw.octane.practice.listeners.bukkit.world.BlockBreakListener;
import pw.octane.practice.listeners.bukkit.world.BlockFromToListener;
import pw.octane.practice.listeners.bukkit.world.BlockPlaceListener;
import pw.octane.practice.listeners.packets.EnderpearlSound;
import pw.octane.practice.occupations.OccupationManager;
import pw.octane.practice.profiles.ProfileManager;
import pw.octane.practice.queues.QueueManager;
import pw.octane.practice.sidebar.SidebarAdapter;
import pw.octane.practice.tasks.CooldownRunnable;
import pw.octane.practice.tasks.NameColorRunnable;
import pw.octane.practice.utils.EntityHider;

import java.util.Arrays;

public class PracticeModule extends Module {

    public static PracticeModule INSTANCE;

    private Assemble assemble;
    @Getter private ProtocolManager protocolManager;
    @Getter private EntityHider entityHider;

    @Getter private ArenaManager arenaManager;
    @Getter private KitManager kitManager;
    @Getter private OccupationManager occupationManager;
    @Getter private ProfileManager profileManager;
    @Getter private QueueManager queueManager;

    @Getter private BukkitTask cooldownTask, nameColorTask;

    @Getter @Setter private Location lobby;
    
    @Override
    public void onEnable() {
        INSTANCE = this;

        this.saveDefaultConfig();

        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.entityHider = new EntityHider(this.getPlugin(), EntityHider.Policy.BLACKLIST);

        this.arenaManager = new ArenaManager(this);
        this.kitManager = new KitManager(this);
        this.occupationManager = new OccupationManager(this);
        this.profileManager = new ProfileManager(this);
        this.queueManager = new QueueManager(this);

        queueManager.refresh();

        this.assemble = new Assemble(this.getPlugin(), new SidebarAdapter(this));
        assemble.setTicks(5);
        assemble.setAssembleStyle(AssembleStyle.MODERN);
        assemble.setup();

        if(getConfig().get("locations.lobby") == null) {
            getManager().getLogger().warning("No lobby location has been set, please set one by doing /practice setlobby.");
        } else {
            this.lobby = (Location) getConfig().get("locations.lobby", Location.class);
        }

        this.cooldownTask = Bukkit.getScheduler().runTaskTimerAsynchronously(getPlugin(), new CooldownRunnable(this), 2, 2);
        this.nameColorTask = Bukkit.getScheduler().runTaskTimerAsynchronously(getPlugin(), new NameColorRunnable(this), 10, 10);

        Listener[] listeners = {

                // Entity
                new EntityDamageByEntityListener(this),
                new EntityDamageListener(this),
                new EntityRegainHealthListener(this),
                new EntitySpawnListener(this),

                // Inventory
                new InventoryClickListener(this),
                new InventoryMoveItemListener(this),

                // Player
                new AsyncPlayerChatListener(this),
                new AsyncPlayerPreLoginListener(this),
                new FoodLevelChangeListener(this),
                new PlayerBucketEmptyListener(this),
                new PlayerDeathListener(this),
                new PlayerDropItemListener(this),
                new PlayerInteractEntityListener(this),
                new PlayerInteractListener(this),
                new PlayerItemConsumeListener(this),
                new PlayerJoinListener(this),
                new PlayerMoveListener(this),
                new PlayerPickupItemListener(this),
                new PlayerQuitListener(this),
                new PlayerTeleportListener(this),

                // Potion
                new PotionSplashListener(this),

                // Projectile
                new ProjectileHitListener(this),
                new ProjectileLaunchListener(this),

                // World
                new BlockBreakListener(this),
                new BlockFromToListener(this),
                new BlockPlaceListener(this)
        };

        Arrays.stream(listeners).forEach(listener -> getManager().registerListener(listener,this));

        MCommand[] commands = {
                new AcceptCommand(this, "accept"),
                new ArenaCommand(this, "arena"),
                new BuildCommand(this, "build"),
                new DuelCommand(this, "duel"),
                new KitCommand(this, "kit"),
                new InventoryCommand(this, "inventory"),
                new PingCommand(this, "ping"),
                new PracticeCommand(this, "practice"),
                new SettingsCommand(this, "settings"),
                new SpectateCommand(this, "spectate")
        };

        Arrays.stream(commands).forEach(mCommand -> getManager().registerCommand(mCommand));

        protocolManager.addPacketListener(new EnderpearlSound(this));
    }

    @Override
    public void onDisable() {
        this.saveConfig();

        this.cooldownTask.cancel();

        this.profileManager.shutdown();

        INSTANCE = null;
    }
}
