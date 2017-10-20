import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.staz.musicBot.api.Question;
import io.staz.musicBot.audio.AudioConnection;
import io.staz.musicBot.audio.LoadErrorHandler;
import io.staz.musicBot.audio.QueuedAudioConnection;
import io.staz.musicBot.command.Command;
import io.staz.musicBot.command.SimpleCommand;
import io.staz.musicBot.instances.Instance;
import io.staz.musicBot.plugin.Plugin;
import io.staz.musicBot.plugin.PluginInfo;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BasicCommands extends Plugin {
    private QueuedAudioConnection connection;
    private User connectedTo;


    public BasicCommands(Instance instance, PluginInfo info) {
        super(instance, info);
    }

    @Override
    public void onLoad() {
        getCommandManager().addCommands(new Command[]{
                new SimpleCommand(this, "commands") {
                    @Override
                    public Object onCommand(String command, String message, Message eventMessage, MessageReceivedEvent event) {
                        MessageBuilder response = new MessageBuilder();
                        getInstance().getCommandManager().getCommandNameMap().keySet().forEach(s ->
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

                        new Question(event.getChannel(), getInstance()).
                                setAnswers(answers).
                                setQuestion("Which song do you wish to play?").
                                setNumericQuestions(true).
                                onResponse((event1, answer) ->
                                {
                                    event.getChannel().sendMessage("Playing song: " + answers.get(answer)).complete();
                                    getInstance().getAudioAPI().playSongTo(event, answer);
                                    return true;
                                }).ask();
                        return null;
                    }
                },
                new SimpleCommand(this, "play") {
                    @Override
                    public Object onCommand(String command, String message, Message eventMessage, MessageReceivedEvent event) {
                        AudioConnection conn = getConnection(event.getAuthor());
                        if (conn != null) {
                            conn.playSong(message, new LoadErrorHandler.DEFAULT(event.getChannel()));
                            return null;
                        }
                        return "Failed to find the channel you are in.";
                    }
                },
                new SimpleCommand(this, "stop") {
                    @Override
                    public Object onCommand(String command, String message, Message eventMessage, MessageReceivedEvent event) {
                        getInstance().getAudioAPI().stop();
                        return null;
                    }
                },
                new SimpleCommand(this, "skip") {
                    @Override
                    public Object onCommand(String command, String message, Message eventMessage, MessageReceivedEvent event) {
                        for (AudioConnection audioConnection : getInstance().getAudioAPI().getConnections()) {
                            if (audioConnection instanceof QueuedAudioConnection) {
                                if (((QueuedAudioConnection) audioConnection).playNextSong())
                                    return null;
                            }
                        }
                        return "No songs available";
                    }
                },
                new SimpleCommand(this, "list") {
                    @Override
                    public Object onCommand(String command, String message, Message eventMessage, MessageReceivedEvent event) {
                        List<String> list = new ArrayList<>();
                        for (AudioConnection connection :
                                getInstance().getAudioAPI().getConnections()) {
                            if (connection instanceof QueuedAudioConnection) {
                                for (AudioTrack track :
                                        ((QueuedAudioConnection) connection).getQueue()) {
                                    list.add(track.getInfo().title + " - " + track.getInfo().author);
                                }
                            }
                        }
                        if (list.size() == 0)
                            return "No songs queued!";
                        return list;
                    }
                },
                new SimpleCommand(this, "clear") {
                    @Override
                    public Object onCommand(String command, String message, Message eventMessage, MessageReceivedEvent event) {
                        for (AudioConnection connection :
                                getInstance().getAudioAPI().getConnections()) {
                            if (connection instanceof QueuedAudioConnection)
                                ((QueuedAudioConnection) connection).clear();
                        }
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
                        QueuedAudioConnection conn = getConnection(event.getAuthor());
                        if (conn != null) {
                            conn.queueSong(message, new LoadErrorHandler.DEFAULT(event.getChannel()));
                            conn.play();
                        } else
                            return "Could not find your channel!";
                        return null;
                    }
                }
        });
    }

    public QueuedAudioConnection getConnection(User author) {
        if (connectedTo != null && connectedTo.equals(author) && connection != null && connection.isActive()) {
            return connection;
        } else {
            for (Guild guild : author.getMutualGuilds()) {
                for (VoiceChannel c : guild.getVoiceChannels()) {
                    for (Member member : c.getMembers()) {
                        if (member.getUser().equals(author)) {
                            connectedTo = author;
                            return connection = getInstance().getAudioAPI().createQueuedConnection(c);
                        }
                    }
                }
            }
        }
        return null;
    }
}
