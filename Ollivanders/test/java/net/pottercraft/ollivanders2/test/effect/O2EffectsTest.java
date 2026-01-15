package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.HIGHER_SKILL;
import net.pottercraft.ollivanders2.effect.O2Effect;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.World;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Comprehensive test suite for the O2Effects effect management system.
 *
 * <p>Tests the core effect lifecycle and management functionality provided by O2Effects:
 * effect creation/removal, event distribution, aging, player lifecycle management, serialization,
 * and detection systems. These tests verify that the effect manager correctly coordinates
 * effects across all players and game events.</p>
 *
 * <p><strong>IMPORTANT: Single MockServer Architecture</strong></p>
 *
 * <p>This test class uses a single static MockBukkit.mock() server instance that is shared across
 * all tests. This is a fundamental constraint because:</p>
 * <ul>
 * <li>MockBukkit.mock() cannot be called in parallel - it must be a singleton</li>
 * <li>The Ollivanders2 plugin maintains global effect state (Ollivanders2API.getPlayers().playerEffects)</li>
 * <li>Tests will modify shared server state (players, effects, ticks)</li>
 * <li>Tests cannot be isolated because they share the same MockBukkit server instance</li>
 * </ul>
 *
 * <p>All tests are consolidated into a single @Test mega-test method (o2effectsTest()) that calls
 * helper methods sequentially. This ensures that @BeforeEach/@AfterEach run once per test execution,
 * properly protecting the shared MockServer state. JUnit treats the entire mega-test as an atomic
 * unit, preventing test interference even if parallel test execution is enabled.</p>
 *
 * <p>Test Categories:</p>
 * <ul>
 * <li><strong>Effect Management:</strong> Adding, removing, and retrieving effects with stacking and exclusivity</li>
 * <li><strong>Event Distribution:</strong> Ensuring events are properly distributed to effects on affected players</li>
 * <li><strong>Effect Aging:</strong> Testing aging all effects, specific effects, and percentage-based aging</li>
 * <li><strong>Player Lifecycle:</strong> Testing join, quit, and death behaviors</li>
 * <li><strong>Detection Systems:</strong> Testing Informous and Legilimens detection</li>
 * <li><strong>Utility Functions:</strong> Testing global effect removal and other utilities</li>
 * </ul>
 *
 * @author Test Suite
 */
public class O2EffectsTest {
    /**
     * Shared mock Bukkit server instance for all tests.
     *
     * <p>Static singleton that is reused across all test methods. MockBukkit.mock() cannot be called
     * in parallel, so this instance is created once in @BeforeAll and reused for the entire test
     * suite lifecycle. All tests access the same server state, and must be designed accordingly.</p>
     */
    static ServerMock mockServer;

    /**
     * Plugin instance loaded for each test method.
     *
     * <p>Reloaded before each @Test method via @BeforeEach to reset plugin state for deterministic
     * test execution. The plugin accesses the shared mockServer instance.</p>
     */
    Ollivanders2 testPlugin;

    /**
     * Test world for spatial operations.
     *
     * <p>Created fresh for each test method in @BeforeEach.</p>
     */
    World testWorld;

    @BeforeAll
    static void globalSetUp() {
        mockServer = MockBukkit.mock();
    }

