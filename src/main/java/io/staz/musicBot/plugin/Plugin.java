package io.staz.musicBot.plugin;

import io.staz.musicBot.api.Configuration;
import io.staz.musicBot.command.CommandManager;
import io.staz.musicBot.guild.GuildConnection;
import io.staz.musicBot.instances.Instance;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Logger;

import java.io.File;

@RequiredArgsConstructor
public abstract class Plugin {

    @Getter
    private final GuildConnection guild;
    @Getter
    private final PluginInfo info;

    public Logger getLogger() {
        return guild.getLogger();
    }

    public <T> Configuration<T> getConfig(String name, Class<T> klass) {
        return new
                Configuration<T>(new File(name), "/" + name, klass);
    }

    public <T> Configuration<T> getDefaultConfig(Class<T> klass) {
        return getConfig("/" + info.id + ".yml", klass);
    }

    public void onLoad() {
    }

    public void onSave() {
    }

    public CommandManager getCommandManager() {
        return guild.getCommandManager();
    }
}
