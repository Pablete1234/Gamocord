package me.pablete1234.gamocord;

import me.pablete1234.gamocord.command.CommandContext;
import me.pablete1234.gamocord.command.CommandHandler;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main extends ListenerAdapter {

    private static final String CONFIG_FILE = "config.properties";

    public static void main(String[] args) throws Exception {
        new Main();
    }

    private final Configuration configuration;
    private final CommandHandler cmdHandler;

    public Main() throws IOException, LoginException {
        Properties props = new Properties();
        props.load(new FileInputStream(CONFIG_FILE));

        this.configuration =  new Configuration(props);
        this.cmdHandler = new CommandHandler(
                configuration.getPrefixes(),
                new ServerCommands(configuration)
        );

        JDABuilder.createLight(configuration.getDiscordToken())
                .setActivity(configuration.getActivity())
                .addEventListeners(this)
                .build();
    }


    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        this.cmdHandler.dispatchCommand(new CommandContext(event.getAuthor(), event.getChannel(), event.getMessage()));
    }

}
