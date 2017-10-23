package io.staz.musicBot.command;

import io.staz.musicBot.plugin.Plugin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Collection;

@RequiredArgsConstructor
public abstract class AbstractCommand implements ICommand {

    @Getter
    private final Plugin plugin;

    public abstract Object onCommand(String command, String message, Message eventMessage, MessageReceivedEvent event);

    public abstract Collection<String> getAliases();

    public abstract String getName();
}
