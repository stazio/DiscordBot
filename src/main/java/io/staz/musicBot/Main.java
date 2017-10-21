package io.staz.musicBot;

import io.staz.musicBot.api.Configuration;
import io.staz.musicBot.configSettings.FlatConfig;
import io.staz.musicBot.configSettings.InstanceConfig;
import io.staz.musicBot.instances.Instance;
import lombok.Getter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;

public class Main {

    public static final boolean DEBUG = Main.class.getPackage().getImplementationVersion() != null;
    @Getter
    public static final String version = Main.class.getPackage().getImplementationVersion() != null ? Main.class.getPackage().getImplementationVersion() : "DEV";

    public static final Logger logger = LogManager.getLogger("Main");
    private static Instance instance;
    private static Configuration<FlatConfig> config;
    private static FlatConfig flat;

    public static void main(String[] args) throws LoginException, RateLimitedException, InstantiationException, InterruptedException, IllegalAccessException, NoSuchFieldException, IOException {
        logger.info("Initialing....");
        logger.info("Is Debugging Mode? " + DEBUG);
        logger.info("Version: " + version); // TODO

        logger.info("Loading configuration...");
        config = new Configuration<FlatConfig>(new File("config.yml"), null, FlatConfig.class);
        flat = config.getValue();

        if (flat != null) {
            instance = new Instance(flat.instance);
        } else {
            flat = new FlatConfig();

            Scanner scanner = new Scanner(System.in);
            logger.info("This is your first time using the Music bot!");
            logger.info("Please follow the instructions here to create a Discord token.");
            logger.info("TODO");

            JDA jda = null;
            String token = null;
            while (jda == null) {
                logger.info("Please enter the token here:");
                token = scanner.next();
                logger.info("Attempting to use token....");
                try {
                    jda = new JDABuilder(AccountType.BOT).setToken(token).buildBlocking();
                } catch (LoginException e) {
                    logger.info(e.getMessage());
                }
            }
            InstanceConfig config = new InstanceConfig();
            config.uuid = UUID.randomUUID().toString();
            config.token = token;
            flat.instance = config;
            Main.config.save(flat);
            instance = new Instance(config);
        }

    }
}
