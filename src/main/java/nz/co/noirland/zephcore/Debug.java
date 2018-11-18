package nz.co.noirland.zephcore;

import com.google.common.io.Files;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Logger;

public class Debug {

    private Logger logger;
    private JavaPlugin plugin;
    boolean debug;

    public Debug(JavaPlugin plugin) {
        this.plugin = plugin;
        this.debug = isDebugEnabled();
        this.logger = plugin.getLogger();
    }

    private boolean isDebugEnabled() {
        File debugFile = new File(ZephCore.inst().getDataFolder(), "debug");
        try {
            List<String> lines = Files.readLines(debugFile, Charset.defaultCharset());
            return lines.contains(plugin.getName());
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Show an Exception's stack trace in console if debug is true.
     * @param e execption to be shown
     */
    public void debug(Throwable e) {
        debug(ExceptionUtils.getStackTrace(e));
    }

    /**
     * Show both a debug message and a stacktrace
     * @param msg message to be shown in console
     * @param e execption to be shown
     */
    public void debug(String msg, Throwable e) {
        debug(msg);
        debug(e);
    }

    /**
     * Disable plugin and show a severe message
     * @param error message to be shown
     */
    public void disable(String error) {
        logger.severe(error);
        plugin.getPluginLoader().disablePlugin(plugin);
    }

    /**
     * Disable plugin with severe message and stack trace if debug is enabled.
     * @param error message to be shown
     * @param e execption to be shown
     */
    public void disable(String error, Throwable e) {
        disable(error);
        e.printStackTrace();
    }

    public Logger getLogger() {
        return logger;
    }

    /**
     * Show a debug message if debug is true in config.
     * @param msg message to be shown in console
     */
    public void debug(String msg) {

        if(debug) {
            logger.info("[DEBUG] " + msg);
        }

    }

    /**
     * Show a warning in the console.
     * @param msg message to be shown in console
     */
    public void warning(String msg) {
        logger.warning(msg);
    }

    /**
     * Show a warning messae and debug a stacktrace
     * @param msg message to be shown in console
     * @param e execption to be shown
     */
    public void warning(String msg, Throwable e) {
        warning(msg);
        debug(e);
    }

}
