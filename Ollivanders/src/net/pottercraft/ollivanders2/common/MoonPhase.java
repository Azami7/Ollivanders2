package net.pottercraft.ollivanders2.common;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * Enumeration of the eight moon phases in Minecraft.
 *
 * <p>The moon phase cycles every 8 Minecraft days (8 * 24000 ticks). Each phase represents a different
 * point in the lunar cycle, from full moon through new moon and back. Moon phases can be used for
 * spell calculations and lunar-based mechanics.</p>
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
     * Get the current moon phase for a world.
     *
     * <p>Calculates the moon phase based on the world's full time, which cycles every 8 Minecraft days.
     * The full time is divided by 24000 (ticks per day) and modulo 8 to get the phase.</p>
     *
     * @param world the world to check
     * @return the current moon phase
     */
    @NotNull
    public static MoonPhase getMoonPhase(@NotNull World world) {
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
