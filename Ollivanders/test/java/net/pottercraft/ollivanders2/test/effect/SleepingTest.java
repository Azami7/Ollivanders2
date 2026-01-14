package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.AWAKE;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.effect.SLEEPING;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.entity.Player;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the SLEEPING effect.
 *
 * <p>Tests the sleep induction effect that forces players into a debilitating sleep state. Verifies
 * core effect behaviors (aging, duration expiration) and specific SLEEPING mechanics:</p>
 * <ul>
 * <li>Applies secondary effects (SLEEP_SPEECH and IMMOBILIZE) to simulate sleep state</li>
 * <li>Tilts player's head downward (pitch = 45 degrees)</li>
 * <li>Sends player notification when falling asleep</li>
 * <li>Is immediately killed when AWAKE effect is present (effect override)</li>
 * <li>Properly cleans up secondary effects when removed</li>
 * </ul>
 *
 * <p>Special attention is given to the interaction between SLEEPING and AWAKE effects to ensure
 * they are mutually exclusive and AWAKE properly overrides SLEEPING.</p>
 */
public class SleepingTest extends EffectTestSuper {
    /**
     * Create a SLEEPING effect for testing.
     *
     * <p>Instantiates a new SLEEPING effect with the specified parameters. This method is called
     * by the test methods to create fresh effect instances for each test scenario.</p>
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration of the effect in game ticks
     * @param isPermanent     true if the effect should be permanent, false for limited duration
     * @return a new SLEEPING effect targeting the specified player
     */
    @Override
    SLEEPING createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new SLEEPING(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * Test SLEEPING effect behavior for applying sleep state and interacting with AWAKE.
     *
     * <p>Performs three comprehensive tests:</p>
     * <ol>
     * <li><strong>Test 1: SLEEPING applies secondary effects</strong> - Applies SLEEPING to a player,
     * advances ticks to initialize it, and verifies that SLEEP_SPEECH and IMMOBILIZE secondary effects
     * are applied, player's head is tilted (pitch = 45 degrees), and the sleep notification message is sent.</li>
     * <li><strong>Test 2: SLEEPING is prevented if AWAKE already present</strong> - Applies AWAKE to a player first,
     * then applies SLEEPING and advances ticks. Verifies that SLEEPING is immediately killed because
     * AWAKE is present.</li>
     * <li><strong>Test 3: SLEEPING is removed when AWAKE is applied after</strong> - Applies SLEEPING to a player,
     * advances ticks to initialize it, then applies AWAKE and advances ticks. Verifies that both effects
     * are active initially, then confirms SLEEPING is removed after AWAKE processes.</li>
     * </ol>
     */
    @Override
    void checkEffectTest() {
        // Test that SLEEPING applies secondary effects and modifies player state
        PlayerMock target = mockServer.addPlayer();
        SLEEPING sleeping = (SLEEPING) addEffect(target, 100, false);

        // Advance ticks so SLEEPS's checkEffect() runs and puts the player to sleep
        mockServer.getScheduler().performTicks(5);

        // Verify secondary effects were applied
        assertNotNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.SLEEP_SPEECH),
            "SLEEP_SPEECH effect was not added by SLEEPING");
        assertNotNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.IMMOBILIZE),
            "IMMOBILIZE effect was not added by SLEEPING");

        // Verify player is sleeping (pitch = 45)
        assertTrue(target.getLocation().getPitch() == 45,
                "Player's head was not tilted down (pitch should be 45, got " + target.getLocation().getPitch() + ")");

        // Verify player received sleep notification message
        String message = target.nextMessage();
        assertNotNull(message, "Sleep message was null");
        assertTrue(TestCommon.messageStartsWith(SLEEPING.SLEEP_MESSAGE, message),
            "Player did not receive sleep notification message");

        // Test that SLEEPING is canceled if player has AWAKE
        PlayerMock target2 = mockServer.addPlayer();

        // Apply AWAKE first
        AWAKE awake = new AWAKE(testPlugin, 100, false, target2.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(awake);
        mockServer.getScheduler().performTicks(5);

        // Apply SLEEPING - it should be immediately killed because AWAKE is present
        SLEEPING sleeping2 = (SLEEPING) addEffect(target2, 100, false);
        mockServer.getScheduler().performTicks(5);

        // Verify SLEEPING was killed by AWAKE
        assertTrue(sleeping2.isKilled(), "SLEEPING was not killed when AWAKE was present");

        // Test that SLEEPING is removed if AWAKE is applied after SLEEPING initializes
        PlayerMock target3 = mockServer.addPlayer();

        // Apply SLEEPING first
        SLEEPING sleeping3 = (SLEEPING) addEffect(target3, 100, false);
        // Advance ticks so SLEEPING initializes and puts player to sleep
        mockServer.getScheduler().performTicks(5);

        // Verify SLEEPING effect is active
        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(target3.getUniqueId(), O2EffectType.SLEEPING),
            "SLEEPING effect was not added to target");

        // Apply AWAKE effect
        AWAKE awake3 = new AWAKE(testPlugin, 100, false, target3.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(awake3);
        // Advance ticks so AWAKE's checkEffect() runs and removes SLEEPING
        mockServer.getScheduler().performTicks(5);

        // Verify AWAKE effect is active
        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(target3.getUniqueId(), O2EffectType.AWAKE),
            "AWAKE effect was not added to target");

        // Verify SLEEPING effect was removed by AWAKE
        assertNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target3.getUniqueId(), O2EffectType.SLEEPING),
            "SLEEPING effect was not removed when AWAKE was applied after");
    }

    /**
     * Event handler tests for the SLEEPING effect.
     *
     * <p>SLEEPING does not implement any event handlers because Spigot does not provide a
     * PlayerWakeEvent that would allow custom code to react to players waking. While a
     * PlayerLeaveBedEvent exists, it cannot be relied upon to detect sleep termination since
     * players might leave their bed for other reasons. Therefore, this test method is empty
     * as there are no event handlers to test. The AWAKE effect handles the wake-up interaction
     * via its own checkEffect() method instead.</p>
     */
    @Override
    void eventHandlerTests() {

    }

    /**
     * Verify that SLEEPING cleanup removes secondary effects.
     *
     * <p>Tests that when the SLEEPING effect is removed via doRemove(), the secondary effects
     * (SLEEP_SPEECH and IMMOBILIZE) that were applied during sleep initialization are properly
     * cleaned up. This ensures the player is fully restored when the sleep effect ends.</p>
     */
    @Override
    void doRemoveTest() {
        PlayerMock target = mockServer.addPlayer();

        // Apply SLEEPING effect (addEffect() already performs 1 tick)
        SLEEPING sleeping = (SLEEPING) addEffect(target, 100, false);

        // Call doRemove() to clean up
        sleeping.doRemove();

        // Verify secondary effects were removed
        assertNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.SLEEP_SPEECH),
            "SLEEP_SPEECH effect was not removed when SLEEPING was removed");
        assertNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.IMMOBILIZE),
            "IMMOBILIZE effect was not removed when SLEEPING was removed");
    }
}
