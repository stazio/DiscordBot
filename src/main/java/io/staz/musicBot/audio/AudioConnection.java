package io.staz.musicBot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

public class AudioConnection extends AudioEventAdapter {

    private AudioPlayerManager playerManager;
    protected AudioPlayer player;

    public AudioConnection(VoiceChannel channel) {
        AudioManager manager = channel.getGuild().getAudioManager();

        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);

        player = playerManager.createPlayer();
        manager.setSendingHandler(new AudioPlayerSendHandler(player));

        manager.openAudioConnection(channel);
    }

    public void playSong(String url, LoadErrorHandler handler) {
        playerManager.loadItem(url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                player.playTrack(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (playlist.getTracks().size() > 0)
                    player.playTrack(playlist.getTracks().get(0));
            }

            @Override
            public void noMatches() {
                handler.noMatches(url);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                handler.loadFailed(exception, url);
            }
        });
    }

    public void loadSong(String url, AudioLoadResultHandler handler) {
        playerManager.loadItem(url, handler);
    }

    public void stop(){
        this.player.stopTrack();
    }

    public void pause() {
        this.player.setPaused(true);
    }

    public void play() {
        this.player.setPaused(false);
    }

    public void playTrack(AudioTrack queued) {
        System.out.println(queued.getIdentifier());
        this.player.playTrack(queued);
    }

    public boolean isActive() {
        return true; // TODO figure this out.
    }
}
