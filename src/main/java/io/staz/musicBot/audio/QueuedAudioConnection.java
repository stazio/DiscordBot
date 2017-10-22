package io.staz.musicBot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.Getter;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.util.ArrayList;

public class QueuedAudioConnection extends AudioConnection {

    @Getter
    private final ArrayList<AudioTrack> queue = new ArrayList<>();

    public QueuedAudioConnection(VoiceChannel channel) {
        super(channel);
    }

    @Override
    public void playSong() {
        if (queue.size() > 0)
            playTrack(queue.remove(0));
        else if (getMessageInfo() != null)
            getMessageInfo().sendMessage("No more songs.").submit();
    }

    @Override
    public void clear() {
        super.clear();
        queue.clear();
    }

    public void queueSong(String identifier, LoadErrorHandler handler, boolean play) {
        loadTrack(identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                queueTrack(track, play);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                playlist.getTracks().forEach(track -> queueTrack(track, play));
            }

            @Override
            public void noMatches() {
                handler.noMatches(identifier);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                handler.loadFailed(exception, identifier);
            }
        });
    }

    public void queueSong(String identifier, LoadErrorHandler handler){
        queueSong(identifier, handler, true);
    }


    public void queueTrack(AudioTrack track, boolean play) {
        if (getMessageInfo() != null)
            getMessageInfo().sendMessage("Added " + track.getInfo().title + " by " + track.getInfo().author + " to the queue.\n" + track.getInfo().uri).submit();
        queue.add(track);

        if (play)
            playSong();
    }

    public void queueTrack(AudioTrack track) {
        queueTrack(track, true);
    }
}
