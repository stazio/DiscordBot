package io.staz.musicBot.command;

import io.staz.musicBot.instances.Instance;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.HashMap;

@RequiredArgsConstructor
public class CommandManager  {
    private final Instance instance;

    private HashMap<String, Command> commandNameMap = new HashMap<>();

    public CommandManager addCommand(Command command) {
        instance.getLogger().info("Adding command: " + command.getName() + ". Aliases: " +
                command.getAliases().toString());
        this.commandNameMap.put(command.getName(), command);

        for (String alias : command.getAliases()) {
            commandNameMap.put(alias, command);
        }
        return this;
    }

    public CommandManager removeCommand(Command command) {
        commandNameMap.remove(command.getName());
        for (String alias :
                command.getAliases()) {
            commandNameMap.remove(alias);
        }
        return this;
    }

    public CommandManager removeCommand(String aliasOrName) {
        return this.removeCommand(this.getCommand(aliasOrName));
    }

    public Command getCommand(String nameOrAlias) {
        return this.commandNameMap.get(nameOrAlias);
    }

    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent event)
    {
        String message = event.getMessage().getContent();
        if (message.indexOf("!") == 0) {
            String command = message.split(" ")[0].substring(1);
            Command commandAction = getCommand(command);
            if (commandAction != null) {
                instance.getLogger().info("Executing command: " + command);
                Object response = commandAction.onCommand(command, message.substring(command.length() + 1).trim(), event.getMessage(), event);
                if (response != null) {
                    instance.getLogger().info("Sending response: " + response);

                    MessageChannel channel;
                    channel = event.getPrivateChannel() != null ?
                            event.getPrivateChannel() :
                            event.getTextChannel();
                    if (response instanceof String) {
                        channel.sendMessage((String) response).submit(true);
                    }else if (response instanceof Message)
                        channel.sendMessage((Message) response).submit(true);
                    else if (response instanceof Iterable) {
                        for (Object o :
                                (Iterable)response) {
                            if (o instanceof String) {
                                channel.sendMessage((String) o).submit(true);
                            }else if (o instanceof Message)
                                channel.sendMessage((Message) o).submit(true);
                        }
                    }
                }
            }else
                instance.getLogger().info("Command " +command + " not found.");
        }
    }
}