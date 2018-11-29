package nz.co.noirland.zephcore;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.zaxxer.hikari.HikariPoolMXBean;
import nz.co.noirland.zephcore.database.Database;
import nz.co.noirland.zephcore.database.mysql.MySQLDatabase;
import org.bukkit.command.CommandSender;

@CommandAlias("zephcore")
public class CoreCommand extends BaseCommand {

    @Subcommand("checkdb")
    @CommandPermission("zephcore.checkdb")
    public void onDBCheck(CommandSender sender) {
        for(Database db : Database.getDatabases()) {
            if(db instanceof MySQLDatabase) {
                HikariPoolMXBean poolBean = ((MySQLDatabase) db).getRawPool().getHikariPoolMXBean();
                int idle = poolBean.getIdleConnections();
                int active = poolBean.getActiveConnections();

                sender.sendMessage(String.format("%s Idle/Active: %s/%s", db.getClass().getSimpleName(), idle, active));
            }
        }
    }
}
