package io.staz.musicBot.command;

import io.staz.musicBot.plugin.Plugin;

import java.util.Collection;
import java.util.Collections;

public abstract class BadCommand extends AbstractCommand {

    private final String name;

    public BadCommand(Plugin plugin, String name) {
        super(plugin);
        this.name = name;
    }

    @Override
    public Collection<String> getAliases() {
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return name;
    }
}
