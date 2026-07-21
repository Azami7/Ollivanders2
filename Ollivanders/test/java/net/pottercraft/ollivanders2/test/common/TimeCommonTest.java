package net.pottercraft.ollivanders2.test.common;

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
 * Unit tests for {@link TimeCommon}.
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
     * MIDNIGHT is 18000 ticks.
     */
    @Test
    void midnightTickTest() {
        assertEquals(18000, TimeCommon.MIDNIGHT.getTick(), "MIDNIGHT should have tick value of 18000");
    }

    /**
     * DAWN is 23000 ticks.
     */
    @Test
    void dawnTickTest() {
        assertEquals(23000, TimeCommon.DAWN.getTick(), "DAWN should have tick value of 23000");
    }

    /**
     * MIDDAY is 6000 ticks.
     */
    @Test
    void middayTickTest() {
        assertEquals(6000, TimeCommon.MIDDAY.getTick(), "MIDDAY should have tick value of 6000");
    }

    /**
     * SUNSET is 12000 ticks.
     */
    @Test
    void sunsetTickTest() {
        assertEquals(12000, TimeCommon.SUNSET.getTick(), "SUNSET should have tick value of 12000");
    }

    /**
     * getDefaultWorldTime() returns a valid (non-negative) time.
     */
    @Test
    void getDefaultWorldTimeTest() {
        long defaultWorldTime = TimeCommon.getDefaultWorldTime();
        assertTrue(defaultWorldTime >= 0, "Default world time should be non-negative");
    }

    /**
     * The default world time advances as the scheduler ticks.
     */
    @Test
    void getDefaultWorldTimeAdvancedTest() {
        long initialTime = TimeCommon.getDefaultWorldTime();

        mockServer.getScheduler().performTicks(100);

        long newTime = TimeCommon.getDefaultWorldTime();
        assertTrue(newTime > initialTime, "World time should advance after scheduler ticks");
    }

    /**
     * getCurrentTimestamp() matches the pattern yyyy-MM-dd-HH-mm-ss.
     */
    @Test
    void getCurrentTimestampTest() {
        String timestamp = TimeCommon.getCurrentTimestamp();

        assertNotNull(timestamp, "getCurrentTimestamp() should not return null");

        String pattern = "\\d{4}-\\d{2}-\\d{2}-\\d{2}-\\d{2}-\\d{2}";
        assertTrue(Pattern.matches(pattern, timestamp),
            "Timestamp should match pattern yyyy-MM-dd-HH-mm-ss, but got: " + timestamp);
    }

    /**
     * Consecutive timestamp calls each return a non-empty value.
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

        assertNotNull(timestamp1, "First timestamp should not be null");
        assertNotNull(timestamp2, "Second timestamp should not be null");
        assertTrue(!timestamp1.isEmpty() && !timestamp2.isEmpty(), "Timestamps should not be empty");
    }

    /**
     * A generated timestamp parses back into a Date.
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
     * Every constant has a non-negative tick value.
     */
    @Test
    void allEnumConstantsHaveValidTicksTest() {
        for (TimeCommon timeConstant : TimeCommon.values()) {
            assertTrue(timeConstant.getTick() >= 0,
                timeConstant.name() + " should have a non-negative tick value");
        }
    }

    /**
     * Every constant has a distinct tick value.
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
