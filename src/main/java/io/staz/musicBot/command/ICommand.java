package io.staz.musicBot.command;

import io.staz.musicBot.plugin.Plugin;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Collection;

public interface ICommand {

    Plugin getPlugin();
    String getName();
    Collection<String> getAliases();

    Object onCommand(String command, String message, Message eventMessage, MessageReceivedEvent event);
}
