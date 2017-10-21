package io.staz.musicBot.command;

import io.staz.musicBot.guild.GuildConnection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.HashMap;

@RequiredArgsConstructor
public class CommandManager {
    private final GuildConnection guild;

    @Getter
    private HashMap<String, ICommand> commandNameMap = new HashMap<>();
    private boolean commandable = true;

    public CommandManager addCommand(ICommand abstractCommand) {
        guild.getLogger().info("Adding abstractCommand: " + abstractCommand.getName());
        this.commandNameMap.put(abstractCommand.getName(), abstractCommand);

        for (String alias : abstractCommand.getAliases()) {
            commandNameMap.put(alias, abstractCommand);
        }
        return this;
    }

    // A easy way to add many abstractCommands at once.
    public void addCommands(ICommand[] abstractCommands) {
        for (ICommand abstractCommand :
                abstractCommands) {
            addCommand(abstractCommand);
        }
    }

    public CommandManager removeCommand(ICommand abstractCommand) {
        guild.getLogger().info("Removing abstractCommand: " + abstractCommand.getName());
        commandNameMap.remove(abstractCommand.getName());
        for (String alias :
                abstractCommand.getAliases()) {
            commandNameMap.remove(alias);
        }
        return this;
    }

    public CommandManager removeCommand(String aliasOrName) {
        return this.removeCommand(this.getCommand(aliasOrName));
    }

    public ICommand getCommand(String nameOrAlias) {
        return this.commandNameMap.get(nameOrAlias);
    }


    // Message Processing
    @SubscribeEvent
    private void onMessageReceived(MessageReceivedEvent event) {
        if (this.commandable) {
            String message = event.getMessage().getContent();
            if (message.indexOf("!") == 0 && !event.getAuthor().isBot()) {
                // Get the command we are sending
                String command = message.split(" ")[0].substring(1);
                ICommand abstractCommandAction = getCommand(command);

                // This command exists
                if (abstractCommandAction != null) {
                    guild.getLogger().info("Executing command: " + command);

                    // Do the commands action
                    Object response = abstractCommandAction.onCommand(command, message.substring(command.length() + 1).trim(),
                            event.getMessage(), event);

                    // The command wants us to send a response
                    if (response != null) {

                        // Get the channel
                        MessageChannel channel = event.getChannel(); // TODO does this not work?

                        // Create the message
                        MessageBuilder responseMessage = new MessageBuilder();

                        if (response instanceof Iterable) {
                            ((Iterable<?>) response).forEach(responseMessage::append);
                        } else
                            responseMessage.append(response);

                        // Send it to the user
                        responseMessage.buildAll(MessageBuilder.SplitPolicy.NEWLINE).forEach(
                                message1 -> channel.sendMessage(message1).submit()
                        );

                    }
                } else
                    // AbstractCommand not found.
                    guild.getLogger().info("AbstractCommand " + command + " not found.");

            }
        }
    }


    public void setCommandable(boolean commandable) {
        this.commandable = commandable;
    }
}