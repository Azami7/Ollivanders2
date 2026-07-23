package net.pottercraft.ollivanders2.test.player;

import net.pottercraft.ollivanders2.common.MagicLevel;
import net.pottercraft.ollivanders2.player.Year;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link Year}.
 */
public class YearTest {
    /**
     * Every year can be looked up by its 0-based value and out-of-range values return null.
     */
    @Test
    void testGetYearByValue() {
        // every year round-trips through its ordinal
        for (Year year : Year.values()) {
            assertSame(year, Year.getYearByValue(year.ordinal()),
                    year + " should be returned for value " + year.ordinal());
        }

        // out-of-range values return null
        assertNull(Year.getYearByValue(-1), "Negative value should return null");
        assertNull(Year.getYearByValue(Year.values().length), "Value past the last year should return null");
    }

    /**
     * Display text is the expected ordinal word for the first and last years.
     */
    @Test
    void testGetDisplayText() {
        assertEquals("1st", Year.YEAR_1.getDisplayText(), "YEAR_1 display text should be 1st");
        assertEquals("7th", Year.YEAR_7.getDisplayText(), "YEAR_7 display text should be 7th");
    }

    /**
     * Max spell level progresses with year and never decreases.
     */
    @Test
    void testGetHighestLevelForYear() {
        assertEquals(MagicLevel.BEGINNER, Year.YEAR_1.getHighestLevelForYear(), "YEAR_1 should be BEGINNER level");
        assertEquals(MagicLevel.OWL, Year.YEAR_3.getHighestLevelForYear(), "YEAR_3 should be OWL level");
        assertEquals(MagicLevel.NEWT, Year.YEAR_5.getHighestLevelForYear(), "YEAR_5 should be NEWT level");
        assertEquals(MagicLevel.EXPERT, Year.YEAR_7.getHighestLevelForYear(), "YEAR_7 should be EXPERT level");

        // levels never decrease as years advance
        for (int i = 1; i < Year.values().length; i++) {
            Year previous = Year.values()[i - 1];
            Year current = Year.values()[i];
            assertTrue(current.getHighestLevelForYear().ordinal() >= previous.getHighestLevelForYear().ordinal(),
                    current + " level should not be lower than " + previous);
        }
    }
}