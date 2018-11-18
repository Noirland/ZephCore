package nz.co.noirland.zephcore.commands;

import com.sk89q.intake.parametric.ParameterException;
import com.sk89q.intake.parametric.argument.ArgumentStack;
import com.sk89q.intake.parametric.binding.BindingBehavior;
import com.sk89q.intake.parametric.binding.BindingHelper;
import com.sk89q.intake.parametric.binding.BindingMatch;
import nz.co.noirland.zephcore.Util;
import nz.co.noirland.zephcore.commands.annotations.Sender;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class BukkitBindings extends BindingHelper {

    @BindingMatch(type = CommandSender.class,
            behavior = BindingBehavior.PROVIDES,
            consumedCount = 0
    )
    public CommandSender getSender(ArgumentStack stack) throws ParameterException {
        CommandSender sender = stack.getContext().getLocals().get(CommandSender.class);
        if(sender == null) {
            throw new ParameterException("Could not find the command's sender!");
        }

        return sender;
    }

    @BindingMatch(type = Player.class,
            behavior = BindingBehavior.CONSUMES,
            consumedCount = 1
    )
    public Player getPlayer(ArgumentStack stack) throws ParameterException {
        OfflinePlayer off = getOffline(stack);
        if(!off.isOnline()) {
            throw new ParameterException(off.getName() + " is not online.");
        }

        return off.getPlayer();
    }

    @BindingMatch(classifier = Sender.class,
            type = Player.class,
            behavior = BindingBehavior.PROVIDES,
            consumedCount = 1
    )
    public Player getPlayerSender(ArgumentStack stack) throws ParameterException {
        CommandSender sender = getSender(stack);

        if(!(sender instanceof Player)) {
            throw new ParameterException("You must be a player to use this command.");
        }

        return (Player) sender;
    }

    @BindingMatch(type = OfflinePlayer.class,
            behavior = BindingBehavior.CONSUMES,
            consumedCount = 1
    )
    public OfflinePlayer getOffline(ArgumentStack stack) throws ParameterException {
        String name = stack.next();

        return Util.player(name);
    }

    @BindingMatch(type = Location.class,
            behavior = BindingBehavior.CONSUMES,
            consumedCount = 3
    )
    public Location getLocation(ArgumentStack stack) throws ParameterException {
        double x = stack.nextDouble();
        double y = stack.nextDouble();
        double z = stack.nextDouble();

        CommandSender sender = getSender(stack);
        World world = getWorld(sender);
        if(world == null) {
            throw new ParameterException("This command must be executed in-game.");
        }

        return new Location(world, x, y, z);
    }

    private World getWorld(CommandSender sender) {
        if(sender instanceof Entity) {
            return ((Entity) sender).getWorld();
        } else if(sender instanceof BlockCommandSender) {
            return ((BlockCommandSender) sender).getBlock().getWorld();
        } else {
            return null;
        }

    }

}
