package xyz.smirking.script;

import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.SetMultimap;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import javax.script.ScriptException;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;

@SuppressWarnings("unchecked")
public class ScriptInterface {
    private static final Field knownCommandsField;
    private static final Field commandMapField;

    static {
        Field localKnownCommandsField;
        Field localCommandMapField;
        try {
            localKnownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            localCommandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
            localKnownCommandsField.setAccessible(true);
            localCommandMapField.setAccessible(true);
        } catch (Exception ex) {
            throw new ExceptionInInitializerError(ex);
        }
        knownCommandsField = localKnownCommandsField;
        commandMapField = localCommandMapField;
    }

    private final SetMultimap<Class, Consumer<Event>> handlers = LinkedHashMultimap.create();
    private final Map<String, Command> knownCommands;
    private final CommandMap commandMap;
    private final Plugin plugin;

    public ScriptInterface(Plugin plugin) {
        this.plugin = plugin;

        try {
            commandMap = (CommandMap) commandMapField.get(plugin.getServer().getPluginManager());
            knownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to reflect command map", e);
        }
    }

    public void command(String name) throws ScriptException {
        command(name, null);
    }

    public void command(String name, BiConsumer<CommandSender, String[]> handler) throws ScriptException {
        if (name == null) {
            throw new ScriptException("name was null");
        }

        name = name.toLowerCase();

        int idx = name.indexOf(' ');
        if (idx > -1) {
            name = name.substring(0, idx);
        }

        synchronized (commandMap) {
            if (handler == null) {
                if (knownCommands.remove(name) != null) {
                    plugin.getLogger().log(Level.FINE, "Unregistered command {0}", name);
                }
            } else {
                Command command = new ScriptCommand(name, this, handler);
                knownCommands.put(name, command);
                knownCommands.put(String.join(":", "spigotjs", name), command);
                command.register(commandMap);
            }
        }
    }

    public <T extends Event> void on(String name, Consumer<T> handler) throws ScriptException {
        Class clazz;
        try {
            clazz = Class.forName(name, false, ScriptPlugin.class.getClassLoader());
            if (!Event.class.isAssignableFrom(clazz)) {
                throw new ScriptException("Invalid event class");
            }
        } catch (ClassNotFoundException e) {
            throw new ScriptException(e);
        }

        if (!handlers.put(clazz, (Consumer<Event>) handler)) {
            throw new ScriptException("duplicate handler registration");
        }
    }

    <T extends Event> void emit(T event) throws ScriptException {
        Preconditions.checkArgument(event != null, "event");
        plugin.getLogger().log(Level.FINEST, "emitted {0}", event.getClass().getCanonicalName());
        Set<Consumer<Event>> handlers = this.handlers.get(event.getClass());
        if (handlers == null || handlers.isEmpty()) {
            return;
        }

        for (Iterator<Consumer<Event>> iterator = handlers.iterator(); iterator.hasNext(); ) {
            Consumer<Event> next = iterator.next();
            try {
                next.accept(event);
            } catch (Throwable throwable) {
                plugin.getLogger().log(Level.SEVERE, "", throwable);
                iterator.remove();
            }
        }
    }

    void cleanup() {
        handlers.clear();
        synchronized (commandMap) {
            for (Iterator<Command> iterator = knownCommands.values().iterator(); iterator.hasNext(); ) {
                Command next = iterator.next();
                if (next instanceof ScriptCommand) {
                    ScriptCommand command = ScriptCommand.class.cast(next);
                    if (command.scriptInterface == this) {
                        iterator.remove();
                    }
                }
            }
        }
    }

    private static final class ScriptCommand extends Command {
        private final BiConsumer<CommandSender, String[]> handler;
        private final ScriptInterface scriptInterface;

        ScriptCommand(String name, ScriptInterface scriptInterface, BiConsumer<CommandSender, String[]> handler) {
            super(name.toLowerCase());
            this.scriptInterface = scriptInterface;
            this.handler = handler;
        }

        @Override
        public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            try {
                handler.accept(sender, args);
            } catch (Throwable throwable) {
                sender.sendMessage(ChatColor.RED + "Something went wrong whilst handling the command.");
                scriptInterface.plugin.getLogger().log(Level.SEVERE, "", throwable);
            }
            return true;
        }
    }
}
