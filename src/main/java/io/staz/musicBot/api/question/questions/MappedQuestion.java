package io.staz.musicBot.api.question.questions;


import io.staz.musicBot.api.question.AbstractQuestion;
import io.staz.musicBot.guild.GuildConnection;
import lombok.Builder;
import lombok.Getter;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;


public class MappedQuestion extends AbstractQuestion<String> {

    @Getter
    private Map<String, String> answerMap;
    private Map<String, String> numericQuestions = null;

    @Builder
    public MappedQuestion(Map<String, String> answers, boolean useNumericQuestions, String errorMessage, IResponse<String> response, String question, GuildConnection connection, MessageChannel channel, User sender) {
        super(errorMessage, response, question, connection, channel, sender);
        this.answerMap = answers;
        if (useNumericQuestions)
            this.numericQuestions = new HashMap<>();
    }

    public MappedQuestion setAnswers(Collection<String> answers) {
        Map<String, String
                > map = new HashMap<>();
        int i = 0;
        for (String s : answers) {
            i++;
            map.put(String.valueOf(i), s);
        }
        return setAnswers(map);
    }

    public MappedQuestion setAnswers(Map<String, String> answers) {
        this.answerMap = answers;
        return this;
    }

    public MappedQuestion setNumericQuestions(boolean numericQuestions) {
        this.numericQuestions = numericQuestions ? new HashMap<>() : null;
        return this;
    }

    @Override
    public void ask() {
        getConnection().getInstance().getJda().addEventListener(this);

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
                append(getQuestion()).append("\n").
                append(builder.toString()).
                buildAll(MessageBuilder.SplitPolicy.NEWLINE);

        message.forEach(message1 -> getChannel().sendMessage(message1).submit(true));
        getConnection().disableCommands();
    }

    @Override
    public void done() {

    }


    @SubscribeEvent
    @Override
    public void onMessage(MessageReceivedEvent event) {
        if (event.getChannel().equals(getChannel())) {
            if (!event.getAuthor().isBot()) {

                String response = event.getMessage().getContent().trim();
                if (numericQuestions != null && numericQuestions.containsKey(response)) {
                    if (getResponse().onResponse(event, numericQuestions.get(response))) {
                        getConnection().getInstance().getJda().removeEventListener(this);
                        getConnection().enableCommands();
                        return;
                    }
                } else if (answerMap.containsKey(response)) {
                    if (getResponse().onResponse(event, response)) {
                        getConnection().getInstance().getJda().removeEventListener(this);
                        getConnection().enableCommands();
                        return;
                    }
                }

                getChannel().sendMessage(getErrorMessage()).submit();
            }
        }
    }

    @Override
    public String getAnswer(MessageReceivedEvent event) {
        return null;
    }
}
