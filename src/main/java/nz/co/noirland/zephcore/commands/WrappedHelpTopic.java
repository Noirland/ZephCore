package nz.co.noirland.zephcore.commands;

import com.google.common.base.Joiner;
import com.sk89q.intake.CommandMapping;
import com.sk89q.intake.context.CommandLocals;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.help.HelpTopic;

import java.util.List;

public class WrappedHelpTopic extends HelpTopic {

    private final CommandMapping command;

    public WrappedHelpTopic(CommandMapping command, List<String> parents) {
        this.command = command;

        this.name = "/" + Joiner.on(" ").join(parents) + command.getPrimaryAlias();
        this.fullText = buildText();
    }

    private String buildText() {
        StringBuilder b = new StringBuilder();

        b.append(ChatColor.GOLD);
        b.append("Description: ");
        b.append(ChatColor.WHITE);
        b.append(command.getDescription().getHelp());

        b.append("\n");

        b.append(ChatColor.GOLD);
        b.append("Usage: ");
        b.append(ChatColor.WHITE);
        b.append(name).append(" ").append(command.getDescription().getUsage());

        if(command.getAllAliases().length > 1) {
            b.append("\n");
            b.append(ChatColor.GOLD);
            b.append("Aliases: ");
            b.append(ChatColor.WHITE);
            Joiner.on(", ").appendTo(b, command.getAllAliases());
        }

        return b.toString();
    }

    @Override
    public boolean canSee(CommandSender player) {
        CommandLocals locals = new CommandLocals();
        locals.put(CommandSender.class, player);
        return command.getCallable().testPermission(locals);
    }

    @Override
    public void amendCanSee(String amendedPermission) {
        // Ignore
    }

    @Override
    public String getShortText() {
        return command.getDescription().getShortDescription();
    }

    @Override
    public void amendTopic(String amendedShortText, String amendedFullText) {
        // Ignore
    }
}
