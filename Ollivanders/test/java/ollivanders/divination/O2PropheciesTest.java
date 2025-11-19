package ollivanders.divination;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.divination.O2Prophecies;
import net.pottercraft.ollivanders2.divination.O2Prophecy;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import ollivanders.testcommon.TestCommon;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for O2Prophecies class.
 * Comprehensive test covering prophecy creation, retrieval, aging, persistence, and player offline/online transitions.
 * This is a mega test case to avoid non-deterministic test run order issues with JUnit 5.
 */
public class O2PropheciesTest {
    static ServerMock mockServer;
    static Ollivanders2 testPlugin;
    static O2Prophecies prophecies;
    static int delayTicks = 10;
    static int effectDurationTicks = 10;
    static int maxAccuracy;

    /**
     * Global test setup. Initializes MockBukkit server, loads the plugin with configuration,
     * and advances the server to allow the scheduler to start.
     */
    @BeforeAll
    static void globalSetUp() {
        mockServer = MockBukkit.mock();
        maxAccuracy = O2Prophecy.maxAccuracy;

        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));
        prophecies = Ollivanders2API.getProphecies();

        // advance the server to let the scheduler start
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    /**
     * Comprehensive test of O2Prophecies functionality.
     * Tests include:
     * - Adding prophecies and retrieving them by target or prophet
     * - Searching for prophecies that don't exist
     * - Aging prophecies and handling expiration
     * - Saving prophecies to disk and loading them back
     * - Verifying loaded prophecies have correct data
     * - Handling player disconnection/reconnection (offline/online transitions)
     * - getProphecy() method for both online and offline players
     *
     * This is a mega test case to avoid non-deterministic test run order issues with JUnit 5.
     */
    @Test
    void propheciesTest() {
        Ollivanders2.debug = true;
        PlayerMock prophet1 = mockServer.addPlayer("Prophet1");
        PlayerMock prophet2 = mockServer.addPlayer("Prophet2");
        PlayerMock target1 = mockServer.addPlayer("Target1");
        PlayerMock target2 = mockServer.addPlayer("Target2");

        // make sure we're starting with a clean list
        assertEquals(0, prophecies.getProphecies().size(), "Tests did not start out with empty prophecies list.");

        // add a prophecy
        String message1 = "test prophecy";
        prophecies.addProphecy(new O2Prophecy(testPlugin, O2EffectType.UNLUCK, message1, target1.getUniqueId(), prophet1.getUniqueId(), delayTicks, effectDurationTicks, maxAccuracy));

        // number of prophecies increases by 1
        assertEquals(1, prophecies.getProphecies().size(), "The number of prophecies did not increase by 1 after prophecies.addProphecy()");

        // we can find a prophecy with the expected message
        assertTrue(TestCommon.containsStringMatch(prophecies.getProphecies(), message1), "Did not find prophecy with matching message, " + message1);

        // we can find the prophecy about target1
        O2Prophecy prophecy = prophecies.getProphecyAboutPlayer(target1.getUniqueId());
        assertNotNull(prophecy, "Prophecy was null after prophecies.addProphecy()");
        assertTrue(prophecy.getProphecyMessage().contains(message1), "Prophecy did not have expected message");

        // we do find a prophecy about target2
        prophecy = prophecies.getProphecyAboutPlayer(target2.getUniqueId());
        assertNull(prophecy, "prophecies.getProphecyAboutPlayer(target2.getUniqueId()) was not null");

        // we do not find a prophet about prophet1
        prophecy = prophecies.getProphecyAboutPlayer(prophet1.getUniqueId());
        assertNull(prophecy, "prophecies.getProphecyAboutPlayer(prophet1.getUniqueId()) was not null");

        // we can find a prophecy by prophet1
        prophecy = prophecies.getProphecyByPlayer(prophet1.getUniqueId());
        assertNotNull(prophecy, "prophecies.getProphecyByPlayer(prophet1.getUniqueId()) was null");
        assertTrue(prophecy.getProphecyMessage().contains(message1), "Prophecy did not have expected message");

        // we do not find a prophecy by prophet2
        prophecy = prophecies.getProphecyByPlayer(prophet2.getUniqueId());
        assertNull(prophecy, "prophecies.getProphecyByPlayer(prophet2.getUniqueId()) is not null");

        // we do not find a prophecy by target1
        prophecy = prophecies.getProphecyByPlayer(target1.getUniqueId());
        assertNull(prophecy, "prophecies.getProphecyByPlayer(target1.getUniqueId()) was not null");

        // test upkeep processing
        // advance the server by 1 tick and make sure the number of prophecies has not changed
        mockServer.getScheduler().performTicks(1);
        assertEquals(1, prophecies.getProphecies().size(), "After " + TestCommon.startupTicks + " game ticks, number of prophecies changed.");

        // advance the server by delayTicks so the prophecy ages out
        mockServer.getScheduler().performTicks(delayTicks);
        assertEquals(0, prophecies.getProphecies().size(), "After " + delayTicks + " more game ticks, prophecies did not expire due to age");

        // test saving and loading prophecies
        // add 2 new prophecies
        message1 = "test prophecy 1";
        prophecies.addProphecy(new O2Prophecy(testPlugin, O2EffectType.UNLUCK, message1, target1.getUniqueId(), prophet1.getUniqueId(), delayTicks, effectDurationTicks, maxAccuracy));
        String message2 = "test prophecy 2";
        prophecies.addProphecy(new O2Prophecy(testPlugin, O2EffectType.UNLUCK, message2, target2.getUniqueId(), prophet2.getUniqueId(), delayTicks, effectDurationTicks, maxAccuracy));
        // save them
        prophecies.saveProphecies();
        assertEquals(2, prophecies.getProphecies().size(), "prophecies.getProphecies().size() not 2 after adding 2 prophecies");
        // clear the loaded prophecies
        prophecies.resetProphecies();
        assertEquals(0, prophecies.getProphecies().size(), "prophecies.getProphecies().size() not 0 after prophecies.resetProphecies();");
        // load saved prophecies
        prophecies.loadProphecies();
        assertEquals(2, prophecies.getProphecies().size(), "prophecies.getProphecies().size() not 2 after prophecies.loadProphecies();");
        // now make sure the loaded prophecies are right
        prophecy = prophecies.getProphecyByPlayer(prophet1.getUniqueId());
        assertNotNull(prophecy, "prophecies.getProphecyByPlayer(prophet1.getUniqueId()) was null after prophecies.loadProphecies()");
        assertTrue(prophecy.getProphecyMessage().contains(message1), "prophecy did not have expected message");
        prophecy = prophecies.getProphecyByPlayer(prophet2.getUniqueId());
        assertNotNull(prophecy, "prophecies.getProphecyByPlayer(prophet2.getUniqueId()) was null after prophecies.loadProphecies()");
        assertTrue(prophecy.getProphecyMessage().contains(message2), "prophecy did not have expected message");

        // test onJoin
        // disconnect target1
        target1.disconnect();
        // age the prophecies so that theirs moves to offlineProphecies
        mockServer.getScheduler().performTicks(delayTicks + 1);
        // verify the prophecies have moved to offline (should have 0 in active list)
        assertEquals(0, prophecies.activeProphecyCount(), "Prophecies did not move to offline after " + (delayTicks + 1) + " ticks.");
        // reconnect target1
        target1.reconnect();
        // verify onJoin() moved their offlineProphecy back to the active list
        assertEquals(1, prophecies.activeProphecyCount(), "onJoin did not add an active prophecy for target1 on reconnect");

        // getProphecy will return a prophecy about the specified player when they are online
        prophecies.resetProphecies(); // clear the prophecies to reset
        prophecies.addProphecy(new O2Prophecy(testPlugin, O2EffectType.UNLUCK, message2, target2.getUniqueId(), prophet2.getUniqueId(), delayTicks, effectDurationTicks, maxAccuracy));
        assertNotNull(prophecies.getProphecy(target2.getUniqueId()));

        // getProphecy will return a prophecy about the specified player when they are offline
        target2.disconnect();
        assertNotNull(prophecies.getProphecy(target2.getUniqueId()));

        // getProphecy will return a prophecy about the specified player when it has moved to the offline list
        mockServer.getScheduler().performTicks(delayTicks + 1);
        assertNotNull(prophecies.getProphecy(target2.getUniqueId()));
    }

    /**
     * Global test teardown. Unmocks the MockBukkit server.
     */
    @AfterAll
    static void globalTearDown () {
        prophecies.resetProphecies();
        MockBukkit.unmock();

        Ollivanders2.debug = false;
    }
}
