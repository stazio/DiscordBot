package io.staz.musicBot.instances;

import io.staz.musicBot.Main;
import io.staz.musicBot.configSettings.InstanceConfig;
import io.staz.musicBot.guild.GuildConnection;
import lombok.Getter;
import lombok.SneakyThrows;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.events.DisconnectEvent;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;


public class Instance  extends ListenerAdapter{
    @Getter
    private final Logger logger;
    @Getter
    private InstanceConfig config;
    @Getter
    private JDA jda;

    private HashSet<GuildConnection> guilds = new HashSet<>();

    public Instance(InstanceConfig config) {
        this.config = config;
        this.logger = LogManager.getLogger("Instance " + config.name);
        logger.info("Loading Instance: " + config.name);

        connect();
    }

    @SneakyThrows
    public void connect() {
        if (Main.DEBUG)
            logger.info("Connecting with token " + config.token);
        else
            logger.info("Connecting...");

        this.jda = new JDABuilder(AccountType.BOT).
                setAutoReconnect(true).
                setStatus(OnlineStatus.ONLINE).
                setToken(config.token).
                setEventManager(new AnnotatedEventManager()).
                addEventListener(this).
                buildAsync();
    }

    public void disconnect() {
        logger.info("Disconnecting...");
        jda.shutdown();
    }

    @Override
    @SubscribeEvent
    public void onReady(ReadyEvent event) {
        logger.info("Connected.");
        getJda().getGuilds().forEach(guild -> {
            GuildConnection guildConn = new GuildConnection(guild, this);
            guilds.add(guildConn);
            guildConn.onLoad();
        });
    }

    @SubscribeEvent
    @Override
    public void onDisconnect(DisconnectEvent event) {
        logger.info("Disconnected.");
        guilds.forEach(GuildConnection::onUnload);
    }
}
