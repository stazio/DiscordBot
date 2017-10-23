package io.staz.musicBot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.player.event.TrackEndEvent;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.staz.musicBot.Main;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Future;

public class AudioConnection implements IAudioConnection, AudioEventListener {

    @Getter
    private final VoiceChannel channel;
    private final AudioManager manager;
    private final DefaultAudioPlayerManager playerManager;
    @Getter
    private final AudioPlayer player;

    @Getter
    @Setter                     // MILLIS * SEC * MIN
    private long disconnectDelay = 1000 * 5;

    @Getter
    @Setter
    private MessageChannel messageInfo;
    private Timer disconnectTimer;

    public AudioConnection(VoiceChannel channel) {
        this.channel = channel;
        this.manager = channel.getGuild().getAudioManager();

        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(this.playerManager);

        this.player = playerManager.createPlayer();
        this.player.addListener(this);
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

        player.playTrack(track);
        playSong();
    }

    @Override
    public void stopSong() {
        pauseSong();
        player.getPlayingTrack().setPosition(0);
    }

    @Override
    public void pauseSong() {
        player.setPaused(true);
        enableDisconnectTimer();
    }

    @Override
    public void playSong() {
        player.setPaused(false);
        disconnectTimer.purge();
    }

    @Override
    public void clear() {
        player.stopTrack();
    }

    @Override
    public void disconnect() {
        manager.closeAudioConnection();
    }

    private void enableDisconnectTimer() {
        Main.logger.info("Delay: " + (System.currentTimeMillis()));
        if (disconnectTimer != null)
            disconnectTimer.purge();

        disconnectTimer = new Timer();
        disconnectTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Main.logger.info("Disconnecting...");
                disconnect();
            }
        }, getDisconnectDelay());
    }

    @Override
    public void onEvent(AudioEvent event) {
        if (event instanceof TrackEndEvent) {
            enableDisconnectTimer();
        }
    }

    @Override
    public boolean isActive() {
        return manager.isConnected();
    }
}
