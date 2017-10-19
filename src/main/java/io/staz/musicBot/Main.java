package io.staz.musicBot;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.staz.musicBot.api.Configuration;
import io.staz.musicBot.configSettings.FlatConfig;
import io.staz.musicBot.configSettings.InstanceConfig;
import io.staz.musicBot.instances.Instance;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

        logger.info("Loading instances...");
        List<InstanceConfig> configs = flat.instances;
        for (InstanceConfig config : configs) {
            logger.info("Loading " + config.uuid);
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
