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
 * Base class for spells that remove Bukkit potion effects from the caster and/or nearby living entities, each removal
 * subject to a skill-based success check. Subclasses configure the effect types, radius, and targeting.
 *
 * @see AddPotionEffect
 */
public abstract class RemovePotionEffect extends O2Spell {
    /**
     * The potion effect types this spell removes.
     */
    List<PotionEffectType> potionEffectTypes = new ArrayList<>();

    /**
     * If true, the spell removes effects from every eligible target in range rather than just one.
     */
    boolean affectsMultiple = false;

    /**
     * If true, the caster is targeted in addition to nearby entities.
     */
    boolean targetSelf = false;

    /**
     * Divisor applied to the caster's skill when computing the removal success rate; see {@link #checkSuccess()}.
     */
    float successModifier = 1.0f;

    /**
     * If true, a visual particle effect is shown at the spell location when it applies.
     */
    boolean doFlair = false;

    /**
     * The radius of entities affected around the projectile, in blocks.
     */
    double effectRadius = defaultRadius;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public RemovePotionEffect(Ollivanders2 plugin) {
        super(plugin);

        branch = O2MagicBranch.CHARMS;
    }

    /**
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public RemovePotionEffect(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        branch = O2MagicBranch.CHARMS;
    }

    /**
     * Remove this spell's potion effects from eligible targets, then end the spell: the caster when
     * {@link #targetSelf} is set, plus nearby entities when {@link #affectsMultiple}.
     */
    @Override
    protected void doCheckEffect() {
        if (hasHitBlock())
            kill();

        if (doFlair)
            Ollivanders2Common.flair(location, (int) effectRadius, 10);

        boolean foundTargets = false;

        if (targetSelf) { // ensure the caster gets the effect removal first
            removePotionEffects(caster);
            foundTargets = true;
        }

        for (LivingEntity livingEntity : getNearbyLivingEntities(effectRadius)) {
            // stop if limit of targets is reached
            if (foundTargets && !affectsMultiple)
                break;

            if (livingEntity.getUniqueId().equals(caster.getUniqueId())) // already handled self above
                continue;

            removePotionEffects(livingEntity);
            foundTargets = true;
        }

        if (foundTargets)
            kill();
    }

    /**
     * Attempt to remove each of this spell's potion effects from the target, each subject to an independent
     * {@link #checkSuccess()} roll.
     *
     * @param target the living entity to remove effects from
     */
    void removePotionEffects(@NotNull LivingEntity target) {
        for (PotionEffectType potionEffectType : potionEffectTypes) {
            if (checkSuccess()) {
                common.printDebugMessage("Removing " + potionEffectType.getName() + " from " + target.getName(), null, null, false);
                target.removePotionEffect(potionEffectType);
            }
        }
    }

    /**
     * Roll for whether a single effect removal succeeds. The chance is {@code usesModifier / successModifier} percent,
     * limited to [1, 100]; a lower {@link #successModifier} makes success easier.
     *
     * @return true if the removal succeeds, false otherwise
     */
    boolean checkSuccess() {
        int successRate = (int) (usesModifier / successModifier);
        if (successRate < 1)
            successRate = 1;
        else if (successRate > 100)
            successRate = 100;

        return Ollivanders2Common.random.nextInt(100) < successRate;
    }

    /**
     * @return true if this spell targets the caster in addition to nearby entities
     */
    public boolean doesTargetSelf() {
        return targetSelf;
    }

    /**
     * @return true if this spell removes effects from every eligible target in range
     */
    public boolean doesAffectMultiple() {
        return affectsMultiple;
    }

    /**
     * @return the effect radius in blocks
     */
    public double getEffectRadius() {
        return effectRadius;
    }
}
