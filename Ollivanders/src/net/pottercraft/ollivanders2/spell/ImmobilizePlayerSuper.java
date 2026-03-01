package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.FULL_IMMOBILIZE;
import net.pottercraft.ollivanders2.effect.IMMOBILIZE;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for player immobilization spells.
 *
 * <p>ImmobilizePlayerSuper provides the common implementation for spells that immobilize a target player
 * by applying immobilization effects. This class handles target detection, effect duration calculation,
 * and immobilization effect application. Concrete subclasses must implement target validation (canTarget)
 * and any additional spell-specific effects (addAdditionalEffects).</p>
 *
 * <p>Spell Mechanics:</p>
 * <ul>
 * <li>Searches for nearby players within the spell's default radius</li>
 * <li>Validates each nearby player using canTarget() to determine if they can be affected</li>
 * <li>Calculates immobilization duration based on spell uses, clamped to min/max bounds</li>
 * <li>Applies either IMMOBILIZE (partial) or FULL_IMMOBILIZE (complete) based on spell type</li>
 * <li>Triggers spell-specific additional effects via addAdditionalEffects()</li>
 * <li>Kills the spell after successfully targeting a player</li>
 * </ul>
 *
 * @author Azami7
 * @see IMMOBILIZE for partial immobilization effect
 * @see FULL_IMMOBILIZE for complete immobilization effect
 * @see O2Spell for the base spell class
 */
abstract public class ImmobilizePlayerSuper extends O2Spell {
    /**
     * The minimum time the target player can be affected.
     */
    int minEffectDuration = 30 * Ollivanders2Common.ticksPerSecond;

    /**
     * The maximum time the target player can be affected.
     */
    int maxEffectDuration = 300 * Ollivanders2Common.ticksPerSecond;

    /**
     * The duration for this spell's effect.
     */
    int effectDuration = 0;

    /**
     * Does this spell fully immobilize the target - location plus pitch and yaw. If false, it
     * will allow pitch and yaw changes.
     */
    boolean fullImmobilize = false;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public ImmobilizePlayerSuper(Ollivanders2 plugin) {
        super(plugin);
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public ImmobilizePlayerSuper(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
    }

    /**
     * Does this spell fully immobilize - location/velocity and pitch/yaw - or not.
     *
     * @return true if the spell does full immobilize, false if it only affects location/velocity
     */
    public boolean isFullImmobilize() {
        return fullImmobilize;
    }

    /**
     * Search for nearby players and immobilize the first valid target.
     *
     * <p>Called when the spell is active. This method iterates through nearby players within the spell's
     * default radius, validates each using canTarget(), and immobilizes the first valid target. The
     * validation skips the caster and any players that fail the canTarget() check. Upon finding a valid
     * target, applies the calculated immobilization effect, triggers additional spell-specific effects,
     * and kills the spell.</p>
     */
    @Override
    protected void doCheckEffect() {
        // projectile has stopped, kill the spell
        if (hasHitTarget())
            kill();

        for (Player target : getNearbyPlayers(defaultRadius)) {
            if (target.getUniqueId().equals(player.getUniqueId()) || !canTarget(target)) {
                common.printDebugMessage("ImmobilizePlayerSuper.doCheckEffect: " + target.getName() + " cannot be targeting, skipping", null, null, false);
                continue;
            }

            common.printDebugMessage("ImmobilizePlayerSuper.doCheckEffect: targeting " + target.getName(), null, null, false);

            calculateDuration();
            addImmobilizationEffect(target);
            addAdditionalEffects(target);

            kill();
            return;
        }
    }

    /**
     * Determine if a player can be targeted by this immobilization spell.
     *
     * <p>Abstract method that concrete subclasses must implement to define spell-specific target validation
     * rules. Examples include checking for protective effects, player status, or other conditions that
     * would prevent the spell from affecting a player.</p>
     *
     * @param target the player to validate as a potential target
     * @return true if the player can be targeted and immobilized, false otherwise
     */
    abstract boolean canTarget(Player target);

    /**
     * Calculate the immobilization effect duration based on spell usage.
     *
     * <p>Calculates the immobilization duration in game ticks based on the spell's usage count (usesModifier).
     * The base formula is: (usesModifier + 30) seconds, which is then converted to ticks. The resulting
     * duration is clamped to the configured minimum (30 seconds / 600 ticks) and maximum (300 seconds / 6000 ticks)
     * bounds. The calculated duration is stored in the effectDuration field.</p>
     */
    void calculateDuration() {
        effectDuration = ((int) usesModifier + 30) * Ollivanders2Common.ticksPerSecond;

        if (effectDuration > maxEffectDuration)
            effectDuration = maxEffectDuration;
        else if (effectDuration < minEffectDuration)
            effectDuration = minEffectDuration;
    }

    /**
     * Apply the immobilization effect to the target player.
     *
     * <p>Creates and applies either a FULL_IMMOBILIZE effect (if fullImmobilize is true) or an IMMOBILIZE
     * effect (if fullImmobilize is false) to the target player with the calculated effect duration.
     * The effect is added to the player effects system and begins taking effect immediately.</p>
     *
     * @param target the player to immobilize
     */
    void addImmobilizationEffect(Player target) {
        IMMOBILIZE immobilize;

        if (fullImmobilize)
            immobilize = new FULL_IMMOBILIZE(p, effectDuration, false, target.getUniqueId());
        else
            immobilize = new IMMOBILIZE(p, effectDuration, false, target.getUniqueId());

        Ollivanders2API.getPlayers().playerEffects.addEffect(immobilize);
    }

    /**
     * Apply any spell-specific additional effects to the immobilized target.
     *
     * <p>Abstract method that concrete subclasses must implement to apply spell-specific effects beyond
     * the base immobilization effect. Examples include potion effects, block modifications, or other
     * environmental changes specific to the spell's mechanics.</p>
     *
     * @param target the immobilized player
     */
    abstract void addAdditionalEffects(Player target);

    /**
     * Get the calculated immobilization duration in game ticks.
     *
     * @return the effect duration in ticks
     */
    public int getEffectDuration() {
        return effectDuration;
    }

    /**
     * Get the minimum immobilization duration in game ticks.
     *
     * @return the minimum effect duration (30 seconds / 600 ticks)
     */
    public int getMinEffectDuration() {
        return minEffectDuration;
    }

    /**
     * Get the maximum immobilization duration in game ticks.
     *
     * @return the maximum effect duration (300 seconds / 6000 ticks)
     */
    public int getMaxEffectDuration() {
        return maxEffectDuration;
    }
}
