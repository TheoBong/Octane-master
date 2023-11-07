package pw.octane.manager;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import pw.octane.manager.commands.*;
import pw.octane.manager.networking.mongo.Mongo;
import pw.octane.manager.networking.redis.RedisPublisher;
import pw.octane.manager.networking.redis.RedisSubscriber;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class OctaneManager extends JavaPlugin {

    public static OctaneManager INSTANCE;

    private @Getter Mongo mongo;

    private @Getter RedisPublisher redisPublisher;
    private @Getter RedisSubscriber redisSubscriber;


    private @Getter Set<Module> loadedModules;
    private Set<MCommand> commands;
    private Map<Module, List<Listener>> listeners;
    private CommandMap commandMap;

    @Override
    public void onEnable() {
        INSTANCE = this;

        this.saveDefaultConfig();

        // Create command map.
        commands = new HashSet<>();
        listeners = new HashMap<>();

        // Mongo
        if (getConfig().getBoolean("networking.mongo.enabled")) {
            this.mongo = new Mongo(this);
        }

        // Redis
        if (getConfig().getBoolean("networking.redis.enabled")) {
            redisPublisher = new RedisPublisher(new Jedis(getConfig().getString("networking.redis.host"), getConfig().getInt("networking.redis.port")), this);
            redisSubscriber = new RedisSubscriber(new Jedis(getConfig().getString("networking.redis.host"), getConfig().getInt("networking.redis.port")), this);
        }

        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadedModules = new HashSet<>();

        File dir = new File(this.getDataFolder().getAbsolutePath() + "/modules");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Loads all modules found in the "OctaneManager/modules" directory.
        loadModule(null, null);

        // Register default Manager commands.
        registerCommand(new CloseCommand(this));
        registerCommand(new LoadCommand(this));
        registerCommand(new ModuleInfoCommand(this));
        registerCommand(new ModulesCommand(this));
        registerCommand(new UnloadCommand(this));
    }

    @Override
    public void onDisable() {

        for(Module module : loadedModules) {
            module.onDisable();
        }

        INSTANCE = null;
    }

    /**
     * Register a command.
     * @param mCommand
     */
    public OctaneManager registerCommand(MCommand mCommand) {
        commands.add(mCommand);
        commandMap.register(mCommand.getName(), mCommand);
        return this;
    }

    public OctaneManager registerListener(Listener listener, Module module) {
        List<Listener> list = listeners.get(module);
        
        if(list == null) {
            list = new ArrayList<>();
            listeners.put(module, list);
        }

        list.add(listener);
        getServer().getPluginManager().registerEvents(listener, this);
        return this;
    }

    public OctaneManager unregisterListener(Listener listener) {
        HandlerList.unregisterAll(listener);
        return this;
    }

    /**
     * Loads a module by name defined in its module.yml.
     * You can leave both args null to load all modules.
     * @param name The name of the module.
     * @param player The player that made this request.
     */
    public void loadModule(String name, CommandSender player) {
        if(name != null) {
            for(Module module : loadedModules) {
                if(module.getModuleInformation().getName().equalsIgnoreCase(name)) {
                    player.sendMessage(ChatColor.RED + "That module is already loaded.");
                    return;
                }
            }
        }

        File dir = new File(this.getDataFolder().getAbsolutePath() + "/modules");
        for (File file : dir.listFiles()) {
            if(!file.isDirectory()) {
                try {
                    ZipFile zipFile = new ZipFile(file);
                    ZipEntry zipEntry = zipFile.getEntry("module.yml");

                    YamlConfiguration config = new YamlConfiguration();
                    config.load(zipFile.getInputStream(zipEntry));

                    ModuleInformation mi = new ModuleInformation(
                            config.getString("name"),
                            config.getString("version"),
                            config.getString("description"),
                            config.getString("author"),
                            config.getString("main"),
                            config.getStringList("depend"));

                    URLClassLoader classLoader = new URLClassLoader(new URL[]{file.toURI().toURL()}, this.getClass().getClassLoader());
                    Class classToLoad = Class.forName(mi.getMainClass(), true, classLoader);
                    Module module = (Module) classToLoad.asSubclass(Module.class).newInstance();

                    if (name != null && player != null) {
                        if (mi.getName().equalsIgnoreCase(name)) {
                            if (mi.getName() == null || mi.getVersion() == null || mi.getMainClass() == null) {
                                player.sendMessage(ChatColor.RED + "You must specify a name, version, and main class for your module in the 'module.yml' file.");
                                break;
                            }

                            module.init(this, this, zipFile, mi);
                            module.onEnable();

                            loadedModules.add(module);

                            player.sendMessage(ChatColor.GREEN + "Module " + ChatColor.WHITE + mi.getName() + ChatColor.GREEN + " has been loaded!");
                            break;
                        }
                    } else {
                        if (mi.getName() != null && mi.getVersion() != null && mi.getMainClass() != null) {
                            module.init(this, this, zipFile, mi);
                            module.onEnable();

                            loadedModules.add(module);
                        } else {
                            throw new IllegalArgumentException("You must specify a name, version, and main class for your module in the 'module.yml' file.");
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Unloads a module by the name specified in module.yml
     * @param name The name of the module.
     * @param player The player that made this request.
     */
    public void unloadModule(String name, CommandSender player) {
        Module module = null;
        for(Module m : loadedModules) {
            if(m.getModuleInformation().getName().equalsIgnoreCase(name)) {
                module = m;
            }
        }

        if(module != null) {
            module.onDisable();

            if(listeners.get(module) != null) {
                for (Listener listener : listeners.get(module)) {
                    unregisterListener(listener);
                }
                listeners.get(module).clear();
            }

            for(MCommand mCommand : commands) {
                if(mCommand.getModule() != null && mCommand.getModule().equals(module)) {
                    Bukkit.getPluginCommand(mCommand.getName()).setExecutor((commandSender, command, s, strings) -> {
                        commandSender.sendMessage(ChatColor.RED + "This command has been unregistered.");
                        return false;
                    });
                }
            }

            loadedModules.remove(module);
            player.sendMessage(ChatColor.GREEN + "Module " + ChatColor.WHITE + module.getModuleInformation().getName() + ChatColor.GREEN + " has been successfully unloaded!");
        } else {
            player.sendMessage(ChatColor.RED + "The module you specified was either not loaded or not found.");
        }
    }

    public static OctaneManager get() {
        return INSTANCE;
    }
}
