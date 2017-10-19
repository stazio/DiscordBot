package io.staz.musicBot.instances;

import io.staz.musicBot.audio.AudioAPI;
import io.staz.musicBot.command.CommandManager;
import io.staz.musicBot.configSettings.InstanceConfig;
import io.staz.musicBot.plugin.PluginManager;
import lombok.Getter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.io.IOException;


public class Instance {
    @Getter
    private final Logger logger;
    @Getter
    private InstanceConfig config;
    @Getter
    private JDA jda;

    @Getter
    private PluginManager pluginManager;

    @Getter
    private CommandManager commandManager;

    @Getter
    private AudioAPI audioAPI;

    public Instance(InstanceConfig config) throws IllegalAccessException, InstantiationException, LoginException, InterruptedException, RateLimitedException, NoSuchFieldException, IOException {
        this.config = config;
        this.logger = LogManager.getLogger("Instance " + config.uuid);
        logger.info("Loading Instance: " + logger.getName());

        init();
        connect();
    }

    public void init() throws InstantiationException, IllegalAccessException, NoSuchFieldException, IOException {
        this.commandManager = new CommandManager(this);
        this.pluginManager = new PluginManager(this);
        this.audioAPI = new AudioAPI(this);

        this.pluginManager.loadAllPlugins();
    }


    public void connect() throws LoginException, InterruptedException, RateLimitedException {
        logger.info("Connecting with Token [redacted]");

        jda = new JDABuilder(AccountType.BOT).
                setAutoReconnect(true).
                setStatus(OnlineStatus.ONLINE).
                setToken(config.token).
                setEventManager(new AnnotatedEventManager()).
                addEventListener(this).
                buildBlocking();
        logger.info("Connected!");

        // Todo Move this
        logger.debug("Registering listeners...");
        this.jda.addEventListener(this.pluginManager, this.commandManager);
        this.pluginManager.getPlugins().values().forEach((plugin) -> this.getJda().addEventListener(plugin));
    }

    public void disconnect() {
        logger.info("Disconnecting...");
        jda.shutdown();
        logger.info("Disconnected.");
    }
}
