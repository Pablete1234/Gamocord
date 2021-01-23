package me.pablete1234.gamocord.command;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class CommandContext {

    public final User sender;
    public final MessageChannel channel;
    public final Message message;
    public final Guild guild;
    public final Member member;

    public CommandContext(User sender, MessageChannel channel, Message message, Guild guild, Member member) {
        this.sender = sender;
        this.channel = channel;
        this.message = message;
        this.guild = guild;
        this.member = member;
    }
}
