package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Passive marker effect that boosts skill experience gain for the affected player.
 *
 * <p>The FAST_LEARNING effect is a temporary marker effect that increases the rate at which the player
 * gains experience when using skills (book learning, spell practice, etc.). This effect does not apply
 * any active modifications itself; instead, the skill system checks for this effect's presence and applies
 * an experience multiplier when the player gains skill experience. The effect is passive and only needs to
 * be tracked until expiration.</p>
 *
 * <p>Mechanism:</p>
 * <ul>
 * <li>Passive marker effect - no active behavior during each tick</li>
 * <li>Skill system applies experience multiplier when this effect is present</li>
 * <li>Detectable by mind-reading spells (Legilimens)</li>
 * <li>Detection text: "feels more mentally alert than usual"</li>
 * <li>Effect expires naturally when duration reaches zero</li>
 * </ul>
 *
 * @author Azami7
 */
public class FAST_LEARNING extends O2Effect {
    /**
     * Constructor for creating a fast learning skill experience boost effect.
     *
     * <p>Creates a temporary marker effect that boosts skill experience gain. This effect is passive and
     * does not apply modifications directly; the skill system checks for its presence to apply an experience
     * multiplier. Sets the detection text for mind-reading spells to "feels more mentally alert than usual".</p>
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the effect in game ticks
     * @param isPermanent true if this effect is permanent, false if it is not
     * @param pid         the unique ID of the player to grant fast learning
     */
    public FAST_LEARNING(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.FAST_LEARNING;
        checkDurationBounds();

        legilimensText = "feels more mentally alert than usual";
    }

    /**
     * Age the fast learning marker effect.
     *
     * <p>Called each game tick. This effect is passive and only needs to track its remaining duration.
     * The actual experience multiplier is applied by the skill system when it detects this effect's presence
     * on a player gaining skill experience. When the duration reaches zero, the effect is automatically killed
     * and removed from the player.</p>
     */
    @Override
    public void checkEffect() {
        age(1);
    }

    /**
     * Perform cleanup when the fast learning effect is removed.
     *
     * <p>The default implementation does nothing, as the fast learning effect is a passive marker with
     * no state to clean up. When removed, the player simply stops receiving the experience multiplier
     * boost from the skill system.</p>
     */
    @Override
    public void doRemove() {
    }
}