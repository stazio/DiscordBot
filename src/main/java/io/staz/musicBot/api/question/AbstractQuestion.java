package io.staz.musicBot.api.question;

import io.staz.musicBot.guild.GuildConnection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

@RequiredArgsConstructor
public abstract class AbstractQuestion<T> implements IQuestion<T> {

    @Getter(AccessLevel.PROTECTED)
    private String errorMessage;

    @Getter(AccessLevel.PROTECTED)
    private IResponse<T> response;

    @Getter(AccessLevel.PROTECTED)
    private String question;

    @Getter(AccessLevel.PROTECTED)
    private GuildConnection connection;

    @Getter(AccessLevel.PROTECTED)
    private MessageChannel channel;

    @Getter(AccessLevel.PROTECTED)
    private User sender;

    @Override
    public AbstractQuestion setErrorMessage(String error) {
        this.errorMessage = error;
        return this;
    }

    @Override
    public AbstractQuestion setResponse(IResponse<T> response) {
        this.response = response;
        return this;
    }

    @Override
    public AbstractQuestion setQuestion(String question) {
        this.question = question;
        return this;
    }

    public AbstractQuestion setConnection(GuildConnection connection) {
        this.connection = connection;
        return this;
    }

    public AbstractQuestion setChannel(MessageChannel channel) {
        this.channel = channel;
        return this;
    }

    public AbstractQuestion setSender(User sender) {
        this.sender = sender;
        return this;
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
