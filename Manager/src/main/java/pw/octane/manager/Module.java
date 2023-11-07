package pw.octane.manager;

import lombok.Data;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public @Data abstract class Module {

    private OctaneManager manager;
    private JavaPlugin plugin;
    private ZipFile zipFile;
    private ModuleInformation moduleInformation;

    private File directory, configFile;
    private FileConfiguration config;

    public abstract void onEnable();
    public abstract void onDisable();

    protected void init(OctaneManager manager, JavaPlugin plugin, ZipFile zipFile, ModuleInformation moduleInformation) {
        this.manager = manager;
        this.plugin = plugin;
        this.zipFile = zipFile;
        this.moduleInformation = moduleInformation;

        this.directory = new File(getPath());
        this.configFile = new File(getPath() + "/config.yml");

        this.config = new YamlConfiguration();

        if(!directory.exists()) {
            directory.mkdirs();
        }

        if(configFile.exists()) {
            try {
                config.load(configFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            InputStream is = getResource("config.yml");
            if(is != null) {
                try {
                    config.load(is);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public InputStream getResource(String s) {
        ZipEntry ze = zipFile.getEntry(s);
        if(ze != null) {
            try {
                return zipFile.getInputStream(ze);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public String getPath() {
        return plugin.getDataFolder().getAbsolutePath() + "/modules/" + moduleInformation.getName();
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveDefaultConfig() {
        InputStream is = getResource("config.yml");
        if(!configFile.exists() && is != null) {
            try {
                config.load(is);
                configFile.createNewFile();
                config.save(configFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
