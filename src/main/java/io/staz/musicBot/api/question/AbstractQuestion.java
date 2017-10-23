package io.staz.musicBot.api.question;

import io.staz.musicBot.guild.GuildConnection;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public abstract class AbstractQuestion<T> implements IQuestion<T> {

    @Getter
    @Setter
    private String errorMessage;

    @Getter
    @Setter
    private IResponse<T> response;

    @Getter
    @Setter
    private String question;

    @Getter
    @Setter
    private GuildConnection connection;

    @Getter
    @Setter
    private MessageChannel channel;

    @Getter
    @Setter
    private User sender;

    public AbstractQuestion(String errorMessage, IResponse<T> response, String question, GuildConnection connection, MessageChannel channel, User sender) {
        this.errorMessage = errorMessage;
        this.response = response;
        this.question = question;
        this.connection = connection;
        this.channel = channel;
        this.sender = sender;
    }

    @Override
    public void ask() {
        channel.sendMessage(getQuestion()).submit();
        getConnection().disableCommands();
    }

    @Override
    public void done() {
        getConnection().enableCommands();
    }

    @Override
    public void onMessage(MessageReceivedEvent event) {
        User sender = getSender();
        if (sender != null && !sender.equals(event.getAuthor()))
            return;

        MessageChannel channel = getChannel();
        if (channel != null && !event.getChannel().equals(channel))
            return;

        if (getResponse().onResponse(event, getAnswer(event)))
            done();
        else
            getChannel().sendMessage(errorMessage).submit();
    }

    public abstract T getAnswer(MessageReceivedEvent event);
}
