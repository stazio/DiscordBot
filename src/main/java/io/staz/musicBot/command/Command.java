package io.staz.musicBot.command;

import io.staz.musicBot.plugin.Plugin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

@RequiredArgsConstructor
public abstract class Command {

    @Getter
    private final Plugin plugin;

    public abstract Object onCommand(String command, String message, Message eventMessage, MessageReceivedEvent event);

    public abstract String[] getAliases();

    public abstract String getName();
}
