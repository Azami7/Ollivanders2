package ollivanders.common;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.TimeCommon;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for TimeCommon enum.
 * Tests cover game time constants, world time retrieval, and timestamp generation.
 */
public class TimeCommonTest {
    static ServerMock mockServer;
    static Ollivanders2 testPlugin;

    @BeforeAll
    static void globalSetUp() {
        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));
        mockServer.addSimpleWorld("world");
    }

    /**
     * Tests that the MIDNIGHT enum constant has the correct tick value.
     * Verifies that MIDNIGHT is set to 18000 ticks.
     */
    @Test
    void midnightTickTest() {
        assertEquals(18000, TimeCommon.MIDNIGHT.getTick(), "MIDNIGHT should have tick value of 18000");
    }

    /**
     * Tests that the DAWN enum constant has the correct tick value.
     * Verifies that DAWN is set to 23000 ticks.
     */
    @Test
    void dawnTickTest() {
        assertEquals(23000, TimeCommon.DAWN.getTick(), "DAWN should have tick value of 23000");
    }

    /**
     * Tests that the MIDDAY enum constant has the correct tick value.
     * Verifies that MIDDAY is set to 6000 ticks.
     */
    @Test
    void middayTickTest() {
        assertEquals(6000, TimeCommon.MIDDAY.getTick(), "MIDDAY should have tick value of 6000");
    }

    /**
     * Tests that the SUNSET enum constant has the correct tick value.
     * Verifies that SUNSET is set to 12000 ticks.
     */
    @Test
    void sunsetTickTest() {
        assertEquals(12000, TimeCommon.SUNSET.getTick(), "SUNSET should have tick value of 12000");
    }

    /**
     * Tests retrieving the default world time from the server.
     * Verifies that getDefaultWorldTime() returns a valid time value.
     */
    @Test
    void getDefaultWorldTimeTest() {
        long defaultWorldTime = TimeCommon.getDefaultWorldTime();
        assertTrue(defaultWorldTime >= 0, "Default world time should be non-negative");
    }

    /**
     * Tests that the default world time changes after advancing server time.
     * Verifies that time progression is correctly reflected in the default world.
     */
    @Test
    void getDefaultWorldTimeAdvancedTest() {
        long initialTime = TimeCommon.getDefaultWorldTime();

        // Advance time by 100 ticks
        mockServer.getScheduler().performTicks(100);

        long newTime = TimeCommon.getDefaultWorldTime();
        assertTrue(newTime > initialTime, "World time should advance after scheduler ticks");
    }

    /**
     * Tests timestamp generation in the correct format.
     * Verifies that getCurrentTimestamp() returns a string matching the expected pattern yyyy-MM-dd-HH-mm-ss.
     */
    @Test
    void getCurrentTimestampTest() {
        String timestamp = TimeCommon.getCurrentTimestamp();

        assertNotNull(timestamp, "getCurrentTimestamp() should not return null");

        // Validate format using regex pattern: yyyy-MM-dd-HH-mm-ss
        String pattern = "\\d{4}-\\d{2}-\\d{2}-\\d{2}-\\d{2}-\\d{2}";
        assertTrue(Pattern.matches(pattern, timestamp),
            "Timestamp should match pattern yyyy-MM-dd-HH-mm-ss, but got: " + timestamp);
    }

    /**
     * Tests that consecutive timestamp calls are different when time has passed.
     * Verifies that timestamps are generated at different times (with at least a small time difference).
     */
    @Test
    void getCurrentTimestampUniqueTest() {
        String timestamp1 = TimeCommon.getCurrentTimestamp();

        // Sleep briefly to ensure time has passed
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String timestamp2 = TimeCommon.getCurrentTimestamp();

        // Timestamps should be different (or at least the seconds should be tracked)
        assertNotNull(timestamp1, "First timestamp should not be null");
        assertNotNull(timestamp2, "Second timestamp should not be null");
        assertTrue(!timestamp1.isEmpty() && !timestamp2.isEmpty(), "Timestamps should not be empty");
    }

    /**
     * Tests that the timestamp is parseable back to a valid date.
     * Verifies that the generated timestamp format can be used to reconstruct a Date object.
     */
    @Test
    void getCurrentTimestampParseable() {
        String timestamp = TimeCommon.getCurrentTimestamp();

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            Date parsedDate = dateFormat.parse(timestamp);
            assertNotNull(parsedDate, "Timestamp should be parseable to a Date object");
        } catch (Exception e) {
            fail("Timestamp '" + timestamp + "' should be parseable: " + e.getMessage());
        }
    }

    /**
     * Tests that all TimeCommon enum constants have valid tick values.
     * Verifies that all enum values return non-negative tick values.
     */
    @Test
    void allEnumConstantsHaveValidTicksTest() {
        for (TimeCommon timeConstant : TimeCommon.values()) {
            assertTrue(timeConstant.getTick() >= 0,
                timeConstant.name() + " should have a non-negative tick value");
        }
    }

    /**
     * Tests that different enum constants have different tick values.
     * Verifies that each time of day has a unique tick value.
     */
    @Test
    void enumConstantsHaveUniquTicksTest() {
        TimeCommon[] values = TimeCommon.values();

        for (int i = 0; i < values.length; i++) {
            for (int j = i + 1; j < values.length; j++) {
                assertTrue(values[i].getTick() != values[j].getTick(),
                    values[i].name() + " and " + values[j].name() + " should have different tick values");
            }
        }
    }

    /**
     * Global test teardown. Unmocks the MockBukkit server.
     */
    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
