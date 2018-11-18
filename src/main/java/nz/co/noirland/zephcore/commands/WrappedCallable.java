package nz.co.noirland.zephcore.commands;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.sk89q.intake.CommandException;
import com.sk89q.intake.CommandMapping;
import com.sk89q.intake.InvalidUsageException;
import com.sk89q.intake.SettableDescription;
import com.sk89q.intake.context.CommandLocals;
import com.sk89q.intake.util.auth.AuthorizationException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

public class WrappedCallable extends Command implements TabExecutor {

    private final CommandMapping mapping;

    public WrappedCallable(CommandMapping mapping) {
        super(mapping.getPrimaryAlias());
        this.mapping = mapping;
    }

    public CommandMapping getMapping() {
        return mapping;
    }

    @Override
    public String getName() {
        return mapping.getPrimaryAlias();
    }

    @Override
    public String getLabel() {
        return getName();
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public String getDescription() {
        return mapping.getDescription().getShortDescription();
    }

    @Override
    public String getUsage() {
        return super.getUsage();
    }

    @Override
    public List<String> getAliases() {
        return ImmutableList.copyOf(mapping.getAllAliases());
    }

    @Override
    public boolean setLabel(String name) {
        return false; // Can never change label
    }

    @Override
    public Command setAliases(List<String> aliases) {
        return this; // Ignore
    }

    @Override
    public Command setDescription(String description) {
        if(getSettable() != null) {
            getSettable().setDescription(description);
        }
        return this;
    }

    @Override
    public Command setUsage(String usage) {
        if(getSettable() != null) {
            getSettable().overrideUsage(usage);
        }

        return this;
    }

    @Override
    public void setPermission(String permission) {
        // Ignore
    }

    @Override
    public boolean testPermissionSilent(CommandSender target) {
        CommandLocals locals = new CommandLocals();
        locals.put(CommandSender.class, target);
        return mapping.getCallable().testPermission(locals);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        CommandLocals locals = new CommandLocals();
        locals.put(CommandSender.class, sender);

        String[] parents = new String[] { label };
        try {
            mapping.getCallable().call(Joiner.on(" ").join(args), locals, parents);
        } catch (InvalidUsageException e) {
            sendErrorMessage(sender, e.getMessage());
            if(e.isFullHelpSuggested()) {
                sender.sendMessage(e.getSimpleUsageString("/"));
            }
        } catch (CommandException e) {
            e.prependStack(getName());
            sendErrorMessage(sender, e.getMessage());
        } catch (AuthorizationException e) {
            sendErrorMessage(sender, e.getMessage());
        }

        return true; // Help is handled through catches
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        CommandLocals locals = new CommandLocals();
        locals.put(CommandSender.class, sender);

        try {
            return mapping.getCallable().getSuggestions(Joiner.on(" ").join(args), locals);
        } catch (CommandException e) {
            e.prependStack(getName());
            sendErrorMessage(sender, e.getMessage());
            return ImmutableList.of();
        }
    }

    protected void sendErrorMessage(CommandSender to, String message) {
        to.sendMessage(ChatColor.DARK_RED + message);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        return false;
    }

    private SettableDescription getSettable() {
        if(mapping.getDescription() instanceof SettableDescription) {
            return (SettableDescription) mapping.getDescription();
        }

        return null;
    }
}

