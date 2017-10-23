package io.staz.musicBot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.concurrent.Future;

public interface IAudioConnection {

    Future<Void> loadTrack(String identifier, AudioLoadResultHandler handler);
    void playSong(String identifier, LoadErrorHandler handler);

    void playTrack(AudioTrack track);

    void stopSong();
    void pauseSong();
    void playSong();
    void clear();

    boolean isActive();

    void disconnect();
}
