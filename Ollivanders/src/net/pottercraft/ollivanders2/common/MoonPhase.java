package net.pottercraft.ollivanders2.common;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * 0 - Full Moon
 * 1 - Waning Gibbous
 * 2 - Last Quarter
 * 3 - Waning Crescent
 * 4 - New Moon
 * 5 - Waxing Crescent
 * 6 - First Quarter
 * 7 - Waxing Gibbous
 */
public enum MoonPhase {
    FULL_MOON,
    WANING_GIBBOUS,
    LAST_QUARTER,
    WANING_CRESCENT,
    NEW_MOON,
    WAXING_CRESCENT,
    FIRST_QUARTER,
    WAXING_GIBBOUS;

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
