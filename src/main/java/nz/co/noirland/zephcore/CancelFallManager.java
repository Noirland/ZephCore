package nz.co.noirland.zephcore;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashSet;
import java.util.UUID;

public class CancelFallManager implements Listener {

    private static CancelFallManager inst;
    private final HashSet<UUID> falling = new HashSet<UUID>();

    public static CancelFallManager inst() {
        if(inst == null) {
            inst = new CancelFallManager();
        }
        return inst;
    }

    private CancelFallManager() {

    }

    public void addPlayer(UUID player) {
        falling.add(player);
    }

    public void removePlayer(UUID player) {
        falling.remove(player);
    }

    public boolean isFalling(UUID player) {
        return falling.contains(player);
    }

    @EventHandler
    public void onFall(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(!isFalling(player.getUniqueId())) return;

        if(event.getFrom().getBlockY() > event.getTo().getBlockY()) { // Fell one block
            if(event.getTo().getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) {
                player.setFallDistance(0);
                falling.remove(player.getUniqueId());
            }
        }
    }


}
