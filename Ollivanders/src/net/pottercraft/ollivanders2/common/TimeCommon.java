package net.pottercraft.ollivanders2.common;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Standard times of day in Minecraft, each carrying the day-relative tick used with {@code /time set}. A Minecraft
 * day is 24000 ticks.
 *
 * @author Azami7
 * @see <a href="https://www.digminecraft.com/game_commands/time_set_day.php">Minecraft /time set values</a>
 */
public enum TimeCommon {
    /**
     * Midnight (18000 ticks).
     */
    MIDNIGHT(18000),
    /**
     * Dawn (23000 ticks).
     */
    DAWN(23000),
    /**
     * Midday (6000 ticks).
     */
    MIDDAY(6000),
    /**
     * Sunset (12000 ticks).
     */
    SUNSET(12000),
    /**
     * Moonrise (13000 ticks).
     */
    MOONRISE(13000),
    /**
     * Sunrise (24000 ticks).
     */
    SUNRISE(24000);

    final private int gameTick;

    /**
     * @param tick the day-relative tick (0-23999) for this time of day
     */
    TimeCommon(int tick) {
        gameTick = tick;
    }

    /**
     * Get the day-relative tick for this time of day.
     *
     * @return the day-relative tick
     */
    public int getTick() {
        return gameTick;
    }

    /**
     * Get the cumulative full time for the default (first-loaded) world. This is total ticks since world creation,
     * not the day-relative time; take {@code % 24000} for the latter.
     *
     * @return the default world's full time in ticks
     */
    static public long getDefaultWorldTime() {
        return Bukkit.getServer().getWorlds().getFirst().getFullTime();
    }

    /**
     * Get the current real-world system time (not game time), for logging and file naming.
     *
     * @return the timestamp formatted as yyyy-MM-dd-HH-mm-ss
     */
    @NotNull
    static public String getCurrentTimestamp() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = new Date();

        return dateFormat.format(date);
    }
}
