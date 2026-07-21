package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.ANIMAGUS_INCANTATION;
import net.pottercraft.ollivanders2.effect.O2Effect;
import net.pottercraft.ollivanders2.effect.O2EffectAntidote;
import org.bukkit.entity.Player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test base for antidote effects: a full-strength antidote (>= 1.0) kills its target effect, a partial one reduces
 * the target's remaining duration by its strength, and either way unrelated effects are untouched.
 *
 * @author Azami7
 * @see PermanentEffectTestSuper
 */
abstract public class O2EffectAntidoteTest extends PermanentEffectTestSuper {
    /**
     * A full-strength antidote kills its target effect and a partial one reduces its duration proportionally; the
     * antidote kills itself after use and leaves unrelated effects untouched.
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
        O2EffectAntidote antidote = createEffect(target, 5, false);
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
    abstract O2EffectAntidote createEffect(Player target, int durationInTicks, boolean isPermanent);

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
     * Antidotes handle no events.
     */
    void eventHandlerTests() {}

    /**
     * Antidotes have no doRemove() cleanup.
     */
    void doRemoveTest() {}
}
