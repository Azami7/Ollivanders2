package net.pottercraft.ollivanders2.common;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * The eight Minecraft moon phases, which cycle every 8 in-game days. Declared in the order Minecraft assigns to
 * consecutive day indices, so {@link #getMoonPhase} can map a world's day count to a phase.
 *
 * @author Azami7
 */
public enum MoonPhase {
    /** Full Moon - brightest lunar phase */
    FULL_MOON,
    /** Waning Gibbous - after full moon, gradually decreasing */
    WANING_GIBBOUS,
    /** Last Quarter - half moon, decreasing */
    LAST_QUARTER,
    /** Waning Crescent - thin crescent before new moon */
    WANING_CRESCENT,
    /** New Moon - darkest lunar phase */
    NEW_MOON,
    /** Waxing Crescent - thin crescent after new moon */
    WAXING_CRESCENT,
    /** First Quarter - half moon, increasing */
    FIRST_QUARTER,
    /** Waxing Gibbous - before full moon, gradually increasing */
    WAXING_GIBBOUS;

    /**
     * Get the current moon phase for a world, derived from its day count within the 8-day lunar cycle.
     *
     * @param world the world to check
     * @return the current moon phase
     */
    @NotNull
    public static MoonPhase getMoonPhase(@NotNull World world) {
        // 24000 ticks per Minecraft day; the phase repeats every 8 days
        int moonPhase = ((int)(world.getFullTime() / 24000)) % 8;

        if (moonPhase == 1)
            return WANING_GIBBOUS;
        else if (moonPhase == 2)
            return LAST_QUARTER;
        else if (moonPhase == 3)
            return WANING_CRESCENT;
        else if (moonPhase == 4)
            return NEW_MOON;
        else if (moonPhase == 5)
            return WAXING_CRESCENT;
        else if (moonPhase == 6)
            return FIRST_QUARTER;
        else if (moonPhase == 7)
            return WAXING_GIBBOUS;
        else
            return FULL_MOON;
    }
}
