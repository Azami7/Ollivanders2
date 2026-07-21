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
 * Unit tests for {@link SLEEPING}.
 *
 * @see EffectTestSuper
 */
public class SleepingTest extends EffectTestSuper {
    @Override
    SLEEPING createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new SLEEPING(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * SLEEPING applies its SLEEP_SPEECH and FULL_IMMOBILIZE secondary effects, tilts the head, and notifies the
     * player; it is killed when AWAKE is already present and removed when AWAKE is applied afterward.
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
        assertNotNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.FULL_IMMOBILIZE),
            "FULL_IMMOBILIZE effect was not added by SLEEPING");

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
     * SLEEPING has no event handlers; the wake-up interaction lives in AWAKE's own checkEffect().
     */
    @Override
    void eventHandlerTests() {

    }

    /**
     * doRemove() removes the SLEEP_SPEECH and FULL_IMMOBILIZE secondary effects.
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
        assertNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.FULL_IMMOBILIZE),
            "FULL_IMMOBILIZE effect was not removed when SLEEPING was removed");
    }
}
