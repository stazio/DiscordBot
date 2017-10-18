package io.staz.musicBot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lombok.Getter;
import lombok.val;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.util.concurrent.LinkedBlockingQueue;

public class QueuedAudioConnection extends AudioConnection {

    @Getter
    private LinkedBlockingQueue<AudioTrack> queue = new LinkedBlockingQueue<>();
    private LoadErrorHandler activeErrorHandler;

    public QueuedAudioConnection(VoiceChannel channel) {
        super(channel);
    }

    public void queueSong(String url, LoadErrorHandler errorHandler) {
        activeErrorHandler = errorHandler;
        loadSong(url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                queue.add(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                queue.addAll(playlist.getTracks());
            }

            @Override
            public void noMatches() {
                errorHandler.noMatches(url);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                errorHandler.loadFailed(exception, url);
            }
        });
    }

    public void queueTrack(AudioTrack track) {
       this.queueTrack(track, true);
    }

    public void queueTrack(AudioTrack track, boolean play) {
        System.out.println("Adding song: " + track.getIdentifier());
        this.queue.add(track);
        if (play)
            this.play();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            playNextSong();
        }
    }

    public boolean playNextSong() {
        AudioTrack queued = queue.poll();
        if (queued != null)
            playTrack(queued);
        else
            return false;
        return true;
    }

    @Override
    public void play() {
        super.play();
        if (player.getPlayingTrack() == null)
            playNextSong();
    }

    public void play(String message, LoadErrorHandler errorHandler) {
        val instance = this;
        loadSong(message, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                playTrack(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                playlist.getTracks().forEach(instance::queueTrack);
            }

            @Override
            public void noMatches() {
                errorHandler.noMatches(message);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                errorHandler.loadFailed(exception, message);
            }
        });
    }
}
