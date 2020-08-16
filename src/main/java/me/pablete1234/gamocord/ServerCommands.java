package me.pablete1234.gamocord;

import me.pablete1234.gamocord.command.BadUsageException;
import me.pablete1234.gamocord.command.Command;
import me.pablete1234.gamocord.command.CommandContext;
import me.pablete1234.gamocord.util.ThrowingFunction;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class ServerCommands {
    private final Configuration configuration;

    public static final String
            WORKING = "\uD83E\uDD14",
            CHECK_MARK = "\u2705",
            CROSS_MARK = "\u274C";

    public ServerCommands(Configuration configuration) {
        this.configuration = configuration;
    }

    @Command(value = "status", usage = "status", description = "Shows the status of the server")
    public String status(CommandContext context, String[] args) {
        String status = serverCommand(GamocosmServer::getStatus, context, args);
        if (status == null) return null;
        Map<String, String> fields = Arrays.stream(status.substring(1, status.length() - 2).split(","))
                .map(str -> str.split(":", 2))
                .collect(Collectors.toMap(str -> str[0].replace("\"", ""), str -> str[1].replace("\"", "")));
        String server = fields.getOrDefault("server", "").equals("true") ? CHECK_MARK : CROSS_MARK,
                minecraft = fields.getOrDefault("minecraft", "").equals("true") ? CHECK_MARK : CROSS_MARK,
                pending = fields.getOrDefault("status", "none");
        if (pending.equals("null")) pending = "none";

        return "Physical server: " + server + "\n" +
                "Minecraft server: " + minecraft + "\n" +
                "Pending operations: *" + pending + "*";
    }

    @Command(value = "start", usage = "start", description = "Starts the practice server vps (~2 min)")
    public String start(CommandContext context, String[] args) {
        return errorOnlyCommand(GamocosmServer::start, context, args);
    }

    @Command(value = "stop", usage = "stop", description = "Stops the practice server vps")
    public String stop(CommandContext context, String[] args) {
        return errorOnlyCommand(GamocosmServer::stop, context, args);
    }

    @Command(value = "reboot", usage = "reboot", description = "Reboots the practice server vps (~1 min)")
    public String reboot(CommandContext context, String[] args) {
        return errorOnlyCommand(GamocosmServer::reboot, context, args);
    }

    @Command(value = "pause", usage = "pause", description = "Stops the minecraft practice server")
    public String pause(CommandContext context, String[] args) {
        return errorOnlyCommand(GamocosmServer::pause, context, args);
    }

    @Command(value = "resume", usage = "resume", description = "Starts the minecraft practice server (~20 secs)")
    public String resume(CommandContext context, String[] args) {
        return errorOnlyCommand(GamocosmServer::resume, context, args);
    }

    @Command(value = "restart", usage = "restart", description = "Restarts the minecraft practice server (~30 secs)")
    public String restart(CommandContext context, String[] args) {
        return errorOnlyCommand(server -> {
            String result = server.pause();
            if (isError(result)) return result;
            return server.resume();
        }, context, args);
    }

    private String serverCommand(ThrowingFunction<GamocosmServer, String, Exception> handle, CommandContext context, String[] args) {
        GamocosmServer server = configuration.getAPI(context.channel.getId());
        if (server == null) return null;

        if (args.length != 0) throw new BadUsageException();
        context.message.addReaction(WORKING).complete();

        try {
            String result = handle.applyThrowing(server);
            context.message.addReaction(isError(result) ? CROSS_MARK : CHECK_MARK).submit();
            context.message.removeReaction(WORKING).submit();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            context.message.addReaction(CROSS_MARK).submit();
        }
        return null;
    }

    private String errorOnlyCommand(ThrowingFunction<GamocosmServer, String, Exception> handle, CommandContext context, String[] args) {
        String result = serverCommand(handle, context, args);
        return isError(result) ? result : null;
    }

    private boolean isError(String result) {
        return result != null && !result.isEmpty() &&
                result.toLowerCase().contains("error") && !result.toLowerCase().contains("error\":null");
    }

}
