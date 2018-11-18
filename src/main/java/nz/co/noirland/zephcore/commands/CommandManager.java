package nz.co.noirland.zephcore.commands;

import com.google.common.collect.ImmutableList;
import com.sk89q.intake.CommandCallable;
import com.sk89q.intake.CommandMapping;
import com.sk89q.intake.ImmutableCommandMapping;
import com.sk89q.intake.dispatcher.Dispatcher;
import com.sk89q.intake.dispatcher.SimpleDispatcher;
import com.sk89q.intake.parametric.ParametricBuilder;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.help.HelpMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.util.List;

public class CommandManager extends SimpleDispatcher {

    private static CommandManager inst;

    private static final BukkitBindings bindings = new BukkitBindings();

    CommandMap commandMap;
    HelpMap helpMap;


    public static CommandManager inst() {
        if(inst == null) {
            inst = new CommandManager();
        }
        return inst;
    }

    private CommandManager() {
        commandMap = getCommandMap();
        helpMap = Bukkit.getHelpMap();
    }

    public void registerCommand(CommandCallable command, String prefix, String... alias) {
        Validate.notNull(command, "Command must not be null!");
        CommandMapping mapping = new ImmutableCommandMapping(command, alias);

        WrappedCallable wrapped = new WrappedCallable(mapping);

        commandMap.register(prefix, wrapped);
        wrapped.register(commandMap);

        super.registerCommand(command, alias);

        registerCommandHelp(mapping, ImmutableList.of());
    }

    public void registerCommand(CommandCallable command, Plugin plugin, String... alias) {
        registerCommand(command, plugin.getName(), alias);
    }

    public void registerCommand(ParametricBuilder builder, Object commands) {
        builder.registerMethodsAsCommands(this, commands);
    }

    public static ParametricBuilder builder() {
        ParametricBuilder builder = new ParametricBuilder();
        builder.addBinding(bindings);

        return builder;
    }

    @Override
    public void registerCommand(CommandCallable command, String... alias) {
        registerCommand(command, "intake", alias);
    }

    private void registerCommandHelp(CommandMapping command, List<String> parents) {
        if(command instanceof Dispatcher) {
            Dispatcher dispatcher = (Dispatcher) command;
            parents.add(command.getPrimaryAlias());

            for(CommandMapping mapping : dispatcher.getCommands()) {
                registerCommandHelp(mapping, parents);
            }
        } else {
            helpMap.addTopic(new WrappedHelpTopic(command, parents));
        }
    }

    private CommandMap getCommandMap() {
        PluginManager pm = Bukkit.getPluginManager();
        if(!(pm instanceof SimplePluginManager)) throw new IllegalStateException("Implementation of PluginManager unknown!");
        SimplePluginManager spm = (SimplePluginManager) pm;

        try {
            Field mapField = spm.getClass().getDeclaredField("commandMap");
            mapField.setAccessible(true);
            return (CommandMap) mapField.get(spm);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("Unable to find the CommandMap!");
    }

}
