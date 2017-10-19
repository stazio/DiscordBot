package io.staz.musicBot.audio;

import io.staz.musicBot.instances.Instance;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.entities.VoiceChannel;

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
}
