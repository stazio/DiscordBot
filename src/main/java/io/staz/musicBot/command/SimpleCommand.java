package io.staz.musicBot.command;

import io.staz.musicBot.plugin.Plugin;

public abstract class SimpleCommand extends Command {

    private final String name;

    public SimpleCommand(Plugin plugin, String name) {
        super(plugin);
        this.name = name;
    }


    @Override
    public String[] getAliases() {
        return new String[]{};
    }

    @Override
    public String getName() {
        return name;
    }
}
