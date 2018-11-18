package nz.co.noirland.zephcore;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateInventoryTask extends BukkitRunnable {
    private Player player;
    public UpdateInventoryTask(Player player) {
        this.player = player;
        runTaskLater(ZephCore.inst(), 0);
    }

    @Override
    public void run() {
        player.updateInventory();
    }
}
