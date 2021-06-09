package me.pablete1234.gamocord;

import me.pablete1234.gamocord.command.BadUsageException;
import me.pablete1234.gamocord.command.Command;
import me.pablete1234.gamocord.command.CommandContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.IMentionable;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminCommands {
    private final Configuration configuration;

    public AdminCommands(Configuration configuration) {
        this.configuration = configuration;
    }

    @Command(value = "reload", usage = "reload", description = "Reloads server mappings from config file", admin = true, master = true)
    public String status(CommandContext context, String[] args) {
        try {
            configuration.loadProps();
        } catch (IOException e) {
            return e.getLocalizedMessage();
        }
        configuration.rebuildLinks();
        return "Successfully reloaded servers (`" + configuration.getServers().size() + "`)";
    }

    @Command(value = "save", usage = "save", description = "Saves mappings to config file", admin = true, master = true)
    public String save(CommandContext context, String[] args) {
        try {
            configuration.saveProps();
        } catch (IOException e) {
            return e.getLocalizedMessage();
        }
        return "Successfully saved servers (`" + configuration.getServers().size() + "`)";
    }

    @Command(value = "list", usage = "list", description = "Lists all available servers & mappings", admin = true)
    public String list(CommandContext context, String[] args) {
        Map<String, GamocosmServer> servers = getServers(context);

        return "Available servers `" + servers.size() + "`: \n" +
                servers.entrySet().stream()
                .map(e -> format(e.getValue().getName(), e.getKey(), context.guild))
                .collect(Collectors.joining("\n"));
    }

    @Command(value = "move", usage = "move <name> <#channel>", description = "Move a server from ", admin = true)
    public String move(CommandContext context, String[] args) {
        if (args.length != 2 || context.guild == null) throw new BadUsageException();

        Map<String, GamocosmServer> servers = getServers(context);

        Map.Entry<String, GamocosmServer> server = servers.entrySet().stream()
                .filter(e -> e.getValue().getName().equalsIgnoreCase(args[0]))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Couldn't find server: " + args[0]));

        GuildChannel newChannel = context.message.getMentionedChannels().stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Missing channel tag to move to"));


        GuildChannel oldChannel = context.guild.getJDA().getGuildChannelById(server.getKey());
        if (oldChannel != null && !context.guild.equals(oldChannel.getGuild())) {
            throw new IllegalArgumentException("Can't move server in a channel from a different guild");
        }

        configuration.getServers().remove(server.getKey());
        configuration.getServers().put(newChannel.getId(), server.getValue());

        return "Successfully moved " + server.getValue().getName() +
                " from " + getChannelName(oldChannel) + " to " + getChannelName(newChannel) + "\n" +
                "Remember to `save` if you want to save them to config";
    }

    private Map<String, GamocosmServer> getServers(CommandContext context) {
        if (isMasterGuild(context.guild)) return configuration.getServers();
        return configuration.getServers().entrySet().stream()
                .filter(e -> context.guild != null && context.guild.getGuildChannelById(e.getKey()) != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static String format(String name, String channelId, @Nullable Guild guild) {
        GuildChannel ch = guild == null ? null : guild.getGuildChannelById(channelId);
        return "`" + name + "`: " + getChannelName(ch) + " (`" + channelId + "`)";
    }

    private static String getChannelName(GuildChannel ch) {
        if (ch == null) return "*unknown*";
        if (ch instanceof IMentionable) return ((IMentionable) ch).getAsMention();
        return "#" + ch.getName();
    }

    private boolean isMasterGuild(Guild guild) {
        return guild != null &&
                (configuration.getMasterGuildId() == null || configuration.getMasterGuildId().equals(guild.getId()));
    }

}
