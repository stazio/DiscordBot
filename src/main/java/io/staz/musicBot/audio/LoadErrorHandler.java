package io.staz.musicBot.audio;

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.Event;

public interface LoadErrorHandler {



    @RequiredArgsConstructor
    class DEFAULT implements LoadErrorHandler{
        @Getter
        private final MessageChannel channel;

        @Override
        public void noMatches(String url) {
            channel.sendMessage(url + " not found.").submit(false);
        }

        @Override
        public void loadFailed(FriendlyException exception, String url) {
            channel.sendMessage("Failed to load " + url).submit(false);
        }
    }

    void noMatches(String url);

    void loadFailed(FriendlyException exception, String url);
}
