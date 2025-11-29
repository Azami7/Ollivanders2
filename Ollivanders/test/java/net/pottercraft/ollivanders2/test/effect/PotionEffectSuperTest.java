package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.O2Effect;
import net.pottercraft.ollivanders2.effect.PotionEffectSuper;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Abstract test base class for testing PotionEffectSuper implementations.
 *
 * <p>PotionEffectSuperTest provides a comprehensive testing framework for all PotionEffectSuper subclasses,
 * handling the test infrastructure and validating that potion effects are properly applied to target players
 * with correct parameters. This class extends {@link NotPermanentEffectTestSuper} to ensure that potion
 * effects correctly enforce their non-permanent constraint.</p>
 *
 * <p>Core Behaviors Tested:</p>
 * <ul>
 * <li><strong>Potion Effect Application:</strong> Verifies that the correct Minecraft potion effect type
 * is applied to the target player when the effect is processed</li>
 * <li><strong>Effect Parameters:</strong> Validates that the applied potion effect has the correct
 * duration and amplifier (strength) as specified in the O2Effect</li>
 * <li><strong>Duration Bounds:</strong> Tests that effect duration is clamped to the valid range
 * (minimum 2400 ticks / 2 minutes, maximum 6000 ticks / 5 minutes)</li>
 * <li><strong>Non-Permanent Enforcement:</strong> Ensures potion effects cannot be marked as permanent,
 * inherited from {@link NotPermanentEffectTestSuper}</li>
 * </ul>
 *
 * <p>Concrete test implementations should extend this class and implement {@link #createEffect(Player, int, boolean)}
 * to instantiate the specific PotionEffectSuper subclass being tested. All other test methods are inherited
 * and will validate the effect's behavior automatically.</p>
 *
 * @author Azami7
 * @see PotionEffectSuper for the abstract class being tested
 * @see NotPermanentEffectTestSuper for non-permanent effect testing infrastructure
 */
abstract public class PotionEffectSuperTest extends NotPermanentEffectTestSuper {
    /**
     * Create a PotionEffectSuper instance for testing.
     *
     * <p>Abstract method that must be implemented by concrete test subclasses to instantiate the
     * specific PotionEffectSuper subclass being tested. This allows each test class to test a
     * different potion effect type while reusing the common testing framework.</p>
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration of the effect in game ticks
     * @param isPermanent     ignored - potion effects cannot be permanent
     * @return the newly created PotionEffectSuper instance of the type being tested
     */
    abstract PotionEffectSuper createEffect(Player target, int durationInTicks, boolean isPermanent);

    /**
     * Test that the potion effect is properly applied to the target player with correct parameters.
     *
     * <p>This test verifies the core functionality of PotionEffectSuper by validating that:
     * <ol>
     * <li>The effect creates and applies the correct Minecraft potion effect type</li>
     * <li>The applied potion effect has the correct duration matching the O2Effect's duration</li>
     * <li>The applied potion effect has the correct amplifier matching the O2Effect's strength</li>
     * </ol>
     * </p>
     *
     * <p>The test workflow:
     * <ol>
     * <li>Creates a potion effect with minimum allowed duration</li>
     * <li>Adds it to the effect manager</li>
     * <li>Records the strength value before processing</li>
     * <li>Advances the scheduler by 1 tick to trigger effect processing</li>
     * <li>Retrieves the applied Minecraft potion effect from the target player</li>
     * <li>Asserts that the effect type, duration, and amplifier are correct</li>
     * </ol>
     * </p>
     */
    void checkEffectTest() {
        PlayerMock target = mockServer.addPlayer();

        // create the potion effect and add it to the effect manager
        PotionEffectSuper potionEffect = createEffect(target, 100, false);
        Ollivanders2API.getPlayers().playerEffects.addEffect(potionEffect);
        int strength = potionEffect.getStrength();

        // age the effect 1 tick so it is processed and potion effect added
        mockServer.getScheduler().performTicks(1);

        // verify the effect adds the correct PotionEffectType, correct strength, and for the correct duration
        PotionEffect appliedEffect = target.getPotionEffect(potionEffect.getPotionEffectType());
        assertNotNull(appliedEffect, potionEffect.getPotionEffectType().toString() + " did not apply " + potionEffect.getPotionEffectType());

        assertEquals(potionEffect.getMinDuration(), appliedEffect.getDuration(), "Potion effect did not have expected duration");
        assertEquals(strength, appliedEffect.getAmplifier(), "Potion effect amplifier was not expected value");
    }

    /**
     * Potion effects do not handle events.
     *
     * <p>PotionEffectSuper implementations do not process custom events and therefore have no
     * event handler behavior to test. This method is intentionally empty as no event handling
     * is expected from potion effect subclasses.</p>
     */
    void eventHandlerTests() {}

    /**
     * Potion effects do not have custom cleanup logic.
     *
     * <p>PotionEffectSuper implementations rely on the parent class cleanup mechanism and do not
     * perform special cleanup when removed. This method is intentionally empty as no custom cleanup
     * is expected from potion effect subclasses.</p>
     */
    void doRemoveTest() {}
}
