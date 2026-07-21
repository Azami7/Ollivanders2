package net.pottercraft.ollivanders2.test.divination;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.divination.O2Prophecies;
import net.pottercraft.ollivanders2.divination.O2Prophecy;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
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
 * Unit tests for {@link O2Prophecy}, covering aging, killing, and fulfillment across accuracy levels and offline
 * target/prophet handling.
 * <p>
 * Written as a single sequential test method ({@link #prophecyTest()}) rather than many, because the scenarios share
 * mutable server state and JUnit does not guarantee method execution order.
 * </p>
 *
 * @see O2Prophecy
 * @see O2Prophecies
 */
public class O2ProphecyTest {
    static ServerMock mockServer;

    static int maxAccuracy;

    static Ollivanders2 testPlugin;

    /**
     * Mock prophet, who makes the prophecies and receives their success/failure messages.
     */
    static PlayerMock prophet;

    /**
     * Mock target, whom the prophecies are about and who receives the effect on fulfillment.
     */
    static PlayerMock target;

    @BeforeAll
    static void globalSetUp() {
        mockServer = MockBukkit.mock();
        maxAccuracy = O2Prophecy.maxAccuracy;

        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));
        prophet = mockServer.addPlayer("prophet");
        target = mockServer.addPlayer("target");

        // advance past the scheduler's startup delay
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    /**
     * Exercise the full prophecy lifecycle in order: aging decrements time; kill/isKilled track state; a high-accuracy
     * fulfillment messages both players and applies a time-limited effect to the target only; a low-accuracy fulfillment
     * only privately notifies the prophet; an offline target defers fulfillment until reconnect; an offline prophet
     * still lets the target be affected; and a killed prophecy does nothing on fulfill.
     */
    @Test
    void prophecyTest() {
        Ollivanders2.debug = true;
        String prophecyMessage = "test prophecy message";
        O2EffectType prophecyEffect = O2EffectType.FAST_LEARNING;
        int effectDuration = prophecyEffect.getMinDuration();

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
        Ollivanders2.maxSpellLevel = true;
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
        Ollivanders2.maxSpellLevel = false;
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

    @AfterAll
    static void globalTearDown () {
        MockBukkit.unmock();
        Ollivanders2.debug = false; // prophecyTest() enables debug
    }
}
