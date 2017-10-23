package io.staz.musicBot.command;

import io.staz.musicBot.plugin.Plugin;
import lombok.Builder;
import lombok.Getter;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Collection;
import java.util.Collections;

@Builder
public class Command implements ICommand {

    @Getter
    private String name;

    @Getter
    @Builder.Default
    private Collection<String> aliases = Collections.emptyList();

    @Getter
    private Plugin plugin;

    @Getter
    private CommandAction action;

    @Override
    public Object onCommand(String command, String message, Message eventMessage, MessageReceivedEvent event) {
        return null;
    }

    public interface CommandAction {
        Object onCommand(String command, String message, Message eventMessage, MessageReceivedEvent event);
    }
}
