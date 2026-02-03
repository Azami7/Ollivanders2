package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.HARM;
import net.pottercraft.ollivanders2.effect.HARM_ANTIDOTE_LESSER;
import net.pottercraft.ollivanders2.effect.O2Effect;
import org.bukkit.entity.Player;

/**
 * Test suite for the HARM_ANTIDOTE_LESSER effect.
 *
 * <p>Tests the antidote effect that counteracts the HARM instant damage effect.
 * Verifies that the antidote correctly reduces or removes HARM effects
 * based on its partial strength, and does not affect unrelated effects.</p>
 */
public class HarmAntidoteLesserTest extends PotionEffectAntidoteSuperTest {
    /**
     * Create a HARM_ANTIDOTE_LESSER effect for testing.
     *
     * <p>Instantiates a new HARM_ANTIDOTE_LESSER antidote effect with the specified parameters.</p>
     *
     * @param target          the player to add the antidote to
     * @param durationInTicks the duration of the antidote in ticks
     * @param isPermanent     whether the antidote is permanent
     * @return a new HARM_ANTIDOTE_LESSER effect targeting the specified player
     */
    @Override
    HARM_ANTIDOTE_LESSER createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new HARM_ANTIDOTE_LESSER(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * Add the effect that this antidote counters to the target player.
     *
     * <p>Creates and returns a HARM effect that the HARM_ANTIDOTE_LESSER antidote
     * is designed to counteract.</p>
     *
     * @param target   the player to add the effect to
     * @param duration the duration of the target effect in ticks
     * @return the created HARM effect
     */
    @Override
    O2Effect addEffectToTarget(Player target, int duration) {
        return new HARM(testPlugin, duration, false, target.getUniqueId());
    }
}