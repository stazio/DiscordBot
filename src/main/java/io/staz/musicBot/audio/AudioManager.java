package io.staz.musicBot.audio;

import io.staz.musicBot.guild.GuildConnection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Optional;

@RequiredArgsConstructor
public class AudioManager {

    @Getter
    private final GuildConnection guild;

    private QueuedAudioConnection audio;

    public QueuedAudioConnection getQueuedAudioConnection(VoiceChannel channel) {
        if (audio != null && audio.getChannel().equals(channel))
            return audio;

        guild.getLogger().info("Connecting to Voice Channel: " + channel.getName());
        return audio = new QueuedAudioConnection(channel);
    }

    public AudioConnection getAudioConnection(VoiceChannel channel) {
        return getQueuedAudioConnection(channel);
    }

    public Optional<QueuedAudioConnection> getAudioConnection() {
        return Optional.of(audio);
    }

    public Optional<QueuedAudioConnection> getQueuedAudioConnection(User author) {
        Member member = getGuild().getGuild().getMember(author);
        if (member != null) {
                VoiceState state = member.getVoiceState();
            if (state != null) {
                AudioChannel channel = state.getAudioChannel();
                if (channel != null)
                    return Optional.of(getQueuedAudioConnection((VoiceChannel) channel));
            }
        }
        return Optional.empty();
    }

    public void playSongTo(MessageReceivedEvent event, String answer, LoadErrorHandler.DEFAULT aDefault) {
        getQueuedAudioConnection(event.getAuthor()).
                ifPresent(queuedAudioConnection -> queuedAudioConnection.play(answer, aDefault));
    }

    public void stop() {
        getAudioConnection().ifPresent(QueuedAudioConnection::stop);
    }

    public void close() {
        getAudioConnection().ifPresent(QueuedAudioConnection::terminate);
        audio = null;
    }
}

