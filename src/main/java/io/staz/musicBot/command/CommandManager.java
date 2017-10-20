package io.staz.musicBot.command;

import io.staz.musicBot.guild.GuildConnection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.HashMap;

@RequiredArgsConstructor
public class CommandManager {
    private final GuildConnection guild;

    @Getter
    private HashMap<String, Command> commandNameMap = new HashMap<>();

    public CommandManager addCommand(Command command) {
        guild.getLogger().info("Adding command: " + command.getName());
        this.commandNameMap.put(command.getName(), command);

        for (String alias : command.getAliases()) {
            commandNameMap.put(alias, command);
        }
        return this;
    }

    // A easy way to add many commands at once.
    public void addCommands(Command[] commands) {
        for (Command command :
                commands) {
            addCommand(command);
        }
    }

    public CommandManager removeCommand(Command command) {
        guild.getLogger().info("Removing command: " + command.getName());
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


    // Message Processing
    @SubscribeEvent
    private void onMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getContent();
        if (message.indexOf("!") == 0 && !event.getAuthor().isBot() && guild.getGuild().equals(event.getGuild())) {
            // Get the command we are sending
            String command = message.split(" ")[0].substring(1);
            Command commandAction = getCommand(command);

            // This command exists
            if (commandAction != null) {
                guild.getLogger().info("Executing command: " + command);

                // Do the commands action
                Object response = commandAction.onCommand(command, message.substring(command.length() + 1).trim(),
                        event.getMessage(), event);

                // The command wants us to send a response
                if (response != null) {

                    // Get the channel
                    MessageChannel channel = event.getChannel(); // TODO does this not work?

                    // Create the message
                    MessageBuilder responseMessage = new MessageBuilder();

                    if (response instanceof Iterable) {
                        ((Iterable<?>) response).forEach(responseMessage::append);
                    }else
                        responseMessage.append(response);

                    // Send it to the user
                    responseMessage.buildAll(MessageBuilder.SplitPolicy.NEWLINE).forEach(
                            message1 -> channel.sendMessage(message1).submit()
                    );

                }
            } else
                // Command not found.
                guild.getLogger().info("Command " + command + " not found.");

        }
    }


}