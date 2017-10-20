package io.staz.musicBot.api;


import io.staz.musicBot.instances.Instance;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

@AllArgsConstructor
@RequiredArgsConstructor
public class Question {

    @Getter
    private final MessageChannel channel;

    @Getter
    private final Instance instance;

    @Getter
    private IResponse response;

    private String question;

    @Getter
    private String errorMessage = "This is not a correct response!";
    private Map<String, String> answerMap;
    private Map<String, String> numericQuestions = null;

    public Question onResponse(IResponse response) {
        this.response = response;
        return this;
    }

    public Question setQuestion(String question) {
        this.question = question;
        return this;
    }

    public Question setAnswers(Collection<String> answers) {
        Map<String, String> map = new HashMap<>();
        int i = 0;
        for (String s : answers) {
            i++;
            map.put(String.valueOf(i), s);
        }
        return setAnswers(map);
    }

    public Question setAnswers(Map<String, String> answers) {
        this.answerMap = answers;
        return this;
    }

    public Question setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public void ask() {
        instance.getJda().addEventListener(this);

        StringBuilder builder = new StringBuilder();

        if (numericQuestions != null) {
            int i = 0;
            for (String s2 : answerMap.keySet()) {
                builder.append(++i).append(" - ").append(answerMap.get(s2)).append("\n");
                numericQuestions.put(String.valueOf(i), s2);
            }
        } else {
            answerMap.forEach((s, s2) -> builder.append(s).append(" - ").append(s2).append("\n"));
        }

        Queue<Message> message = new MessageBuilder().
                append(question).append("\n").
                append(builder.toString()).
                buildAll(MessageBuilder.SplitPolicy.NEWLINE);

        message.forEach(message1 -> channel.sendMessage(message1).submit(true));
    }

    @SubscribeEvent
    public void onMessage(MessageReceivedEvent event) {
        if (event.getChannel().equals(channel)) {
            if (!event.getAuthor().isBot()) {

                String response = event.getMessage().getContent().trim();
                if (numericQuestions != null && numericQuestions.containsKey(response)) {
                    if (getResponse().onResponse(event, numericQuestions.get(response))) {
                        getInstance().getJda().removeEventListener(this);
                        return;
                    }
                } else if (answerMap.containsKey(response)) {
                    if (getResponse().onResponse(event, response)) {
                        getInstance().getJda().removeEventListener(this);
                        return;
                    }
                }

                channel.sendMessage(errorMessage).submit();
            }
        }
    }

    public Question setNumericQuestions(boolean numericQuestions) {
        this.numericQuestions = numericQuestions ? new HashMap<>() : null;
        return this;
    }

    public static interface IResponse {
        boolean onResponse(MessageReceivedEvent event, String answer);
    }
}
