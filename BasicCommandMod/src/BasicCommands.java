import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.staz.musicBot.api.Question;
import io.staz.musicBot.audio.AudioConnection;
import io.staz.musicBot.audio.LoadErrorHandler;
import io.staz.musicBot.audio.QueuedAudioConnection;
import io.staz.musicBot.command.Command;
import io.staz.musicBot.command.SimpleCommand;
import io.staz.musicBot.guild.GuildConnection;
import io.staz.musicBot.instances.Instance;
import io.staz.musicBot.plugin.Plugin;
import io.staz.musicBot.plugin.PluginInfo;
import lombok.val;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BasicCommands extends Plugin {

    public BasicCommands(GuildConnection guild, PluginInfo info) {
        super(guild, info);
    }


    @Override
    public void onLoad() {
        getCommandManager().addCommands(new Command[]{
                new SimpleCommand(this, "commands") {
                    @Override
                    public Object onCommand(String command, String message, Message eventMessage, MessageReceivedEvent event) {
                        MessageBuilder response = new MessageBuilder();
                        getGuild().getCommandManager().getCommandNameMap().keySet().forEach(s ->
                                response.append("!").
                                        append(s).
                                        append("\n"));
                        return response.buildAll();
                    }
                },

                new SimpleCommand(this, "search") {
                    @Override
                    public Object onCommand(String command, String message, Message eventMessage, MessageReceivedEvent event) {
                        Map<String, String> answers = YTSearch.search(message.trim());

                        new Question(event.getChannel(), getGuild()).
                                setAnswers(answers).
                                setQuestion("Which song do you wish to play?").
                                setNumericQuestions(true).
                                onResponse((event1, answer) ->
                                {
                                    event.getChannel().sendMessage("Playing song: " + answers.get(answer)).complete();
                                    getGuild().getAudioManager().playSongTo(event, answer, new LoadErrorHandler.DEFAULT(event.getChannel()));
                                    return true;
                                }).ask();
                        return null;
                    }
                },
                new SimpleCommand(this, "play") {
                    @Override
                    public Object onCommand(String command, String message, Message eventMessage, MessageReceivedEvent event) {
                        getGuild().getAudioManager().playSongTo(event, message, new LoadErrorHandler.DEFAULT(event.getChannel()));
                        return null;
                    }
                },
                new SimpleCommand(this, "stop") {
                    @Override
                    public Object onCommand(String command, String message, Message eventMessage, MessageReceivedEvent event) {
                        getGuild().getAudioManager().stop();
                        return null;
                    }
                },
                new SimpleCommand(this, "skip") {
                    @Override
                    public Object onCommand(String command, String message, Message eventMessage, MessageReceivedEvent event) {
                       getGuild().getAudioManager().getAudioConnection()
                                .ifPresent(QueuedAudioConnection::playNextSong);
                        return null;
                    }
                },
                new SimpleCommand(this, "list") {
                    @Override
                    public Object onCommand(String command, String message, Message eventMessage, MessageReceivedEvent event) {
                        List<String> list = new ArrayList<>();

                        getGuild().getAudioManager().getAudioConnection().ifPresent(conn ->
                                conn.getQueue().forEach(track ->
                                        list.add(track.getInfo().title + " - " + track.getInfo().author)));

                        if (list.size() == 0)
                            return "No songs queued!";
                        return list;
                    }
                },
                new SimpleCommand(this, "clear") {
                    @Override
                    public Object onCommand(String command, String message, Message eventMessage, MessageReceivedEvent event) {
                        getGuild().getAudioManager().getAudioConnection().ifPresent(QueuedAudioConnection::clear);
                        return null;
                    }
                },
                new SimpleCommand(this, "stanislaw") {
                    @Override
                    public Object onCommand(String command, String message, Message eventMessage, MessageReceivedEvent event) {
                        return "You have entered the Stanislaw.";
                    }
                },
                new SimpleCommand(this, "queue") {
                    @Override
                    public Object onCommand(String command, String message, Message eventMessage, MessageReceivedEvent event) {

                        val conn = getGuild().getAudioManager().getQueuedAudioConnection(event.getAuthor());
                        conn.ifPresent(queuedAudioConnection -> queuedAudioConnection.queueSong(message,
                                new LoadErrorHandler.DEFAULT(event.getChannel())));
                        if (!conn.isPresent())
                            return "Could not find the channel you are in.";
                        return null;
                    }
                }
        });
    }
}
