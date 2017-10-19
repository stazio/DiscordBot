package io.staz.musicBot.plugin;

import io.staz.musicBot.instances.Instance;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.HashMap;

@RequiredArgsConstructor
public class PluginManager {

    @Getter
    private final Instance instance;

    @Getter
    private HashMap<String, Plugin> plugins = new HashMap<>();

    @SneakyThrows
    public void loadAllPlugins() {
        ArrayList<PluginInfo> infos = PluginLoader.findPlugins(true);
        for (PluginInfo info : infos
                ) {

            instance.getLogger().info("Loading Plugin: " + info.name);
            Class<? extends Plugin> pluginClass = PluginLoader.findClass(info.mainClass);
            Plugin plugin = pluginClass.
                    getDeclaredConstructor(Instance.class, PluginInfo.class).
                    newInstance(instance, info);
            plugins.put(info.id, plugin);
            plugin.onLoad();
        }
    }
}
