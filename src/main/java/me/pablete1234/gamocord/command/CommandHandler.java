package me.pablete1234.gamocord.command;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandHandler {

    private static final String DEFAULT_COMMAND_SYMBOLS = ".-/!";

    private final String prefixes;

    private final Map<String, CommandWrapper> registeredCommands = new LinkedHashMap<>();

    public CommandHandler(Object... handlers) {
        this(DEFAULT_COMMAND_SYMBOLS, handlers);
    }

    public CommandHandler(String prefixes, Object... handlers) {
        this.prefixes = prefixes;
        registerCommands(new HelpCommand());
        for (Object handler : handlers) {
            registerCommands(handler);
        }
    }

    public void registerCommands(Object obj) {
        for (Method method : obj.getClass().getMethods()) {
            Command cmd = method.getAnnotation(Command.class);
            if (cmd == null) continue;

            registeredCommands.put(cmd.value(), new CommandWrapper(cmd, obj, method));
        }
    }

    // True if the user got a response, false if one isn't needed (not a command)
    public boolean dispatchCommand(CommandContext context) {
        String msg = context.message.getContentDisplay();

        if (msg.isEmpty() || !prefixes.contains(msg.charAt(0) + "")) return false;

        msg = msg.substring(1);
        if (msg.isEmpty()) return false;

        String[] cmd = msg.split(" ", 2);

        MessageChannel channel = context.channel;

        CommandWrapper command = registeredCommands.get(cmd[0]);
        if (command == null || !isAllowed(command.cmd, context.member)) {
            reply(channel, "Unknown command `" + cmd[0] + "`, use `help` to list available commands");
            return true;
        }

        try {
            reply(channel, command.run(context, cmd.length == 1 ? new String[]{} : cmd[1].split(" ")));
        } catch (BadUsageException e) {
            reply(channel, "Bad command usage: `" + command.cmd.usage() + "`");
        } catch (IllegalArgumentException e) {
            reply(channel, "Bad argument: " + e.getMessage());
        } catch (Exception e) {
            reply(channel, "There was an error running the command, try again later");
            e.printStackTrace();
        } catch (Throwable e) {
            reply(channel, "The command couldn't be handled, try again later");
            e.printStackTrace();
        }

        return true;
    }

    private void reply(MessageChannel channel, String msg) {
        if (msg == null) return;
        if (msg.length() >= 1995)
            msg = msg.substring(0, 1995) + "...";

        channel.sendMessage(msg).complete();
    }


    private static class CommandWrapper {
        private final Command cmd;
        private final Object obj;
        private final Method method;

        CommandWrapper(Command cmd, Object obj, Method method) {
            this.cmd = cmd;
            this.obj = obj;
            this.method = method;
        }

        public String run(CommandContext context, String[] args) throws Throwable {
            try {
                return (String) method.invoke(obj, context, args);
            } catch (InvocationTargetException e) {
               throw e.getCause();
            }
        }
    }

    public static boolean isAllowed(Command cmd, Member member) {
        return !cmd.admin() ||
                (member != null && member.hasPermission(Permission.ADMINISTRATOR));
    }

    private class HelpCommand {
        @Command(
                value = "help",
                usage = "help",
                description = "List the available commands on the bot"
        )
        public String help(CommandContext context, String[] args) {
            if (args.length != 0) throw new BadUsageException();

            return "Available commands: \n" + registeredCommands.values().stream()
                    .map(cwrap -> cwrap.cmd)
                    .filter(c -> isAllowed(c, context.member))
                    .map(c -> " - `" + c.value() + "` - Usage: `." + c.usage() + "` - " + c.description())
                    .collect(Collectors.joining("\n"));
        }
    }

}
