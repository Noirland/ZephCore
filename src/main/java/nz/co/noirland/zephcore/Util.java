package nz.co.noirland.zephcore;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Util {


    public static final long HOUR = TimeUnit.HOURS.toMillis(1);
    public static final long DAY = TimeUnit.DAYS.toMillis(1);

    private static final Random rand = new Random();
    private static final ArrayList<TimeUnit> unitsDescending = new ArrayList<TimeUnit>() {{
        add(TimeUnit.DAYS);
        add(TimeUnit.HOURS);
        add(TimeUnit.MINUTES);
        add(TimeUnit.SECONDS);
        add(TimeUnit.MILLISECONDS);
    }};

    public static boolean isSign(Block block) {
        return block != null && (block.getState() instanceof Sign);
    }

    public static boolean isSignAttachedToBlock(Block sign, Block block) {
        org.bukkit.material.Sign sData = (org.bukkit.material.Sign) sign.getState().getData();
        Block attached = sign.getRelative(sData.getAttachedFace());
        return attached.equals(block); // Has no trade sign attached
    }

    public static OfflinePlayer player(UUID uuid) {
        return ZephCore.inst().getServer().getOfflinePlayer(uuid);
    }

    public static OfflinePlayer player(String name) {
        return ZephCore.inst().getServer().getOfflinePlayer(name);
    }

    public static String formatTime(long millis) {
        if(millis < HOUR) {
            long mins = TimeUnit.MILLISECONDS.toMinutes(millis);
            return (mins + " Minutes");
        }
        else if(millis < DAY) {
            long hours = TimeUnit.MILLISECONDS.toHours(millis);
            return (hours + " Hours");
        }
        else{
            long days = TimeUnit.MILLISECONDS.toDays(millis);
            long subtrDays = TimeUnit.DAYS.toMillis(days);
            long hours = TimeUnit.MILLISECONDS.toHours(millis - subtrDays);
            return (days + " Days, " + hours + " Hours");
        }
    }

    public static <K> TreeMap<Integer, K> getSortedMap(Set<K> set) {
        TreeMap<Integer, K> map = new TreeMap<Integer, K>();
        Iterator<K> it = set.iterator();
        int i = 1;
        while(it.hasNext()) {
            map.put(i++, it.next());
        }
        return map;
    }

    public static String concatenate(String start, Collection<?> values, String startDelim, String delim) {
        StringBuilder builder = new StringBuilder(start);

        String d = startDelim;
        for(Object str : values) {
            builder.append(d).append(str.toString());
            d = delim;
        }
        return builder.toString();
    }

    public static double round(double in, DecimalFormat format) {
        return Double.parseDouble(format.format(in));
    }

    public static int hexToInt(String hex) {
        return Integer.parseInt(hex, 16);
    }

    public static int createRandomHex(int length) {
        return rand.nextInt((int) Math.pow(0x10, length));
    }

    public static Map<String, Object> toMap(Location loc) {
        if(loc == null) return null;
        Map<String, Object> ret = new HashMap<String, Object>();
        ret.put("world", loc.getWorld().getName());
        ret.put("x", loc.getX());
        ret.put("y", loc.getY());
        ret.put("z", loc.getZ());
        ret.put("yaw", loc.getYaw());
        ret.put("pitch", loc.getPitch());
        return ret;
    }

    /**
     * Returns a psuedo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimim value
     * @param max Maximim value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int randInt(int min, int max) {
        return rand.nextInt((max - min) + 1) + min;
    }

    /**
     * Returns a pseudo-random number between 0 (inclusive) and max (exclusive).
     * @param max Max value, exclusive
     * @return A random int
     */
    public static int randInt(int max) {
        return rand.nextInt(max);
    }

    public static <T> T randInArray(T[] array) {
        int index = rand.nextInt(array.length);
        return array[index];
    }

    public static void cancelFall(Player player) {
        boolean isNearGround = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().isSolid();
        if (isNearGround) return;

        CancelFallManager.inst().addPlayer(player.getUniqueId());
    }

    public static long toTicks(long time, TimeUnit unit) {
        return unit.toSeconds(time) * 20;
    }

    public static TimeUnit mostSignificantTimeUnit(long millis, TimeUnit startUnit, TimeUnit stopUnit) {
        int start = (startUnit == null ? 0 : unitsDescending.indexOf(startUnit));
        Iterator<TimeUnit> it = unitsDescending.listIterator(start);
        for(TimeUnit unit = it.next(); it.hasNext();) {
            long newTime = TimeUnit.MILLISECONDS.convert(millis, unit);
            if(newTime > 0 || unit == stopUnit) return unit;
        }
        return TimeUnit.MILLISECONDS;
    }
}
