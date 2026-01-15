package net.pottercraft.ollivanders2.common;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Enumeration of standard times of day in Minecraft, based on day-relative ticks.
 * <p>
 * Minecraft days cycle through 24000 ticks (20 ticks per second = 20 minutes per full day).
 * Day-relative ticks are the tick value modulo 24000, ranging from 0 to 23999, where:
 * <ul>
 * <li>0 = Midnight (start of day)</li>
 * <li>6000 = Midday (noon)</li>
 * <li>12000 = Sunset</li>
 * <li>18000 = Midnight (end of day)</li>
 * </ul>
 * <p>
 * These constants represent the standard times of day and can be used with commands like `/time set`.
 * </p>
 *
 * @author Azami7
 * @see <a href="https://www.digminecraft.com/game_commands/time_set_day.php">https://www.digminecraft.com/game_commands/time_set_day.php</a>
 */
public enum TimeCommon {
    /**
     * Midnight (18000 ticks) - the start of the night cycle, when darkness is complete.
     */
    MIDNIGHT(18000),
    /**
     * Dawn (23000 ticks) - early morning when the sun is just beginning to rise.
     */
    DAWN(23000),
    /**
     * Midday (6000 ticks) - noon, when the sun is at its highest point and brightness is maximum.
     */
    MIDDAY(6000),
    /**
     * Sunset (12000 ticks) - evening when the sun begins to set and darkness is approaching.
     */
    SUNSET(12000),
    /**
     * Moonrise (13000 ticks)
     */
    MOONRISE(13000);

    /**
     * The day-relative tick for this enumerated time of day
     */
    final private int gameTick;

    /**
     * Constructor for TimeCommon enum constants.
     *
     * @param tick the day-relative tick value (0-23999) representing this time of day in the Minecraft cycle
     */
    TimeCommon(int tick) {
        gameTick = tick;
    }

    /**
     * Get the day-relative tick value for this enumerated time of day
     *
     * @return the day-relative tick for this time of day
     */
    public int getTick() {
        return gameTick;
    }

    /**
     * Get the full time (cumulative ticks) for the default world.
     * <p>
     * Returns the full time in ticks since the world was created, not the day-relative time (0-23999).
     * To get the day-relative time, use: {@code getDefaultWorldTime() % 24000}
     * </p>
     *
     * @return the full time in ticks for the default world
     */
    static public long getDefaultWorldTime() {
        return Bukkit.getServer().getWorlds().getFirst().getFullTime();
    }

    /**
     * Get the current system date and time as a formatted string.
     * <p>
     * Returns the system date/time (not Minecraft game time). This is typically used for logging,
     * file naming, or other real-world timestamp purposes.
     * </p>
     *
     * @return the current system timestamp in the format yyyy-MM-dd-HH-mm-ss (e.g., "2018-09-30-12-15-30")
     */
    @NotNull
    static public String getCurrentTimestamp() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = new Date();

        return dateFormat.format(date);
    }
}
