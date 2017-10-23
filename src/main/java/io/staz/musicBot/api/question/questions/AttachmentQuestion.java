package io.staz.musicBot.api.question.questions;

import io.staz.musicBot.api.question.AbstractQuestion;
import io.staz.musicBot.guild.GuildConnection;
import lombok.Builder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

public class AttachmentQuestion extends AbstractQuestion<List<Message.Attachment>> {
    @Builder
    public AttachmentQuestion(String errorMessage, IResponse<List<Message.Attachment>> response, String question, GuildConnection connection, MessageChannel channel, User sender) {
        super(errorMessage, response, question, connection, channel, sender);
    }

    @Override
    public List<Message.Attachment> getAnswer(MessageReceivedEvent event) {
        return event.getMessage().getAttachments();
    }
}
