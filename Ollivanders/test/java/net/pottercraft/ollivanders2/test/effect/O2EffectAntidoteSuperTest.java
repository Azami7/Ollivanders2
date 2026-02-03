package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.ANIMAGUS_INCANTATION;
import net.pottercraft.ollivanders2.effect.O2Effect;
import net.pottercraft.ollivanders2.effect.O2EffectAntidoteSuper;
import org.bukkit.entity.Player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Abstract test base class for antidote effects.
 *
 * <p>Provides comprehensive testing for antidote effects that counteract other magical effects.
 * Tests verify both full-strength antidotes (that completely remove target effects) and
 * partial-strength antidotes (that reduce target effect duration by a percentage). Also verifies
 * that antidotes only affect their specified target effect type and do not harm unrelated effects.</p>
 */
abstract public class O2EffectAntidoteSuperTest extends PermanentEffectTestSuper {
    /**
     * Test the core antidote effect mechanism including strength-based reduction and effect targeting.
     *
     * <p>This test validates two critical antidote behaviors:</p>
     * <ol>
     * <li><strong>Strength-Based Reduction:</strong>
     * <ul>
     * <li>Full-strength antidotes (strength ≥ 1.0) completely kill target effects</li>
     * <li>Partial-strength antidotes (strength < 1.0) reduce target effect duration by the correct percentage</li>
     * <li>The test accounts for natural duration aging during test execution</li>
     * </ul></li>
     * <li><strong>Effect Targeting:</strong>
     * <ul>
     * <li>Antidotes only affect their specified target effect type</li>
     * <li>Unrelated effects are left completely untouched</li>
     * </ul></li>
     * </ol>
     *
     * <p>Test Flow:</p>
     * <ol>
     * <li>Create target effect with 100-tick duration</li>
     * <li>Apply antidote and verify strength-based reduction is correct</li>
     * <li>Verify antidote is killed after use (instant effect)</li>
     * <li>Create unrelated effect and apply antidote</li>
     * <li>Verify unrelated effect is not affected by the antidote</li>
     * </ol>
     */
    @Override
    void checkEffectTest() {
        Player target = mockServer.addPlayer();

        // add the specific effect to the target and event manager
        O2Effect effect = addEffectToTarget(target, 100);
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);
        int duration = effect.getMinDuration();

        // advance the server 1 tick
        mockServer.getScheduler().performTicks(1);

        // create the antidote and add it to the event manager
        O2EffectAntidoteSuper antidote = createEffect(target, 5, false);
        Ollivanders2API.getPlayers().playerEffects.addEffect(antidote);

        // advance the server 1 tick
        mockServer.getScheduler().performTicks(1);

        // the antidote should reduce the effect based on its strength
        double strength = antidote.getStrength();
        if (strength < 1.0) {
            // subtract 2 ticks from duration because we performed 2 ticks above
            duration = duration - 2;

            double reduction = ((duration) * strength);
            assertEquals((duration) - (int) reduction, effect.getRemainingDuration(), "Antidote of strength " + strength + " did not reduce " + effect.effectType + " as expected.");
        }
        else
            assertTrue(effect.isKilled(), "Antidote of strength " + strength + " did not kill " + effect.effectType);

        // antidote is killed
        assertTrue(antidote.isKilled(), "Antidote not killed after use.");

        // advance the server 1 tick
        mockServer.getScheduler().performTicks(1);

        // add an effect not affected by this antidote type
        effect = addUnrelatedEffectToTarget(target, duration);
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

        // add the antidote
        antidote = createEffect(target, 5, false);
        Ollivanders2API.getPlayers().playerEffects.addEffect(antidote);

        // advance the server 1 tick
        mockServer.getScheduler().performTicks(1);

        // effect is not killed, antidote is killed
        assertFalse(effect.isKilled(), effect.effectType.toString() + " was unexpectedly killed by " + antidote.effectType);
        assertTrue(antidote.isKilled(), "Antidote not killed after use.");
    }

    /**
     * Create an antidote effect with the specified duration and permanence.
     *
     * @param target the player to add the antidote to
     * @param durationInTicks the duration of the antidote in ticks
     * @param isPermanent     whether the antidote is permanent
     * @return the created antidote effect
     */
    abstract O2EffectAntidoteSuper createEffect(Player target, int durationInTicks, boolean isPermanent);

    /**
     * Add the effect this antidote counters to the target.
     *
     * @param target the player to add the effect to
     * @param duration the duration of the target effect in ticks
     * @return the created target effect
     */
    abstract O2Effect addEffectToTarget(Player target, int duration);

    /**
     * Add an effect this antidote does not counter to the target.
     *
     * @param target the player to add this effect to
     * @param duration the duration of the unrelated effect in ticks
     * @return the created unrelated effect
     */
    O2Effect addUnrelatedEffectToTarget(Player target, int duration) {
        // add animagus incantation since it has no antidote
        return new ANIMAGUS_INCANTATION(testPlugin, duration, false, target.getUniqueId());
    }

    /**
     * Override of event handler tests.
     *
     * <p>Antidotes do not have event handlers, so this test is intentionally empty.</p>
     */
    void eventHandlerTests() {}

    /**
     * Override of cleanup tests.
     *
     * <p>Antidotes do not have cleanup functions, so this test is intentionally empty.</p>
     */
    void doRemoveTest() {}
}
