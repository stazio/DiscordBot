package io.staz.musicBot.api.question;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public interface IQuestion<T> {


    void setErrorMessage(String error);

    void setResponse(IResponse<T> response);

    void setQuestion(String question);

    void ask();

    void done();

    void onMessage(MessageReceivedEvent event);

    interface IResponse<T> {
        boolean onResponse(MessageReceivedEvent event, T answer);
    }
}
