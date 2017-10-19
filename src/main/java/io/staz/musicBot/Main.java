package io.staz.musicBot;

import io.staz.musicBot.api.Configuration;
import io.staz.musicBot.configSettings.FlatConfig;
import io.staz.musicBot.configSettings.InstanceConfig;
import io.staz.musicBot.instances.Instance;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {

    public static final boolean DEBUG = true;

    public static final Logger logger = LogManager.getLogger("Main");

    private static Configuration<FlatConfig> config;
    private static FlatConfig flat;

    private static final Map<UUID, Instance> instances = new HashMap<>();

    public static void main(String[] args) throws LoginException, RateLimitedException, InstantiationException, InterruptedException, IllegalAccessException, NoSuchFieldException, IOException {
        logger.info("Initialing....");
        logger.info("Is Debugging Mode? " + DEBUG);
        logger.info("Version: indev"); // TODO

        logger.info("Loading configuration...");
        config = new Configuration<FlatConfig>(new File("test.yml"), "/config.yml", FlatConfig.class);
        flat = config.getValue();

        if (flat != null) {
            logger.info("Loading instances...");
            List<InstanceConfig> configs = flat.instances;
            for (InstanceConfig config : configs) {
                logger.info("Loading " + config.uuid);
                instances.put(UUID.fromString(config.uuid), new Instance(config));
            }
        }else {
            flat = new FlatConfig();

            Scanner scanner = new Scanner(System.in);
            logger.info("This is your first time using the Music bot!");
            logger.info("Please follow the instructions here to create a Discord token.");
            logger.info("TODO");

            JDA jda = null;

            while (jda == null) {
                logger.info("Please enter the token here:");
                String token = scanner.next();
                logger.info("Attempting to use token....");
                try {
                    jda = new JDABuilder(AccountType.BOT).setToken(token).buildBlocking();
                } catch (LoginException e) {
                    logger.info(e.getMessage());
                }
            }
            InstanceConfig config = new InstanceConfig();
            config.uuid =  UUID.randomUUID().toString();

            flat.instances.add(config);
            instances.put(UUID.fromString(config.uuid), new Instance(config));
        }

    }

    public static File getDirectory(String name) throws IOException {
        File file =  new File(
                System.getProperty("user.dir") +File.separator + name
        );
        file.mkdirs();
        return file;
    }
}
