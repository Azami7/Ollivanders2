package net.pottercraft.ollivanders2.player;

import net.pottercraft.ollivanders2.common.MagicLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the year this player is in school.
 */
public enum Year {
    /**
     * "1st", MagicLevel.BEGINNER
     */
    YEAR_1("1st", MagicLevel.BEGINNER),
    /**
     * "2nd", MagicLevel.BEGINNER
     */
    YEAR_2("2nd", MagicLevel.BEGINNER),
    /**
     * "3rd", MagicLevel.OWL
     */
    YEAR_3("3rd", MagicLevel.OWL),
    /**
     * "4th", MagicLevel.OWL
     */
    YEAR_4("4th", MagicLevel.OWL),
    /**
     * "5th", MagicLevel.NEWT
     */
    YEAR_5("5th", MagicLevel.NEWT),
    /**
     * "6th", MagicLevel.NEWT
     */
    YEAR_6("6th", MagicLevel.NEWT),
    /**
     * "7th", MagicLevel.EXPERT
     */
    YEAR_7("7th", MagicLevel.EXPERT);

    /**
     * The display text for this year when sending messages.
     */
    final String displayText;

    /**
     * The highest spell level this player can cast without some sort of penalty for inexperience.
     */
    final MagicLevel maxLevel;

    /**
     * Constructor
     *
     * @param text the string for this year
     */
    Year(@NotNull String text, @NotNull MagicLevel level) {
        displayText = text;
        maxLevel = level;
    }

    /**
     * Get the text for writing this year, example: "3rd year"
     *
     * @return the display text for this year
     */
    @NotNull
    public String getDisplayText() {
        return displayText;
    }

    /**
     * Get the year that has the corresponding value
     *
     * @param value the value to get
     * @return the Year if exists, null otherwise
     */
    @Nullable
    public static Year getYearByValue(int value) {
        for (Year year : Year.values()) {
            if (year.ordinal() == value)
                return year;
        }

        return null;
    }

    /**
     * The highest spell level this year can do without possible experience penalties.
     *
     * @return the maximum spell level
     */
    public MagicLevel getHighestLevelForYear() {
        return maxLevel;
    }
}
