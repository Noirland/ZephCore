package nz.co.noirland.zephcore;

import nz.co.noirland.zephcore.database.AsyncDatabaseUpdateTask;
import nz.co.noirland.zephcore.database.Database;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ZephCore extends JavaPlugin implements Listener {

    private static ZephCore inst;
    private static Debug debug;

    public static ZephCore inst() {
        return inst;
    }

    public static Debug debug() { return debug; }

    @Override
    public void onEnable() {
        inst = this;
        debug = new Debug(this);
        setupUUIDFetcher();
        AsyncDatabaseUpdateTask.inst().start();
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(CancelFallManager.inst(), this);
    }

    @Override
    public void onDisable() {
        AsyncDatabaseUpdateTask.inst().finish();
        for(Database db : Database.getDatabases()) {
            db.close();
        }
    }

    private void setupUUIDFetcher() {
        long start = System.currentTimeMillis();
        OfflinePlayer[] oPlayers = getServer().getOfflinePlayers();
        for(OfflinePlayer oPlayer : oPlayers) {
            UUIDFetcher.updatePlayer(oPlayer.getName(), oPlayer.getUniqueId());
        }
        debug.debug("Fetched " + oPlayers.length + " UUIDs in " + (System.currentTimeMillis() - start));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        UUIDFetcher.updatePlayer(player.getName(), player.getUniqueId());
    }
}
