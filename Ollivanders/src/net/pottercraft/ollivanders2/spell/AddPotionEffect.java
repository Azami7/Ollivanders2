package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for spells that apply Bukkit potion effects to the caster and/or nearby living entities. Effect
 * duration, radius, and amplifier scale with the caster's skill; subclasses configure the effect types and ranges.
 *
 * @see <a href="https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffect.html">Bukkit PotionEffect documentation</a>
 * @author Azami7
 */
public abstract class AddPotionEffect extends O2Spell {
    /**
     * The effect duration in seconds. Set by {@link #calculateEffectDurationTicks()}.
     */
    int durationInSeconds;

    /**
     * Upper limit for {@link #durationInSeconds}.
     */
    int maxDurationInSeconds = 300; // 5 minutes;

    /**
     * Lower limit for {@link #durationInSeconds}.
     */
    int minDurationInSeconds = 5; // 5 seconds

    /**
     * Scales how much caster skill affects duration; see {@link #calculateEffectDurationTicks()} for the formula.
     */
    double durationModifier = 1.0; // 100% of usesModifier

    /**
     * The potion effect amplifier (0 = level I). Valid range is -127 to 127.
     *
     * @see <a href="https://minecraft.fandom.com/wiki/Effect">Minecraft Wiki - Effect</a>
     */
    int amplifier = 0;

    /**
     * If true, the spell stops after affecting one target; if false, it may affect every eligible target in range.
     */
    boolean affectSingleTarget = true;

    /**
     * If true, the caster is targeted in addition to nearby entities; if false, only other entities are targeted.
     */
    boolean targetSelf = false;

    /**
     * The radius of entities affected around the projectile, in blocks. Set by {@link #calculateEffectRadius()}.
     */
    double effectRadius = defaultRadius;

    /**
     * Lower limit for {@link #effectRadius}, in blocks.
     */
    double minEffectRadius = defaultRadius;

    /**
     * Upper limit for {@link #effectRadius}, in blocks.
     */
    double maxEffectRadius = defaultRadius;

    /**
     * If true, a visual particle effect is shown at the spell location when it applies.
     */
    boolean flair = false;

    /**
     * The potion effect types this spell applies. Empty by default; subclasses populate it.
     */
    List<PotionEffectType> potionEffectTypes = new ArrayList<>();

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public AddPotionEffect(Ollivanders2 plugin) {
        super(plugin);

        branch = O2MagicBranch.CHARMS;
    }

    /**
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public AddPotionEffect(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        branch = O2MagicBranch.CHARMS;
    }

    /**
     * Apply this spell's potion effects to eligible targets each tick.
     */
    @Override
    protected void doCheckEffect() {
        if (hasHitBlock() || noProjectile)
            kill();

        affectRadius();
    }

    /**
     * Apply the potion effects to the caster (when {@link #targetSelf} is set) and/or nearby living entities, ending
     * the spell once a target is affected. Stops after the first target when {@link #affectSingleTarget} is set.
     */
    void affectRadius() {
        doFlair();

        if (targetSelf) {
            addEffectsToTarget(caster);
            kill(); // because we have hit a target, don't let the spell keep going after this tick

            if (affectSingleTarget)
                return;
        }

        calculateEffectRadius();

        for (LivingEntity livingEntity : getNearbyLivingEntities(effectRadius)) {
            if (livingEntity.getUniqueId().equals(caster.getUniqueId()))
                continue;

            addEffectsToTarget(livingEntity);
            kill(); // because we have hit a target, don't let the spell keep going after this tick

            if (affectSingleTarget)
                return;
        }
    }

    /**
     * Set {@link #effectRadius} to {@code usesModifier / 10}, limited to [{@link #minEffectRadius},
     * {@link #maxEffectRadius}].
     */
    void calculateEffectRadius() {
        effectRadius = usesModifier / 10;

        if (effectRadius < minEffectRadius)
            effectRadius = minEffectRadius;
        else if (effectRadius > maxEffectRadius)
            effectRadius = maxEffectRadius;
    }

    /**
     * Apply visual particle effects at the spell location if enabled.
     */
    void doFlair() {
        if (flair)
            Ollivanders2Common.flair(location, (int) effectRadius, 10);
    }

    /**
     * Apply this spell's potion effects to the target for the skill-scaled duration and amplifier.
     *
     * @param target the target living entity
     */
    void addEffectsToTarget(@NotNull LivingEntity target) {
        // duration
        int durationInTicks = calculateEffectDurationTicks();

        calculateAmplifier();

        for (PotionEffectType effectType : potionEffectTypes) {
            org.bukkit.potion.PotionEffect effect = new org.bukkit.potion.PotionEffect(effectType, durationInTicks, amplifier);
            target.addPotionEffect(effect);

            common.printDebugMessage("Added " + effectType + " to " + target.getName() + " with amplifier of " + amplifier + " and duration of " + durationInSeconds + " seconds", null, null, false);
        }
    }

    /**
     * Set {@link #durationInSeconds} to {@code usesModifier * durationModifier}, limited to
     * [{@link #minDurationInSeconds}, {@link #maxDurationInSeconds}].
     *
     * @return that duration converted to ticks
     */
    int calculateEffectDurationTicks() {
        durationInSeconds = (int) (usesModifier * durationModifier);
        if (durationInSeconds < minDurationInSeconds)
            durationInSeconds = minDurationInSeconds;
        else if (durationInSeconds > maxDurationInSeconds)
            durationInSeconds = maxDurationInSeconds;

        return durationInSeconds * Ollivanders2Common.ticksPerSecond;
    }

    /**
     * Set {@link #amplifier} from the caster's skill. The default raises it by one level past half spell mastery;
     * subclasses override for custom scaling.
     */
    void calculateAmplifier() {
        amplifier = 0;

        if (usesModifier > (double) (O2Spell.spellMasteryLevel / 2))
            amplifier = 1;
    }

    /**
     * @return a copy of the potion effect types this spell applies
     */
    @NotNull
    public List<PotionEffectType> getPotionEffectTypes() {
        return new ArrayList<>() {{
            addAll(potionEffectTypes);
        }};
    }

    /**
     * @return the minimum effect duration in seconds
     */
    public int getMinDurationInSeconds() {
        return minDurationInSeconds;
    }

    /**
     * @return the maximum effect duration in seconds
     */
    public int getMaxDurationInSeconds() {
        return maxDurationInSeconds;
    }

    /**
     * @return the effect duration in seconds most recently set by {@link #calculateEffectDurationTicks()}
     */
    public int getDurationInSeconds() {
        return durationInSeconds;
    }

    /**
     * @return the minimum effect radius in blocks
     */
    public double getMinEffectRadius() {
        return minEffectRadius;
    }

    /**
     * @return the maximum effect radius in blocks
     */
    public double getMaxEffectRadius() {
        return maxEffectRadius;
    }

    /**
     * @return the effect radius in blocks most recently set by {@link #calculateEffectRadius()}
     */
    public double getEffectRadius() {
        return effectRadius;
    }

    /**
     * @return true if this spell stops after affecting a single target
     */
    public boolean affectsSingleTarget() {
        return affectSingleTarget;
    }

    /**
     * @return true if this spell targets the caster in addition to nearby entities
     */
    public boolean targetsSelf() {
        return targetSelf;
    }

    /**
     * @return the potion effect amplifier
     */
    public int getAmplifier() {
        return amplifier;
    }
}
