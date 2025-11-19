package ollivanders.divination;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.divination.O2Prophecy;
import net.pottercraft.ollivanders2.effect.O2Effect;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for O2Prophecy class.
 * Tests cover prophecy aging, killing, fulfillment with high and low accuracy,
 * message broadcasting, effect application, and behavior with offline players.
 * All tests are in a single mega test method to avoid test order interference with JUnit 5.
 */
public class O2ProphecyTest {
    static ServerMock mockServer;
    static int maxAccuracy;
    static Ollivanders2 testPlugin;
    static PlayerMock prophet;
    static PlayerMock target;

    /**
     * Global test setup. Initializes MockBukkit server, loads the plugin with configuration,
     * creates two test players (prophet and target), and advances the server to allow the scheduler to start.
     */
    @BeforeAll
    static void globalSetUp() {
        mockServer = MockBukkit.mock();
        maxAccuracy = O2Prophecy.maxAccuracy;

        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));
        prophet = mockServer.addPlayer("prophet");
        target = mockServer.addPlayer("target");

        // advance the server to let the scheduler start
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    /**
     * Comprehensive test of O2Prophecy functionality.
     * Tests include:
     * - Aging prophecies and verifying time decreases
     * - Killing prophecies and checking killed state
     * - High accuracy (99) prophecy fulfillment with successful effect application
     * - Verification of broadcast messages to both target and prophet
     * - Effect duration expiration
     * - Low accuracy (0) prophecy fulfillment with failure messaging
     * - Prophecy fulfillment deferred when target is offline
     * - Prophecy fulfillment when offline target reconnects
     * - Prophecy fulfillment when prophet is offline (target still receives effect)
     * - Prophecy not fulfilled if already killed before fulfill() call
     *
     * This is a mega test case to avoid non-deterministic test run order issues with JUnit 5.
     */
    @Test
    void prophecyTest() {
        Ollivanders2.debug = true;
        String prophecyMessage = "test prophecy message";
        O2EffectType prophecyEffect = O2EffectType.FAST_LEARNING;
        int effectDuration = O2Effect.minDuration; // must be > 5

        // test age()
        O2Prophecy prophecy = new O2Prophecy(testPlugin, prophecyEffect, prophecyMessage, target.getUniqueId(), prophet.getUniqueId(), 10, effectDuration, 99);

        long age = prophecy.getTime();
        prophecy.age();
        assertEquals((age - 1), prophecy.getTime(), "prophecy.age() did not reduce prophecy age by 1");

        // test kill() and isKilled()
        assertFalse(prophecy.isKilled(), "Prophecy set to killed when not expected");
        prophecy.kill();
        assertTrue(prophecy.isKilled(), "Prophecy not set to killed when expected");

        // test fulfill
        // a prophecy with max accuracy - so should succeed
        prophecy = new O2Prophecy(testPlugin, prophecyEffect, prophecyMessage, target.getUniqueId(), prophet.getUniqueId(), 10, effectDuration, 99);
        prophecy.fulfill();
        mockServer.getScheduler().performTicks(5);

        // prophecy sent a message to both the target and player
        String targetReceivedMessage = target.nextMessage();
        assertNotNull(targetReceivedMessage, "target did not receive a prophecy message");
        String prophetReceivedMessage = prophet.nextMessage();
        assertNotNull(prophetReceivedMessage, "prophet did not receive a prophecy message");

        // target got the prophecy broadcast with the prophet's name and the prophecy message
        assertTrue(targetReceivedMessage.contains(prophet.getName()), "prophecy broadcast did not contain prophet's name");
        assertTrue(targetReceivedMessage.contains("And so came to pass the prophecy"), "Prophecy broadcast message does not contain 'And so came to pass the prophecy'");
        assertTrue(targetReceivedMessage.contains(prophecyMessage), "prophecy broadcast did not contain expected prophecy message, expected: " + prophecyMessage + ", actual: " + targetReceivedMessage);

        // prophet also got the prophecy broadcast
        assertTrue(prophetReceivedMessage.contains("And so came to pass the prophecy"), "Prophet did not receive the prophecy success broadcast message");

        // prophecy added the effect to the target
        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(target.getUniqueId(), prophecyEffect), "Target does not have prophecy effect");

        // prophecy does not add the effect to the prophet
        assertFalse(Ollivanders2API.getPlayers().playerEffects.hasEffect(prophet.getUniqueId(), prophecyEffect), "Prophet has the prophecy effect");

        // effect duration is correct
        mockServer.getScheduler().performTicks(effectDuration); // we've already done 5 ticks, doing effectDuration more should ensure the effect has been removed
        assertFalse(Ollivanders2API.getPlayers().playerEffects.hasEffect(target.getUniqueId(), prophecyEffect), "Target still has the prophecy effect after expected duration");

        // low accuracy prophecy sends failure message to prophet and nothing to target, no effect added to target
        prophecyMessage = "low accuracy prophecy";
        prophecy = new O2Prophecy(testPlugin, prophecyEffect, prophecyMessage, target.getUniqueId(), prophet.getUniqueId(), 10, 10, 0);
        prophecy.fulfill();
        prophetReceivedMessage = prophet.nextMessage();
        assertNotNull(prophetReceivedMessage, "prophet did not receive a prophecy message");
        assertTrue(prophetReceivedMessage.contains("did not come to pass"), "Prophet did not receive the prophecy failure message");
        targetReceivedMessage = target.nextMessage();
        assertNull(targetReceivedMessage, "Target received prophecy message when prophecy failed");
        assertFalse(Ollivanders2API.getPlayers().playerEffects.hasEffect(target.getUniqueId(), prophecyEffect), "Target has the prophecy effect when prophecy failed");

        // prophecy fulfillment deferred when target is offline
        prophecy = new O2Prophecy(testPlugin, prophecyEffect, "test", target.getUniqueId(), prophet.getUniqueId(), 3, effectDuration, 99);
        Ollivanders2API.getProphecies().addProphecy(prophecy); // add it to the prophecy manager so we can test the persistence
        target.disconnect();
        mockServer.getScheduler().performTicks(5); // advance the ticks past the delay time so O2Prophecies will call fulfill
        prophetReceivedMessage = prophet.nextMessage();
        assertNull(prophetReceivedMessage, "Prophet received prophecy message when target offline: " + prophetReceivedMessage);

        // prophecy is fulfilled when target comes back online - technically this is testing O2Prophecies.java but it is easier to do here when we have the server in the right state.
        target.reconnect();
        mockServer.getScheduler().performTicks(5);
        assertNotNull(prophet.nextMessage(), "Prophet did not receive prophecy message when target reconnected");
        assertNotNull(target.nextMessage(), "Target did not receive prophecy message when they reconnected");
        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(target.getUniqueId(), prophecyEffect), "Target does not have prophecy effect");

        // prophecies are fulfilled when prophets are offline
        mockServer.getScheduler().performTicks(effectDuration); // expire the current effect
        prophecy = new O2Prophecy(testPlugin, prophecyEffect, prophecyMessage, target.getUniqueId(), prophet.getUniqueId(), 10, 10, 99);
        prophet.disconnect();
        mockServer.getScheduler().performTicks(5);
        prophecy.fulfill();
        assertNotNull(target.nextMessage(), "Target did not receive prophecy message when they reconnected");
        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(target.getUniqueId(), prophecyEffect), "Target does not have prophecy effect");

        // prophecies are not fulfilled if prophecy already killed
        prophecyEffect = O2EffectType.BABBLING;
        prophecy = new O2Prophecy(testPlugin, prophecyEffect, prophecyMessage, target.getUniqueId(), prophet.getUniqueId(), 10, 10, 99);
        prophecy.kill();
        prophecy.fulfill();
        assertNull(target.nextMessage(), "target received prophecy message when prophecy was killed before fulfill()");
        assertNull(prophet.nextMessage(), "prophet received prophecy message when prophecy was killed before fulfill()");
        assertFalse(Ollivanders2API.getPlayers().playerEffects.hasEffect(target.getUniqueId(), prophecyEffect), "Target has the prophecy effect when prophecy was killed before fulfill()");
    }

    /**
     * Global test teardown. Unmocks the MockBukkit server.
     */
    @AfterAll
    static void globalTearDown () {
        MockBukkit.unmock();
        Ollivanders2.debug = false;
    }
}
