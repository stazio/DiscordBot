package io.staz.musicBot.api.question.questions;


import io.staz.musicBot.api.question.AbstractQuestion;
import io.staz.musicBot.api.question.IQuestion;
import io.staz.musicBot.guild.GuildConnection;
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
    private IQuestion.IResponse<String> response;

    @Getter
    private String errorMessage = "This is not a correct response!";
    private Map<String, String> answerMap;
    private Map<String, String> numericQuestions = null;

    public MappedQuestion setAnswers(Collection<String> answers) {
        Map<String, String> map = new HashMap<>();
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

                getChannel().sendMessage(errorMessage).submit();
            }
        }
    }

    @Override
    public String getAnswer(MessageReceivedEvent event) {
        return null;
    }


    @Override
    public MappedQuestion setErrorMessage(String error) {
        super.setErrorMessage(error);
        return this;
    }

    @Override
    public MappedQuestion setResponse(IResponse<String> response) {
        super.setResponse(response);
        return this;
    }

    @Override
    public MappedQuestion setQuestion(String question) {
        super.setQuestion(question);
        return this;
    }

    @Override
    public MappedQuestion setConnection(GuildConnection connection) {
        super.setConnection(connection);
        return this;
    }

    @Override
    public MappedQuestion setChannel(MessageChannel channel) {
        super.setChannel(channel);
        return this;
    }

    @Override
    public MappedQuestion setSender(User sender) {
        super.setSender(sender);
        return this;
    }
}
