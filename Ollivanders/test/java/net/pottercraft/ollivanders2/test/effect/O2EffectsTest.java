package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.HIGHER_SKILL;
import net.pottercraft.ollivanders2.effect.INVISIBILITY;
import net.pottercraft.ollivanders2.effect.O2Effect;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the O2Effects effect-management system: effect add/remove/query, stacking, aging, upkeep, and
 * cross-player event distribution.
 * <p>
 * All cases run inside one @Test mega-method against a single shared MockBukkit server. MockBukkit.mock() cannot run in
 * parallel, and the plugin holds global effect state, so the shared server must not be split across parallel tests.
 * </p>
 *
 * @author Test Suite
 */
public class O2EffectsTest {
    /**
     * Shared MockBukkit server, mocked once as MockBukkit.mock() cannot run in parallel.
     */
    static ServerMock mockServer;

    /**
     * The plugin instance, reloaded before each test method for isolation.
     */
    Ollivanders2 testPlugin;

    /**
     * The test world effects are applied in.
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
     * Runs all effect-manager cases in sequence; single method because they share mutable MockBukkit server state and
     * JUnit does not guarantee method order.
     */
    @Test
    void o2effectsTest() {
        testAddEffect();
        testEffectStacking();
        testRemoveEffect();
        testEffectQueries();
        testGetEffects();
        testAgeAllEffects();
        testAgeEffect();
        testAgeEffectByPercent();
        testUpkeep();
        testGetAllActiveEffects();
        testRemoveAllEffects();
        testOnPlayerQuitSavesAndOnJoinRestores();
        testKilledEffectNotSavedOnQuit();
        testOnEntityDeathClearsEffects();
    }

    /**
     * An added effect is registered and retrievable by type.
     */
    private void testAddEffect() {
        PlayerMock player = mockServer.addPlayer();
        O2Effect effect = new HIGHER_SKILL(testPlugin, 1000, false, player.getUniqueId());

        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);
        // Advance multiple ticks to ensure the effect is fully processed into the active effects system
        mockServer.getScheduler().performTicks(20);

