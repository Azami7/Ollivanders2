package net.pottercraft.ollivanders2.test.common;

import net.pottercraft.ollivanders2.common.MoonPhase;
import org.bukkit.World;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test suite for the MoonPhase utility enum.
 *
 * <p>Tests the moon phase calculation that determines the current lunar phase based on world time.
 * Verifies that:</p>
 * <ul>
 * <li>All 8 moon phases are correctly identified based on world time</li>
 * <li>Moon phase cycles through all phases in the correct order</li>
 * <li>Full moon (phase 0) is correctly returned as the default</li>
 * <li>Phase transitions occur at the correct world time intervals (every 24000 ticks / Minecraft day)</li>
 * </ul>
 *
 * <p>The moon phase is calculated as (worldTime / 24000) % 8, where each phase represents
 * one-eighth of the full lunar cycle (8 Minecraft days).</p>
 */
public class MoonPhaseTest {
    static ServerMock mockServer;
    private World world;

    @BeforeAll
    static public void globalSetup() {
        mockServer = MockBukkit.mock();
    }

    @BeforeEach
    public void setup() {
        world = mockServer.addSimpleWorld("world");
    }

    /**
     * Test that full moon (phase 0) is correctly identified.
     *
     * <p>Verifies that when world time is 0 or any multiple of 24000 * 8,
     * the full moon phase is returned.</p>
     */
    @Test
    void testFullMoon() {
        // Set world time to 0 (full moon)
        world.setFullTime(0);
        assertEquals(MoonPhase.FULL_MOON, MoonPhase.getMoonPhase(world),
            "Full moon not identified at world time 0");

        // Test another full moon cycle
        world.setFullTime(24000L * 8);
        assertEquals(MoonPhase.FULL_MOON, MoonPhase.getMoonPhase(world),
            "Full moon not identified at world time 192000");
    }

    /**
     * Test that waning gibbous (phase 1) is correctly identified.
     */
    @Test
    void testWaningGibbous() {
        world.setFullTime(24000L);
        assertEquals(MoonPhase.WANING_GIBBOUS, MoonPhase.getMoonPhase(world),
            "Waning gibbous not identified at world time 24000");
    }

    /**
     * Test that last quarter (phase 2) is correctly identified.
     */
    @Test
    void testLastQuarter() {
        world.setFullTime(24000L * 2);
        assertEquals(MoonPhase.LAST_QUARTER, MoonPhase.getMoonPhase(world),
            "Last quarter not identified at world time 48000");
    }

    /**
     * Test that waning crescent (phase 3) is correctly identified.
     */
    @Test
    void testWaningCrescent() {
        world.setFullTime(24000L * 3);
        assertEquals(MoonPhase.WANING_CRESCENT, MoonPhase.getMoonPhase(world),
            "Waning crescent not identified at world time 72000");
    }

    /**
     * Test that new moon (phase 4) is correctly identified.
     */
    @Test
    void testNewMoon() {
        world.setFullTime(24000L * 4);
        assertEquals(MoonPhase.NEW_MOON, MoonPhase.getMoonPhase(world),
            "New moon not identified at world time 96000");
    }

    /**
     * Test that waxing crescent (phase 5) is correctly identified.
     */
    @Test
    void testWaxingCrescent() {
        world.setFullTime(24000L * 5);
        assertEquals(MoonPhase.WAXING_CRESCENT, MoonPhase.getMoonPhase(world),
            "Waxing crescent not identified at world time 120000");
    }

    /**
     * Test that first quarter (phase 6) is correctly identified.
     */
    @Test
    void testFirstQuarter() {
        world.setFullTime(24000L * 6);
        assertEquals(MoonPhase.FIRST_QUARTER, MoonPhase.getMoonPhase(world),
            "First quarter not identified at world time 144000");
    }

    /**
     * Test that waxing gibbous (phase 7) is correctly identified.
     */
    @Test
    void testWaxingGibbous() {
        world.setFullTime(24000L * 7);
        assertEquals(MoonPhase.WAXING_GIBBOUS, MoonPhase.getMoonPhase(world),
            "Waxing gibbous not identified at world time 168000");
    }

    /**
     * Test that moon phases cycle correctly through multiple lunar cycles.
     *
     * <p>Verifies that the moon phase calculation correctly cycles through all 8 phases
     * multiple times as world time progresses.</p>
     */
    @Test
    void testMoonPhaseCycle() {
        MoonPhase[] expectedPhases = {
            MoonPhase.FULL_MOON,
            MoonPhase.WANING_GIBBOUS,
            MoonPhase.LAST_QUARTER,
            MoonPhase.WANING_CRESCENT,
            MoonPhase.NEW_MOON,
            MoonPhase.WAXING_CRESCENT,
            MoonPhase.FIRST_QUARTER,
            MoonPhase.WAXING_GIBBOUS
        };

        // Test first cycle
        for (int i = 0; i < 8; i++) {
            world.setFullTime(24000L * i);
            assertEquals(expectedPhases[i], MoonPhase.getMoonPhase(world),
                "Phase " + i + " not matched at world time " + (24000L * i));
        }

        // Test second cycle to verify wrapping
        for (int i = 0; i < 8; i++) {
            world.setFullTime(24000L * (8 + i));
            assertEquals(expectedPhases[i], MoonPhase.getMoonPhase(world),
                "Phase " + i + " not matched in second cycle at world time " + (24000L * (8 + i)));
        }
    }

    /**
     * Test that intermediate world times (midday) return the correct phase.
     *
     * <p>Verifies that the phase calculation ignores partial days. For example,
     * world time 1000 (partway through the first day) should still return full moon.</p>
     */
    @Test
    void testIntermediateDayTimes() {
        // Mid-way through first full moon day
        world.setFullTime(12000);
        assertEquals(MoonPhase.FULL_MOON, MoonPhase.getMoonPhase(world),
            "Full moon not maintained during day 0");

        // Mid-way through first waning gibbous day
        world.setFullTime(24000L + 12000);
        assertEquals(MoonPhase.WANING_GIBBOUS, MoonPhase.getMoonPhase(world),
            "Waning gibbous not maintained during day 1");

        // Mid-way through new moon day
        world.setFullTime(24000L * 4 + 12000);
        assertEquals(MoonPhase.NEW_MOON, MoonPhase.getMoonPhase(world),
            "New moon not maintained during day 4");
    }

    /**
     * Global test teardown. Unmocks the MockBukkit server.
     */
    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}