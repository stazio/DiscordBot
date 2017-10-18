package io.staz.musicBot.plugin;

import io.staz.musicBot.Main;
import io.staz.musicBot.command.Command;
import io.staz.musicBot.command.CommandManager;
import io.staz.musicBot.instances.Instance;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

@RequiredArgsConstructor
public class PluginManager {
    @Getter
    private final Instance instance;

    @Getter
    private final HashMap<String, Plugin> plugins = new HashMap<>();

    public Plugin getPlugin(String name) {
        return plugins.get(name);
    }

    public PluginManager addPlugin(Plugin plugin) {
        this.plugins.put(plugin.getID(), plugin);
        return this;
    }

    public PluginManager createPlugin(Class<?extends  Plugin> klass) throws IllegalAccessException, InstantiationException, NoSuchFieldException {
        Plugin plugin = klass.newInstance();
        instance.getLogger().info("Loading plugin: " + plugin.getName() + " - " + plugin.getID());

        Field field = Plugin.class.getDeclaredField("instance");
        field.setAccessible(true);
        field.set(plugin, instance);

        CommandManager manager = instance.getCommandManager();
        for (Command command :
                plugin.commands()) {
            manager.addCommand(command);
        }

        return this;
    }

    public PluginManager removePlugin(Plugin plugin) {
        this.plugins.remove(plugin.getID());
        return this;
    }

    public void loadAll() throws IllegalAccessException, InstantiationException, NoSuchFieldException, IOException {
        Main.logger.info("Loading plugins...");
        loadPlugins(false);
        Main.logger.info("Found " + pluginClasses.size() + " plugin(s).");
        for (Class<? extends Plugin> pluginClass :
                pluginClasses) {
            Main.logger.info("Loading plugin class: " + pluginClass);
            this.createPlugin(pluginClass);
        }
        Main.logger.info("Loaded plugins...");
    }

    private static Set<Class<? extends Plugin>> pluginClasses = null;
    @SneakyThrows
    private static void loadPlugins(boolean reload) throws IOException {
        if (reload || pluginClasses == null) {
            Reflections reflections = new Reflections();
            pluginClasses = reflections.getSubTypesOf(Plugin.class);
        }
    }
}
