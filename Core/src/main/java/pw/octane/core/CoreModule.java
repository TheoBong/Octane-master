package pw.octane.core;

import lombok.Getter;
import pw.octane.core.commands.general.ListCommand;
import pw.octane.core.commands.general.ReportCommand;
import pw.octane.core.commands.general.SettingsCommand;
import pw.octane.core.commands.messaging.IgnoreCommand;
import pw.octane.core.commands.messaging.MessageCommand;
import pw.octane.core.commands.messaging.UnignoreCommand;
import pw.octane.core.commands.moderation.CheckPunishmentsCommand;
import pw.octane.core.commands.moderation.PunishCommand;
import pw.octane.core.commands.moderation.TempPunishCommand;
import pw.octane.core.commands.moderation.UnpunishCommand;
import pw.octane.core.commands.ranks.AddRankCommand;
import pw.octane.core.commands.ranks.GetRanksCommand;
import pw.octane.core.commands.ranks.RankCommand;
import pw.octane.core.commands.ranks.RemoveRankCommand;
import pw.octane.core.commands.staff.*;
import pw.octane.core.commands.tags.AddTagCommand;
import pw.octane.core.commands.tags.RemoveTagCommand;
import pw.octane.core.commands.tags.TagCommand;
import pw.octane.core.commands.tags.TagsCommand;
import pw.octane.core.listeners.player.AsyncPlayerChatListener;
import pw.octane.core.listeners.player.PlayerJoinListener;
import pw.octane.core.listeners.player.PlayerPreLoginListener;
import pw.octane.core.listeners.player.PlayerQuitListener;
import pw.octane.core.listeners.world.LeavesDecayListener;
import pw.octane.core.listeners.world.WeatherChangeListener;
import pw.octane.core.networking.CoreRedisMessageListener;
import pw.octane.core.profiles.ProfileManager;
import pw.octane.core.punishments.PunishmentManager;
import pw.octane.core.ranks.RankManager;
import pw.octane.core.server.CoreServer;
import pw.octane.core.tags.TagManager;
import pw.octane.manager.MCommand;
import pw.octane.manager.Module;
import xyz.leuo.gooey.Gooey;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class CoreModule extends Module {
    public static CoreModule INSTANCE;

    public Gooey gooey;

    @Getter private ProfileManager profileManager;
    @Getter private PunishmentManager punishmentManager;
    @Getter private RankManager rankManager;
    @Getter private TagManager tagManager;

    @Getter private CoreServer coreServer;

    private CoreRedisMessageListener coreRedisMessageListener;

    @Override
    public void onEnable() {
        INSTANCE = this;

        this.saveDefaultConfig();

        this.coreServer = new CoreServer(getConfig());

        this.gooey = new Gooey(getPlugin());

        this.profileManager = new ProfileManager(this);
        this.punishmentManager = new PunishmentManager(this);
        this.rankManager = new RankManager(this);
        this.tagManager = new TagManager(this);

        new AsyncPlayerChatListener(this);
        new PlayerJoinListener(this);
        new PlayerPreLoginListener(this);
        new PlayerQuitListener(this);

        getManager().registerListener(new LeavesDecayListener(this), this);
        getManager().registerListener(new WeatherChangeListener(this), this);

        // General
        getManager().registerCommand(new ListCommand(this, "list"));
        getManager().registerCommand(new ReportCommand(this, "report"));
        getManager().registerCommand(new SettingsCommand(this, "coresettings"));

        // Messaging
        getManager().registerCommand(new IgnoreCommand(this, "ignore"));
        getManager().registerCommand(new MessageCommand(this, "message"));
        getManager().registerCommand(new UnignoreCommand(this, "unignore"));

        // Moderation
        getManager().registerCommand(new CheckPunishmentsCommand(this, "checkpunishments"));
        getManager().registerCommand(new PunishCommand(this, "punish"));
        getManager().registerCommand(new TempPunishCommand(this, "temppunish"));
        getManager().registerCommand(new UnpunishCommand(this, "unpunish"));

        // Ranks
        getManager().registerCommand(new AddRankCommand(this, "addrank"));
        getManager().registerCommand(new GetRanksCommand(this, "getranks"));
        getManager().registerCommand(new RankCommand(this, "rank"));
        getManager().registerCommand(new RemoveRankCommand(this, "removerank"));

        // Staff
        getManager().registerCommand(new BroadcastCommand(this, "broadcast"));
        getManager().registerCommand(new FeedCommand(this, "feed"));
        getManager().registerCommand(new GamemodeCommand(this, "gamemode"));
        getManager().registerCommand(new HealCommand(this, "heal"));
        getManager().registerCommand(new MoreCommand(this, "more"));
        getManager().registerCommand(new SudoCommand(this, "sudo"));

        // Tags
        getManager().registerCommand(new AddTagCommand(this, "addtag"));
        getManager().registerCommand(new RemoveTagCommand(this, "removetag"));
        getManager().registerCommand(new TagCommand(this, "tag"));
        getManager().registerCommand(new TagsCommand(this, "tags"));

        this.coreRedisMessageListener = new CoreRedisMessageListener(this);

        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {

                    public java.security.cert.X509Certificate[] getAcceptedIssuers()
                    {
                        return null;
                    }
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
                    {
                        //No need to implement.
                    }
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
                    {
                        //No need to implement.
                    }
                }
        };

        try
        {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        profileManager.shutdown();

        coreRedisMessageListener.close();

        coreServer.export(getConfig());

        this.saveConfig();
    }
}
