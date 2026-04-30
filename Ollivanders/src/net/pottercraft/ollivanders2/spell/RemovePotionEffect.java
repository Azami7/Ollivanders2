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
 * Base class for spells that remove potion effects from targets.
 *
 * <p>Provides functionality for removing one or more potion effects from living entities within
 * a configurable radius. Each effect removal is subject to a success check based on the caster's
 * spell experience level. Subclasses configure which effect types to remove, the effect radius,
 * number of targets, and whether to target the caster or nearby entities.</p>
 *
 * <p>Configuration:</p>
 *
 * <ul>
 * <li>Success rate: calculated from usesModifier / successModifier, clamped to [1, 100]</li>
 * <li>Does this spell affect multiple targets, set by {@link #affectsMultiple}</li>
 * <li>Target filtering: excludes caster unless {@link #targetSelf} is true</li>
 * <li>Visual flair: optional particle effect at spell location (controlled by {@link #doFlair})</li>
 * <li>Effect radius: configurable via {@link #effectRadius}, defaults to {@link O2Spell#defaultRadius}</li>
 * </ul>
 *
 * @see AddPotionEffect
 */
public abstract class RemovePotionEffect extends O2Spell {
    /**
     * The potion effects to remove
     */
    List<PotionEffectType> potionEffectTypes = new ArrayList<>();

    /**
     * Does this spell affect multiple targets
     */
    boolean affectsMultiple = false;

    /**
     * Whether the spell targets the caster
     */
    boolean targetSelf = false;

    /**
     * The modifier for success rate on this spell
     */
    float successModifier = 1.0f;

    /**
     * Does this spell do a flair?
     */
    boolean doFlair = false;

    /**
     * The radius of effect for this spell
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
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public RemovePotionEffect(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        branch = O2MagicBranch.CHARMS;
    }

    /**
     * Remove potion effects from nearby targets within the effect radius.
     *
     * <p>If the spell has hit a block, it is killed. Otherwise, processes targets within the effect
     * radius — starting with the caster if {@link #targetSelf} is true, then iterating through
     * nearby living entities if {@link #affectsMultiple}. Kills the spell once at least one
     * target has been processed.</p>
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
     * Remove potion effects from the target entity.
     *
     * <p>Iterates through all configured {@link #potionEffectTypes} and attempts to remove each one.
     * Each removal is subject to a {@link #checkSuccess()} roll — only effects that pass the check
     * are removed.</p>
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
     * Determine if this spell successfully removes an effect based on the caster's experience.
     *
     * <p>Calculates a success rate from {@code usesModifier / successModifier}, clamped to [1, 100],
     * then rolls against it. A lower {@link #successModifier} makes success easier (e.g., 0.01 yields
     * near-certain success).</p>
     *
     * @return true if the effect can be removed, false otherwise
     */
    boolean checkSuccess() {
        int successRate = (int) (usesModifier / successModifier);
        if (successRate < 1)
            successRate = 1;
        else if (successRate > 100)
            successRate = 100;

        return Ollivanders2Common.random.nextInt(100) < successRate;
    }

    public boolean doesTargetSelf() {
        return targetSelf;
    }

    public boolean doesAffectMultiple() {
        return affectsMultiple;
    }

    public double getEffectRadius() {
        return effectRadius;
    }
}
