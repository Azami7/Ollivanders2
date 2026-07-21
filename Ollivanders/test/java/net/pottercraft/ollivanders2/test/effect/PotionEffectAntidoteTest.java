package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.ANIMAGUS_INCANTATION;
import net.pottercraft.ollivanders2.effect.O2Effect;
import net.pottercraft.ollivanders2.effect.PotionEffectAntidote;
import net.pottercraft.ollivanders2.effect.PotionEffect;
import org.bukkit.entity.Player;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test base for {@link PotionEffectAntidote} effects: verifies full-strength antidotes remove their target potion
 * effect, partial-strength antidotes reduce its duration proportionally, and unrelated effects are left untouched.
 */
abstract public class PotionEffectAntidoteTest extends NotPermanentEffectTestSuper {
    /**
     * A partial-strength antidote reduces its target potion effect's duration to {@code duration * strength}, a
     * full-strength one removes it entirely, the antidote kills itself after use, and an unrelated effect is untouched.
     */
    @Override
    void checkEffectTest() {
        Player target = mockServer.addPlayer();

        // add the specific potion effect to the target and event manager
        O2Effect effect = addEffectToTarget(target, 100);
        PotionEffect potionEffect = (PotionEffect) effect;
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

        // advance the server 1 tick to apply the potion effect
        mockServer.getScheduler().performTicks(1);

        // get the applied potion effect from the player
        org.bukkit.potion.PotionEffect appliedEffect = target.getPotionEffect(potionEffect.getPotionEffectType());
        assertNotNull(appliedEffect, "Potion effect was not applied to player");
        int originalDuration = appliedEffect.getDuration();

        // create the antidote and add it to the event manager
        PotionEffectAntidote antidote = createEffect(target, 5, false);
        Ollivanders2API.getPlayers().playerEffects.addEffect(antidote);

        // advance the server 1 tick to apply the antidote
        mockServer.getScheduler().performTicks(1);

        // the antidote should reduce the potion effect based on its strength
        double strength = antidote.getStrength();
        if (strength < 1.0) {
            // For potion effect antidotes, the new duration is originalDuration * strength
            int expectedDuration = (int) (originalDuration * strength);
            org.bukkit.potion.PotionEffect reducedEffect = target.getPotionEffect(potionEffect.getPotionEffectType());
            assertNotNull(reducedEffect, "Potion effect was removed by antidote");
            // Account for floating point rounding by allowing 1 tick difference
            assertTrue(Math.abs(expectedDuration - reducedEffect.getDuration()) <= 1,
                "Antidote of strength " + strength + " did not reduce potion effect duration as expected. Expected: " + expectedDuration + ", Got: " + reducedEffect.getDuration());
        }
        else {
            // Full-strength antidote should remove the potion effect completely
            org.bukkit.potion.PotionEffect removedEffect = target.getPotionEffect(potionEffect.getPotionEffectType());
            assertTrue(removedEffect == null || removedEffect.getDuration() == 0, "Antidote of strength " + strength + " did not remove potion effect");
        }

        // antidote is killed
        assertTrue(antidote.isKilled(), "Antidote not killed after use.");

        // advance the server 1 tick
        mockServer.getScheduler().performTicks(1);

        // add an effect not affected by this antidote type
        effect = addUnrelatedEffectToTarget(target, originalDuration);
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
    abstract PotionEffectAntidote createEffect(Player target, int durationInTicks, boolean isPermanent);

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

    void eventHandlerTests() {}

    void doRemoveTest() {}
}