package io.staz.musicBot.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.staz.musicBot.Main;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.File;

public class Configuration<T> {

    private final File file;
    private String resource;

    private
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    @Getter
    @Setter
    private T value;

    @SneakyThrows
    public Configuration(File file, String resource, Class<T> klass) {
        this.file = file;
        this.resource = resource;

        if (this.file.exists())
            value = mapper.readValue(file, klass);
        else {
            if (resource != null) {
                value = mapper.readValue(
                        Main.class.getResource(resource),
                        klass
                );
            }
        }
    }

    public Configuration(File file, Class<T> klass) {
        this(file, null, klass);
    }

    public void save() {
        save(value);
    }

    @SneakyThrows
    public void save(T value) {
        mapper.writeValue(file, value);
    }
}
