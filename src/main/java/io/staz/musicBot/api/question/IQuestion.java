package io.staz.musicBot.api.question;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public interface IQuestion<T> {


    IQuestion setErrorMessage(String error);

    IQuestion setResponse(IResponse<T> response);

    IQuestion setQuestion(String question);

    void ask();

    void done();

    void onMessage(MessageReceivedEvent event);

    public static interface IResponse<T> {
        boolean onResponse(MessageReceivedEvent event, T answer);
    }
}
