package io.staz.musicBot.api.question.questions;

import io.staz.musicBot.api.question.AbstractQuestion;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

public class FileUploadQuestion extends AbstractQuestion<List<Message.Attachment>> {

    public FileUploadQuestion() {
        setErrorMessage("This is an invalid file!");
        setQuestion("Please attach an file(s).");
    }

    @Override
    public List<Message.Attachment> getAnswer(MessageReceivedEvent event) {
        return event.getMessage().getAttachments();
    }
}
