package io.staz.musicBot.plugin;

import io.staz.musicBot.guild.GuildConnection;
import io.staz.musicBot.instances.Instance;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.dv8tion.jda.core.JDA;

import java.util.ArrayList;
import java.util.HashMap;

@RequiredArgsConstructor
public class PluginManager {

    @Getter
    private final GuildConnection guild;

    @Getter
    private HashMap<String, Plugin> plugins = new HashMap<>();

    @SneakyThrows
    public void loadAllPlugins() {
        for (PluginInfo info : PluginLoader.findPlugins(true)) {

            guild.getLogger().info("Loading Plugin: " + info.name);
            Class<? extends Plugin> pluginClass = PluginLoader.findClass(info.mainClass);
            Plugin plugin = pluginClass.
                    getDeclaredConstructor(GuildConnection.class, PluginInfo.class).
                    newInstance(guild, info);
            plugins.put(info.id, plugin);
            plugin.onLoad();
        }
    }

    public void registerEventListeners(JDA jda) {
        getPlugins().values().forEach(jda::addEventListener);
    }

    public void saveAllPlugins() {
        getPlugins().values().forEach(Plugin::onSave);
    }
}
