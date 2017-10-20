package io.staz.musicBot.audio;

import io.staz.musicBot.instances.Instance;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;

@RequiredArgsConstructor
public class AudioAPI {

    @Getter
    private final Instance instance;

    @Getter
    private ArrayList<AudioConnection> connections = new ArrayList<>();

    public AudioConnection createConnection(VoiceChannel channel) {
        AudioConnection conn = new AudioConnection(channel);
        connections.add(conn);
        return conn;
    }

    public QueuedAudioConnection createQueuedConnection(VoiceChannel channel) {
        QueuedAudioConnection conn = new QueuedAudioConnection(channel);
        connections.add(conn);
        return conn;
    }

    public void stop() {
        connections.forEach(AudioConnection::stop);
    }

    public void playSongTo(MessageReceivedEvent event, String answer) {
        VoiceChannel channel = null;
        for (Guild guild :
                event.getAuthor().getMutualGuilds()) {
            for (VoiceChannel c :
                    guild.getVoiceChannels()) {
                for (Member member : c.getMembers()) {
                    if (member.getUser().equals(event.getAuthor()))
                        channel = c;
                }
            }
        }

        if (channel != null) {
            AudioConnection connection = createConnection(channel);
            connection.playSong(answer, new LoadErrorHandler.DEFAULT(event.getChannel()));
        } else {
            event.getChannel().sendMessage("Cannot find your location!").submit();
        }
    }
}
