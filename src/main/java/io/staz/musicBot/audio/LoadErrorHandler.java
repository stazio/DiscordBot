package io.staz.musicBot.audio;

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.entities.MessageChannel;

public interface LoadErrorHandler {


    void noMatches(String url);

    void loadFailed(FriendlyException exception, String url);

    @RequiredArgsConstructor
    class DEFAULT implements LoadErrorHandler {
        @Getter
        private final MessageChannel channel;

        @Override
        public void noMatches(String identifier) {
            channel.sendMessage(identifier + " not found.").submit(false);
        }

        @Override
        public void loadFailed(FriendlyException exception, String identifier) {
            channel.sendMessage("Failed to load " + identifier).submit(false);
        }
    }
}
