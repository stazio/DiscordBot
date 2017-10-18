package io.staz.musicBot.plugin;

import io.staz.musicBot.command.Command;
import io.staz.musicBot.instances.Instance;
import lombok.Getter;
import net.dv8tion.jda.core.hooks.EventListener;

public abstract class Plugin {

    @Getter
    private Instance instance;

    public void registerCommand(Command command) {
        this.instance.getCommandManager().addCommand(command);
    }

    public void unregisterCommand(Command command) {
        this.instance.getCommandManager().removeCommand(command);
    }

    public void registerEventListener(Object listener) {
        this.instance.getJda().addEventListener(listener);
    }

    public void onLoad() {

    }

    public void onSave() {

    }

    public void onStop() {

    }

    public abstract String getID();
    public abstract String getName();
    public abstract String[] configs();
    public abstract Command[] commands();
}
