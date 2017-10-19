import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.staz.musicBot.audio.AudioConnection;
import io.staz.musicBot.audio.LoadErrorHandler;
import io.staz.musicBot.audio.QueuedAudioConnection;
import io.staz.musicBot.command.Command;
import io.staz.musicBot.command.SimpleCommand;
import io.staz.musicBot.plugin.Plugin;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class BasicCommands extends Plugin {
    private QueuedAudioConnection connection;
    private User connectedTo;

    @Override
    public String getID() {
        return "io.staz.BasicCommands";
    }

    @Override
    public String getName() {
        return "Basic Commands";
    }

    @Override
    public String[] configs() {
        return new String[0];
    }

    @Override
    public Command[] commands() {
        return new Command[]{
            new SimpleCommand(this, "play"){

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
                            for (AudioTrack track:
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
            new SimpleCommand(this, "stop") {

                @Override
                public Object onCommand(String command, String message, Message eventMessage, MessageReceivedEvent event) {
                    getInstance().getAudioAPI().stop();
                    return null;
                }
            },
            new SimpleCommand(this, "clear") {
                @Override
                public Object onCommand(String command, String message, Message eventMessage, MessageReceivedEvent event) {
                    for (AudioConnection connection :
                            getInstance().getAudioAPI().getConnections()) {
                        if (connection instanceof QueuedAudioConnection)
                            ((QueuedAudioConnection)connection).clear();
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
                        }
                         else
                            return "Could not find your channel!";
                        return null;
                    }
                }
        };
    }

    public QueuedAudioConnection getConnection(User author) {
        if (connectedTo != null && connectedTo.equals(author) && connection != null && connection.isActive()) {
                return connection;
        }else {
            for (Guild guild : author.getMutualGuilds()){
                for (VoiceChannel c : guild.getVoiceChannels()) {
                    for (Member member : c.getMembers()) {
                        if (member.getUser().equals(author)) {
                            connectedTo = author;
                            return connection =  getInstance().getAudioAPI().createQueuedConnection(c);
                        }
                    }
                }
            }
        }
        return null;
    }
}