        O2Effect retrieved = Ollivanders2API.getPlayers().playerEffects.getEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL);
        assertNotNull(retrieved, "Effect should be retrievable after adding");
        assertEquals(O2EffectType.HIGHER_SKILL, retrieved.effectType, "Retrieved effect should be HIGHER_SKILL type");
    }

    /**
     * Adding an effect a player already has combines durations rather than replacing.
     */
    private void testEffectStacking() {
        PlayerMock player = mockServer.addPlayer();

        // Add first HIGHER_SKILL effect with 1000 ticks
        O2Effect effect1 = new HIGHER_SKILL(testPlugin, 1000, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect1);
        mockServer.getScheduler().performTicks(20);

        // Add second HIGHER_SKILL effect with 500 ticks - should stack
        O2Effect effect2 = new HIGHER_SKILL(testPlugin, 500, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect2);
        mockServer.getScheduler().performTicks(20);

        O2Effect combined = Ollivanders2API.getPlayers().playerEffects.getEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL);
        // Minimum duration is enforced so we verify it's greater than just the first effect
        assertTrue(combined.getRemainingDuration() >= 1000, "Effect stacking should combine durations");
    }

    /**
     * removeEffect() terminates an effect and removes it from the active list.
     */
    private void testRemoveEffect() {
        PlayerMock player = mockServer.addPlayer();
        O2Effect effect = new HIGHER_SKILL(testPlugin, 1000, false, player.getUniqueId());

        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);
        mockServer.getScheduler().performTicks(20);

        assertNotNull(Ollivanders2API.getPlayers().playerEffects.getEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL),
                "Effect should exist before removal");

        Ollivanders2API.getPlayers().playerEffects.removeEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL);
        mockServer.getScheduler().performTicks(1);

        assertNull(Ollivanders2API.getPlayers().playerEffects.getEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL),
                "Effect should be removed after removeEffect()");
    }

    /**
     * hasEffect() and hasEffects() report specific and any active effects correctly.
     */
    private void testEffectQueries() {
        PlayerMock player = mockServer.addPlayer();

        assertFalse(Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL),
                "Player should not have HIGHER_SKILL before adding");
        assertFalse(Ollivanders2API.getPlayers().playerEffects.hasEffects(player.getUniqueId()),
                "Player should not have any effects initially");

        O2Effect effect = new HIGHER_SKILL(testPlugin, 1000, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);
        mockServer.getScheduler().performTicks(20);

        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL),
                "Player should have HIGHER_SKILL after adding");
        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffects(player.getUniqueId()),
                "Player should have effects after adding");
    }

    /**
     * getEffects() returns all active effect types on a player.
     */
    private void testGetEffects() {
        PlayerMock player = mockServer.addPlayer();

        O2Effect effect1 = new HIGHER_SKILL(testPlugin, 1000, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect1);
        mockServer.getScheduler().performTicks(20);

        List<O2EffectType> effects = Ollivanders2API.getPlayers().playerEffects.getEffects(player.getUniqueId());
        assertEquals(1, effects.size(), "Player should have 1 effect");
        assertTrue(effects.contains(O2EffectType.HIGHER_SKILL), "HIGHER_SKILL should be in effects list");
    }

    /**
     * ageAllEffects() decrements every non-permanent effect on a player by the given amount.
     */
    private void testAgeAllEffects() {
        PlayerMock player = mockServer.addPlayer();
        O2Effect effect = new HIGHER_SKILL(testPlugin, 1000, false, player.getUniqueId());

        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);
        mockServer.getScheduler().performTicks(20);

        int duration = Ollivanders2API.getPlayers().playerEffects.getEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL).getRemainingDuration();

        Ollivanders2API.getPlayers().playerEffects.ageAllEffects(player.getUniqueId(), 10);

        int newDuration = Ollivanders2API.getPlayers().playerEffects.getEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL).getRemainingDuration();
        assertEquals(duration - 10, newDuration, "ageAllEffects should decrement duration by 10");
    }

    /**
     * ageEffect() decrements a single effect's duration by an absolute amount.
     */
    private void testAgeEffect() {
        PlayerMock player = mockServer.addPlayer();
        O2Effect effect = new HIGHER_SKILL(testPlugin, 1000, false, player.getUniqueId());

        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);
        mockServer.getScheduler().performTicks(20);

        int duration = Ollivanders2API.getPlayers().playerEffects.getEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL).getRemainingDuration();

        Ollivanders2API.getPlayers().playerEffects.ageEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL, 25);

        int newDuration = Ollivanders2API.getPlayers().playerEffects.getEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL).getRemainingDuration();
        assertEquals(duration - 25, newDuration, "ageEffect should decrement duration by 25");
    }

    /**
     * ageEffectByPercent() reduces an effect's duration by a percentage of its current value.
     */
    private void testAgeEffectByPercent() {
        PlayerMock player = mockServer.addPlayer();
        O2Effect effect = new HIGHER_SKILL(testPlugin, 1000, false, player.getUniqueId());

        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);
        mockServer.getScheduler().performTicks(20);

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
     * upkeep() removes killed effects.
     */
    private void testUpkeep() {
        PlayerMock player = mockServer.addPlayer();
        O2Effect effect = new HIGHER_SKILL(testPlugin, 1000, false, player.getUniqueId());

        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);
        mockServer.getScheduler().performTicks(20);

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
     * getAllActiveEffects() spans all players: a joining player cannot see two other players who each hold INVISIBILITY,
     * since onPlayerJoinEvent distributes the join to every active effect.
     */
    private void testGetAllActiveEffects() {
        PlayerMock player1 = mockServer.addPlayer();
        PlayerMock player2 = mockServer.addPlayer();

        // add INVISIBILITY to both players
        O2Effect invis1 = new INVISIBILITY(testPlugin, 1000, false, player1.getUniqueId());
        O2Effect invis2 = new INVISIBILITY(testPlugin, 1000, false, player2.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(invis1);
        Ollivanders2API.getPlayers().playerEffects.addEffect(invis2);

        // advance ticks so checkEffect() runs and sets invisible = true
        mockServer.getScheduler().performTicks(20);

        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(player1.getUniqueId(), O2EffectType.INVISIBILITY),
                "Player 1 should have INVISIBILITY");
        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(player2.getUniqueId(), O2EffectType.INVISIBILITY),
                "Player 2 should have INVISIBILITY");

        // new player joins - onPlayerJoinEvent iterates getAllActiveEffects()
        // and each INVISIBILITY effect should hide its target from the joining player
        PlayerMock joiner = mockServer.addPlayer();
        mockServer.getScheduler().performTicks(5);

        assertFalse(joiner.canSee(player1),
                "Joining player should not see invisible player 1 (getAllActiveEffects should include player 1's effect)");
        assertFalse(joiner.canSee(player2),
                "Joining player should not see invisible player 2 (getAllActiveEffects should include player 2's effect)");
    }

    /**
     * removeAllEffects() clears active effects on every player.
     */
    private void testRemoveAllEffects() {
        PlayerMock player1 = mockServer.addPlayer();
        PlayerMock player2 = mockServer.addPlayer();

        O2Effect effect1 = new HIGHER_SKILL(testPlugin, 1000, false, player1.getUniqueId());
        O2Effect effect2 = new HIGHER_SKILL(testPlugin, 1000, false, player2.getUniqueId());

        Ollivanders2API.getPlayers().playerEffects.addEffect(effect1);
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect2);
        mockServer.getScheduler().performTicks(20);

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
     * onPlayerQuitEvent moves a player's active effects into saved storage; onPlayerJoinEvent restores them on rejoin.
     */
    private void testOnPlayerQuitSavesAndOnJoinRestores() {
        PlayerMock player = mockServer.addPlayer();
        Ollivanders2API.getPlayers().playerEffects.addEffect(new HIGHER_SKILL(testPlugin, 1000, false, player.getUniqueId()));
        mockServer.getScheduler().performTicks(20);
        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL),
                "effect should be active before quit");

        // quitting moves the active effect to saved storage and clears it from the active list
        player.disconnect();
        mockServer.getScheduler().performTicks(1);
        assertFalse(Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL),
                "quit should move the effect out of the active list");

        // rejoining restores the saved effect to active
        player.reconnect();
        mockServer.getScheduler().performTicks(5);
        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL),
                "join should restore the saved effect to active");
    }

    /**
     * An effect killed before quit (as a shield kills itself in its quit handler) is not saved, so it does not return
     * when the player rejoins.
     */
    private void testKilledEffectNotSavedOnQuit() {
        PlayerMock player = mockServer.addPlayer();
        Ollivanders2API.getPlayers().playerEffects.addEffect(new HIGHER_SKILL(testPlugin, 1000, false, player.getUniqueId()));
        mockServer.getScheduler().performTicks(20);

        O2Effect effect = Ollivanders2API.getPlayers().playerEffects.getEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL);
        assertNotNull(effect, "effect should be active before it is killed");

        // kill the effect but leave it in the active list, then quit before any tick removes it
        effect.kill();
        player.disconnect();
        mockServer.getScheduler().performTicks(1);
        player.reconnect();
        mockServer.getScheduler().performTicks(5);

        assertFalse(Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL),
                "a killed effect should not be saved on quit, so it does not return on join");
    }

    /**
     * onEntityDeathEvent resets a player's active effects so they respawn clean.
     */
    private void testOnEntityDeathClearsEffects() {
        PlayerMock player = mockServer.addPlayer();
        player.setLocation(new Location(testWorld, 0, 4, 0));
        Ollivanders2API.getPlayers().playerEffects.addEffect(new HIGHER_SKILL(testPlugin, 1000, false, player.getUniqueId()));
        mockServer.getScheduler().performTicks(20);
        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL),
                "effect should be active before death");

        DamageSource damageSource = DamageSource.builder(DamageType.IN_FIRE).withDamageLocation(player.getLocation()).build();
        PlayerDeathEvent event = new PlayerDeathEvent(player, damageSource, new ArrayList<>(), 0, 0, 0, 0, "died");
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(1);

        assertFalse(Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL),
                "death should reset the player's active effects");
    }

    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
