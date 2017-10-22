package io.staz.musicBot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

import java.util.concurrent.Future;

public class AudioConnection implements IAudioConnection {

    @Getter
    private final VoiceChannel channel;
    private final AudioManager manager;
    private final DefaultAudioPlayerManager playerManager;
    @Getter
    private final AudioPlayer player;

    @Getter
    @Setter
    private MessageChannel messageInfo;

    public AudioConnection(VoiceChannel channel) {
        this.channel = channel;
        this.manager = channel.getGuild().getAudioManager();

        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(this.playerManager);

        this.player = playerManager.createPlayer();
        manager.setSendingHandler(new AudioPlayerSendHandler(this.player));

        manager.openAudioConnection(channel);
    }

    @Override
    public Future<Void> loadTrack(String identifier, AudioLoadResultHandler handler) {
        return playerManager.loadItem(identifier, handler);
    }

    @Override
    public void playSong(String identifier, LoadErrorHandler handler) {
        loadTrack(identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                playTrack(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (playlist.getSelectedTrack() != null)
                    playTrack(playlist.getSelectedTrack());
                else if (playlist.getTracks().size() > 0)
                    playTrack(playlist.getTracks().get(0));
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

    @Override
    public void playTrack(AudioTrack track) {
        if (messageInfo != null)
            messageInfo.sendMessage("Now playing: " + track.getInfo().title + " by: " + track.getInfo().author + "\n" + track.getInfo().uri).submit();

        player.setPaused(false);
        player.playTrack(track);
    }

    @Override
    public void stopSong() {
        pauseSong();
        player.getPlayingTrack().setPosition(0);
    }

    @Override
    public void pauseSong() {
        player.setPaused(true);
    }

    @Override
    public void playSong() {
        player.setPaused(false);
    }

    @Override
    public void clear() {
        player.stopTrack();
    }

    @Override
    public void disconnect() {
        manager.closeAudioConnection();
    }
}
