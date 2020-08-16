package me.pablete1234.gamocord.command;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class CommandContext {

    public final User sender;
    public final MessageChannel channel;
    public final Message message;

    public CommandContext(User sender, MessageChannel channel, Message message) {
        this.sender = sender;
        this.channel = channel;
        this.message = message;
    }
}