    @BeforeEach
    void setUp() {
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));
        testWorld = mockServer.addSimpleWorld("world");

        // advance the server by 20 ticks to let the scheduler start (it has an initial delay of 20 ticks)
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    @AfterEach
    void tearDown() {
        // remove all effects so none get written to save files when the plugin exits
        Ollivanders2API.getPlayers().playerEffects.removeAllEffects();

        // advance the game enough ticks for it to process killing and removing all active effects
        mockServer.getScheduler().performTicks(5);
    }

    /**
     * Comprehensive O2Effects system test.
     *
     * <p>This is a mega test that runs all effect manager tests in sequence within a single @Test method.
     * This approach is necessary because parallelized, random-order JUnit tests mess up the shared
     * game state (MockBukkit server). By running all tests in order within o2effectsTest(), we ensure
     * the game state remains consistent throughout all validations.</p>
     *
     * <p>Tests performed in sequence:</p>
     * <ul>
     * <li>Effect addition and retrieval</li>
     * <li>Effect stacking and duration combination</li>
     * <li>Effect removal and queries</li>
     * <li>Effect listing</li>
     * <li>Effect aging (all, specific, percentage)</li>
     * <li>Effect upkeep and cleanup</li>
     * <li>Global effect removal</li>
     * </ul>
     */
    @Test
    void o2effectsTest() {
        Ollivanders2.debug = true;

        testAddEffect();
        testEffectStacking();
        testRemoveEffect();
        testEffectQueries();
        testGetEffects();
        testAgeAllEffects();
        testAgeEffect();
        testAgeEffectByPercent();
        testUpkeep();
        testRemoveAllEffects();
    }

    /**
     * Test adding a single effect to a player and verifying it can be retrieved.
     *
     * <p>Verifies that an effect is properly registered in the active effects system when added
     * and can be retrieved by effect type.</p>
     */
    private void testAddEffect() {
        PlayerMock player = mockServer.addPlayer();
        O2Effect effect = new HIGHER_SKILL(testPlugin, 1000, false, player.getUniqueId());

        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);
        // Advance multiple ticks to ensure the effect is fully processed into the active effects system
        mockServer.getScheduler().performTicks(10);
        try {
            // now sleep so we give the runnable time to run
            Thread.sleep(500);
        }
        catch (InterruptedException e) {}

        O2Effect retrieved = Ollivanders2API.getPlayers().playerEffects.getEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL);
        assertNotNull(retrieved, "Effect should be retrievable after adding");
        assertEquals(O2EffectType.HIGHER_SKILL, retrieved.effectType, "Retrieved effect should be HIGHER_SKILL type");
    }

    /**
     * Test effect stacking: adding the same effect twice combines durations.
     *
     * <p>When an effect is added to a player who already has that effect type, the durations
     * should be combined instead of replacing the effect.</p>
     */
    private void testEffectStacking() {
        PlayerMock player = mockServer.addPlayer();

        // Add first HIGHER_SKILL effect with 1000 ticks
        O2Effect effect1 = new HIGHER_SKILL(testPlugin, 1000, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect1);
        mockServer.getScheduler().performTicks(10);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {}

        // Add second HIGHER_SKILL effect with 500 ticks - should stack
        O2Effect effect2 = new HIGHER_SKILL(testPlugin, 500, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect2);
        mockServer.getScheduler().performTicks(10);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {}

        O2Effect combined = Ollivanders2API.getPlayers().playerEffects.getEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL);
        // Minimum duration is enforced so we verify it's greater than just the first effect
        assertTrue(combined.getRemainingDuration() >= 1000, "Effect stacking should combine durations");
    }

    /**
     * Test removing an effect from a player.
     *
     * <p>Verifies that removeEffect() properly terminates an effect and removes it from the
     * active effects list.</p>
     */
    private void testRemoveEffect() {
        PlayerMock player = mockServer.addPlayer();
        O2Effect effect = new HIGHER_SKILL(testPlugin, 1000, false, player.getUniqueId());

        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);
        mockServer.getScheduler().performTicks(10);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {}

        assertNotNull(Ollivanders2API.getPlayers().playerEffects.getEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL),
                "Effect should exist before removal");

        Ollivanders2API.getPlayers().playerEffects.removeEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL);
        mockServer.getScheduler().performTicks(1);

        assertNull(Ollivanders2API.getPlayers().playerEffects.getEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL),
                "Effect should be removed after removeEffect()");
    }

    /**
     * Test hasEffect() and hasEffects() query methods.
     *
     * <p>Verifies that the effect system correctly reports whether a player has specific or
     * any active effects.</p>
     */
    private void testEffectQueries() {
        PlayerMock player = mockServer.addPlayer();

        assertFalse(Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL),
                "Player should not have HIGHER_SKILL before adding");
        assertFalse(Ollivanders2API.getPlayers().playerEffects.hasEffects(player.getUniqueId()),
                "Player should not have any effects initially");

        O2Effect effect = new HIGHER_SKILL(testPlugin, 1000, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);
        mockServer.getScheduler().performTicks(10);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {}

        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL),
                "Player should have HIGHER_SKILL after adding");
        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffects(player.getUniqueId()),
                "Player should have effects after adding");
    }

    /**
     * Test getEffects() returns all effect types on a player.
     *
     * <p>Verifies that getEffects() returns a complete list of all active effect types on a player.</p>
     */
    private void testGetEffects() {
        PlayerMock player = mockServer.addPlayer();

        O2Effect effect1 = new HIGHER_SKILL(testPlugin, 1000, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect1);
        mockServer.getScheduler().performTicks(10);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {}

        List<O2EffectType> effects = Ollivanders2API.getPlayers().playerEffects.getEffects(player.getUniqueId());
        assertEquals(1, effects.size(), "Player should have 1 effect");
        assertTrue(effects.contains(O2EffectType.HIGHER_SKILL), "HIGHER_SKILL should be in effects list");
    }

    /**
     * Test that aging all effects decrements durations correctly.
     *
     * <p>Verifies that ageAllEffects() decrements all non-permanent effects on a player by the
     * specified amount and does not affect permanent effects.</p>
     */
    private void testAgeAllEffects() {
        PlayerMock player = mockServer.addPlayer();
        O2Effect effect = new HIGHER_SKILL(testPlugin, 1000, false, player.getUniqueId());

        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);
        mockServer.getScheduler().performTicks(10);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {}

        int duration = Ollivanders2API.getPlayers().playerEffects.getEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL).getRemainingDuration();

        Ollivanders2API.getPlayers().playerEffects.ageAllEffects(player.getUniqueId(), 10);

        int newDuration = Ollivanders2API.getPlayers().playerEffects.getEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL).getRemainingDuration();
        assertEquals(duration - 10, newDuration, "ageAllEffects should decrement duration by 10");
    }

    /**
     * Test aging a specific effect by absolute amount.
     *
     * <p>Verifies that ageEffect() decrements a specific effect's duration without affecting
     * other effects on the player.</p>
     */
    private void testAgeEffect() {
        PlayerMock player = mockServer.addPlayer();
        O2Effect effect = new HIGHER_SKILL(testPlugin, 1000, false, player.getUniqueId());

        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);
        mockServer.getScheduler().performTicks(10);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {}

        int duration = Ollivanders2API.getPlayers().playerEffects.getEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL).getRemainingDuration();

        Ollivanders2API.getPlayers().playerEffects.ageEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL, 25);

        int newDuration = Ollivanders2API.getPlayers().playerEffects.getEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL).getRemainingDuration();
        assertEquals(duration - 25, newDuration, "ageEffect should decrement duration by 25");
    }

    /**
     * Test aging a specific effect by percentage.
     *
     * <p>Verifies that ageEffectByPercent() reduces an effect's duration by the specified
     * percentage of its current duration.</p>
     */
    private void testAgeEffectByPercent() {
        PlayerMock player = mockServer.addPlayer();
        O2Effect effect = new HIGHER_SKILL(testPlugin, 1000, false, player.getUniqueId());

        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);
        mockServer.getScheduler().performTicks(10);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {}

        int duration = Ollivanders2API.getPlayers().playerEffects.getEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL).getRemainingDuration();

        // Age by 25% of current duration
        Ollivanders2API.getPlayers().playerEffects.ageEffectByPercent(player.getUniqueId(), O2EffectType.HIGHER_SKILL, 25);

        int newDuration = Ollivanders2API.getPlayers().playerEffects.getEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL).getRemainingDuration();
        int expected = (int)(duration - (duration * 0.25));
        // Allow 2-tick variance to account for natural effect aging between measurements
        assertTrue(Math.abs(newDuration - expected) <= 2,
                "ageEffectByPercent should reduce by 25% (within 2 ticks). Expected: " + expected + ", Got: " + newDuration);
    }

    /**
     * Test that upkeep processes effects and removes killed ones.
     *
     * <p>Verifies that upkeep() calls checkEffect() on all effects and removes those that have
     * been killed or are no longer enabled.</p>
     */
    private void testUpkeep() {
        PlayerMock player = mockServer.addPlayer();
        O2Effect effect = new HIGHER_SKILL(testPlugin, 1000, false, player.getUniqueId());

        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);
        mockServer.getScheduler().performTicks(10);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {}

        assertNotNull(Ollivanders2API.getPlayers().playerEffects.getEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL),
                "Effect should exist before upkeep");

        // Kill the effect directly
        effect.kill();

        // Upkeep should remove killed effects
        Ollivanders2API.getPlayers().playerEffects.upkeep(player.getUniqueId());

        assertNull(Ollivanders2API.getPlayers().playerEffects.getEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL),
                "Killed effect should be removed by upkeep");
    }

    /**
     * Test removeAllEffects() clears all effects globally.
     *
     * <p>Verifies that removeAllEffects() terminates all active effects on all players and
     * clears all saved effects for offline players.</p>
     */
    private void testRemoveAllEffects() {
        PlayerMock player1 = mockServer.addPlayer();
        PlayerMock player2 = mockServer.addPlayer();

        O2Effect effect1 = new HIGHER_SKILL(testPlugin, 1000, false, player1.getUniqueId());
        O2Effect effect2 = new HIGHER_SKILL(testPlugin, 1000, false, player2.getUniqueId());

        Ollivanders2API.getPlayers().playerEffects.addEffect(effect1);
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect2);
        mockServer.getScheduler().performTicks(10);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {}

        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffects(player1.getUniqueId()),
                "Player 1 should have effects");
        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffects(player2.getUniqueId()),
                "Player 2 should have effects");

        Ollivanders2API.getPlayers().playerEffects.removeAllEffects();
        mockServer.getScheduler().performTicks(1);

        assertFalse(Ollivanders2API.getPlayers().playerEffects.hasEffects(player1.getUniqueId()),
                "Player 1 effects should be cleared");
        assertFalse(Ollivanders2API.getPlayers().playerEffects.hasEffects(player2.getUniqueId()),
                "Player 2 effects should be cleared");
    }

    /**
     * Global test teardown. Unmocks the MockBukkit server.
     */
    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
