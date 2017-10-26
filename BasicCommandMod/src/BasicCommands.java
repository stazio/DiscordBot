import io.staz.musicBot.api.Configuration;
import io.staz.musicBot.api.question.questions.TextChoiceQuestion;
import io.staz.musicBot.audio.LoadErrorHandler;
import io.staz.musicBot.audio.QueuedAudioConnection;
import io.staz.musicBot.command.BadCommand;
import io.staz.musicBot.command.Command;
import io.staz.musicBot.command.ICommand;
import io.staz.musicBot.guild.GuildConnection;
import io.staz.musicBot.plugin.Plugin;
import io.staz.musicBot.plugin.PluginInfo;
import lombok.val;
import net.dv8tion.jda.core.MessageBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BasicCommands extends Plugin {

    public BasicCommands(GuildConnection guild, PluginInfo info) {
        super(guild, info);
    }

    @Override
    public void onLoad() {
        Configuration<BasicConfig> config = getConfig("config.conf", BasicConfig.class, false);
        BasicConfig res = config.getValue() != null ? config.getValue() : new BasicConfig();
        Plugin instance = this;

        res.commands.forEach(command -> getCommandManager().addCommand(Command.builder().
                        name(command.name).
                        plugin(instance).
                        description("Plays the song: " + command.video).
                        action((command1, message, eventMessage1, event1) -> {
                            getGuild().getAudioManager().playSongTo(event1, command.video,
                                    new LoadErrorHandler.DEFAULT(event1.getChannel()));
                            return null;
                        }).build()));


        getCommandManager().addCommands(new ICommand[]{

                Command.builder().
                        name("addsong").
                        plugin(this).
                        description("Create a new command that will play a song when executed.").
                        action((command, message, eventMessage, event) -> {
                            BasicConfig.CommandConfig commandConfig = new BasicConfig.CommandConfig();
                            commandConfig.name = message.split(" ")[0];
                            if (message.substring(commandConfig.name.length()).length() > 0) {
                                commandConfig.video = message.substring(commandConfig.name.length());

                            } else if (event.getMessage().getAttachments().size() > 0) {
                                commandConfig.video = event.getMessage().getAttachments().get(0).getUrl();
                                instance.getLogger().info("Adding this song: " + commandConfig.video);
                            } else
                                return "Please either attach a song, or provide a valid URL.";
                            getCommandManager().addCommand(
                                    Command.builder().
                                            name(commandConfig.name).
                                            plugin(instance).
                                            description("Plays the song: " + commandConfig.video).
                                            action((command1, message1, eventMessage1, event1) -> {
                                        getGuild().getAudioManager().playSongTo(event, commandConfig.video, new LoadErrorHandler.DEFAULT(event.getChannel()));
                                        return null;
                                    }).build()
                            );
                            res.commands.add(commandConfig);
                            config.save();
                            return null;
                        }).
                        build(),

                Command.builder().
                        name("join").
                        plugin(this).
                        description("Joins the discord voice channel you are in.").
                        action((command, message, eventMessage, event) -> {
                            if (getGuild().getAudioManager().getQueuedAudioConnection(event.getAuthor()).isPresent())
                                return null;
                            return "Could not find where you are!";
                        }).
                        build(),

                Command.builder().
                        name("leave").
                        plugin(this).
                        description("Disconnects from the voice channel.").
                        action((command, message, eventMessage, event) -> {
                            getAudioManager().close();
                            return null;
                        }).build(),

                Command.builder().
                        name("commands").
                        plugin(this).
                        description("Shows a list of commands.").
                        action((command, message, eventMessage, event) -> {
                            MessageBuilder response = new MessageBuilder();
                            getGuild().getCommandManager().getCommandNameMap().values().forEach(cmd ->
                                    response.append("!").
                                            append(cmd.getName()).
                                            append(" - ").
                                            append(cmd.getDescription()).
                                            append("\n"));
                            return response.build();
                        }).build(),

                Command.builder().plugin(this).name("search").
                        description("Search for a song on YouTube.").
                        action(((command, message, eventMessage, event) -> {
                            Map<String, String> answers = YTSearch.search(message.trim());

                            TextChoiceQuestion.builder().
                                    channel(event.getChannel()).
                                    connection(getGuild()).
                                    answers(answers).
                                    question("Which song do you wish to play?").
                                    useNumericQuestions(true).
                                    response((event1, answer) ->
                                    {
                                        event.getChannel().sendMessage("Playing song: " + answers.get(answer)).complete();
                                        getGuild().getAudioManager().playSongTo(event, answer, new LoadErrorHandler.DEFAULT(event.getChannel()));
                                        return true;
                                    }).build().ask();
                            return null;
                        })).build(),
                Command.builder().plugin(this).name("play").
                        description("Play a song from an YouTube or MP3 link.").
                        action(((command, message, eventMessage, event) -> {
                            getGuild().getAudioManager().playSongTo(event, message, new LoadErrorHandler.DEFAULT(event.getChannel()));
                            return null;
                        })).build(),

                Command.builder().plugin(this).name("stop").
                        description("Stops and resets to beginning of track.").
                        action(((command, message, eventMessage, event) -> {
                            getGuild().getAudioManager().stop();
                            return null;
                        })).build(),
                Command.builder().plugin(this).name("skip").
                        description("Skips to the next song in queue.").
                        action(((command, message, eventMessage, event) -> {
                            getGuild().getAudioManager().getAudioConnection()
                                    .ifPresent(QueuedAudioConnection::playSong);
                            return null;
                        })).build(),
                Command.builder().plugin(this).name("list").
                        description("List's the current songs in the queue.").
                        action(((command, message, eventMessage, event) -> {
                            List<String> list = new ArrayList<>();

                            getGuild().getAudioManager().getAudioConnection().ifPresent(conn ->
                                    conn.getQueue().forEach(track ->
                                            list.add(track.getInfo().title + " - " + track.getInfo().author)));

                            if (list.size() == 0)
                                return "No songs queued!";
                            return list;
                        })).build(),
                Command.builder().plugin(this).name("clear").
                        description("Clears the queue.").
                        action(((command, message, eventMessage, event) -> {
                            getGuild().getAudioManager().getAudioConnection().ifPresent(QueuedAudioConnection::clear);
                            return null;
                        })).build(),
                Command.builder().plugin(this).name("stanislaw").
                        description("Memes.").
                        action(((command, message, eventMessage, event) ->
                                "You have entered the Stanislaw.")).build(),

                Command.builder().plugin(this).name("queue").
                        description("Adds a new song to the queue of the current song.").
                        action(((command, message, eventMessage, event) -> {
                            val conn = getGuild().getAudioManager().getQueuedAudioConnection(event.getAuthor());
                            conn.ifPresent(queuedAudioConnection -> queuedAudioConnection.queueSong(message,
                                    new LoadErrorHandler.DEFAULT(event.getChannel())));
                            if (!conn.isPresent())
                                return "Could not find the channel you are in.";
                            return null;
                        })).build()
        });
    }

}
