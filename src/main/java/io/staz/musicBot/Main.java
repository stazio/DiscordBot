package io.staz.musicBot;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
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

    private static Config config;

    private static final Map<UUID, Instance> instances = new HashMap<>();

    public static void main(String[] args) throws LoginException, RateLimitedException, InstantiationException, InterruptedException, IllegalAccessException, NoSuchFieldException, IOException {
        logger.info("Initialing....");
        logger.info("Is Debugging Mode? " + DEBUG);
        logger.info("Version: indev"); // TODO

        logger.info("Loading configuration...");
        config = ConfigFactory.load();

        logger.info("Loading instances...");
        List<? extends Config> configs = config.getConfigList("instances");
        for (Config config : configs) {
            logger.info("Loading " + config.getString("uuid"));
            instances.put(UUID.fromString(config.getString("uuid")), new Instance(config));
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
