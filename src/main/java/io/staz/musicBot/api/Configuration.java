package io.staz.musicBot.api;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;

public class Configuration {

    private Config config;

    private final File file;
    private String resourceName;

    public Configuration(File file, String resourceName) {
        this.file = file;
        this.resourceName = resourceName;

        this.config = ConfigFactory.parseFile(file);
    }

    @SneakyThrows
    public void load() {
        if (!this.file.exists()) {
            if (this.resourceName != null) {
                URL input = getClass().getResource(resourceName);
                FileUtils.copyURLToFile(input, file);
            }
        }

        if (this.file.exists())
            this.config = ConfigFactory.parseFile(file);
        else
            this.config = ConfigFactory.empty();
    }

    public void set(String name, ConfigValue value) {
    }

    public Configuration(File file) {
        this(file, null);
    }
}
