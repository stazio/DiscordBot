package io.staz.musicBot.guild;

import io.staz.musicBot.audio.AudioManager;
import io.staz.musicBot.command.CommandManager;
import io.staz.musicBot.instances.Instance;
import io.staz.musicBot.plugin.PluginManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.entities.Guild;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RequiredArgsConstructor
public class GuildConnection {
    @Getter
    private final Guild guild;

    @Getter
    private final Instance instance;

    @Getter
    private Logger logger;

    @Getter
    private CommandManager commandManager;

    @Getter
    private PluginManager pluginManager;

    @Getter
    private AudioManager audioManager;

    public void onLoad() {
        logger = LogManager.getLogger("Guild " + guild.getName());
        this.commandManager = new CommandManager(this);
        this.pluginManager = new PluginManager(this);
        this.audioManager = new AudioManager(this);

        getInstance().getJda().addEventListener(commandManager, pluginManager);

        logger.info("Loading plugins...");
        this.pluginManager.loadAllPlugins();
    }

    public void onUnload() {
        logger.info("Saving plugins...");
        this.pluginManager.saveAllPlugins();
    }
}
