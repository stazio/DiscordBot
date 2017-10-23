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
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

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

        res.commands.forEach(command -> getCommandManager().addCommand(new BadCommand(this, command.name) {
            @Override
            public Object onCommand(String cmnd, String message, Message eventMessage, MessageReceivedEvent event) {
                getGuild().getAudioManager().playSongTo(event, command.video, new LoadErrorHandler.DEFAULT(event.getChannel()));
                return null;
            }
        }));

        Plugin instance = this;
        getCommandManager().addCommands(new ICommand[]{
                new BadCommand(this, "addsong") {
                    @Override
                    public Object onCommand(String command, String message, Message eventMessage, MessageReceivedEvent event) {
                        BasicConfig.CommandConfig commandConfig = new BasicConfig.CommandConfig();
                        commandConfig.name = message.split(" ")[0];
                        if (message.substring(commandConfig.name.length()).length() > 0) {
                            commandConfig.video = message.substring(commandConfig.name.length());

                        } else if (event.getMessage().getAttachments().size() > 0) {
                            commandConfig.video = event.getMessage().getAttachments().get(0).getUrl();
                            getPlugin().getLogger().info("Adding this song: " + commandConfig.video);
                        } else
                            return "Please either attach a song, or provide a valid URL.";
                        getCommandManager().addCommand(new BadCommand(instance, commandConfig.name) {
                            @Override
                            public Object onCommand(String cmnd, String message, Message eventMessage, MessageReceivedEvent event) {
                                getGuild().getAudioManager().playSongTo(event, commandConfig.video, new LoadErrorHandler.DEFAULT(event.getChannel()));
                                return null;
                            }
                        });
                        res.commands.add(commandConfig);
                        config.save();
                        return null;
                    }
                },

                Command.builder().
                        name("join").
                        plugin(this).
                        action((command, message, eventMessage, event) -> {
                            if (getGuild().getAudioManager().getQueuedAudioConnection(event.getAuthor()).isPresent())
                                return null;
                            return "Could not find where you are!";
                        }).
                        build(),

                new BadCommand(this, "leave") {
                    @Override
                    public Object onCommand(String command, String message, Message eventMessage, MessageReceivedEvent event) {
                        getAudioManager().close();
                        return null;
                    }
                },
                new BadCommand(this, "commands") {
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
                new BadCommand(this, "search") {
                    @Override
                    public Object onCommand(String command, String message, Message eventMessage, MessageReceivedEvent event) {
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
                    }
                },
                new BadCommand(this, "play") {
                    @Override
                    public Object onCommand(String command, String message, Message eventMessage, MessageReceivedEvent event) {
                        getGuild().getAudioManager().playSongTo(event, message, new LoadErrorHandler.DEFAULT(event.getChannel()));
                        return null;
                    }
                },
                new BadCommand(this, "stop") {
                    @Override
                    public Object onCommand(String command, String message, Message eventMessage, MessageReceivedEvent event) {
                        getGuild().getAudioManager().stop();
                        return null;
                    }
                },
                new BadCommand(this, "skip") {
                    @Override
                    public Object onCommand(String command, String message, Message eventMessage, MessageReceivedEvent event) {
                        getGuild().getAudioManager().getAudioConnection()
                                .ifPresent(QueuedAudioConnection::playSong);
                        return null;
                    }
                },
                new BadCommand(this, "list") {
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
                new BadCommand(this, "clear") {
                    @Override
                    public Object onCommand(String command, String message, Message eventMessage, MessageReceivedEvent event) {
                        getGuild().getAudioManager().getAudioConnection().ifPresent(QueuedAudioConnection::clear);
                        return null;
                    }
                },
                new BadCommand(this, "stanislaw") {
                    @Override
                    public Object onCommand(String command, String message, Message eventMessage, MessageReceivedEvent event) {
                        return "You have entered the Stanislaw.";
                    }
                },
                new BadCommand(this, "queue") {
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
