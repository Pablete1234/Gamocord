package me.pablete1234.gamocord;

import me.pablete1234.gamocord.command.CommandContext;
import me.pablete1234.gamocord.command.CommandHandler;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.io.IOException;

public class Main extends ListenerAdapter {

    public static void main(String[] args) throws Exception {
        new Main();
    }

    private final Configuration configuration;
    private final CommandHandler cmdHandler;

    public Main() throws IOException, LoginException {
        this.configuration = new Configuration();

        this.cmdHandler = new CommandHandler(
                configuration.getPrefixes(),
                new ServerCommands(configuration),
                new AdminCommands(configuration)
        );

        JDABuilder.createLight(configuration.getDiscordToken())
                .setActivity(configuration.getActivity())
                .addEventListeners(this)
                .build();
    }


    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        this.cmdHandler.dispatchCommand(new CommandContext(
                event.getAuthor(),
                event.getChannel(),
                event.getMessage(),
                event.getGuild(),
                event.getMember()));
    }

}
