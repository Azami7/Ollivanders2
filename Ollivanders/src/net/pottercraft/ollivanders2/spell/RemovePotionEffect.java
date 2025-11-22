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
 * Remove potion effects from a target.
 */
public abstract class RemovePotionEffect extends O2Spell {
    /**
     * The potion effect. Set to luck by default.
     */
    List<PotionEffectType> potionEffectTypes = new ArrayList<>();

    /**
     * Number of targets that can be affected
     */
    int numberOfTargets = 1;

    /**
     * Whether the spell targets the caster
     */
    boolean targetSelf = false;

    /**
     * The modifier for success rate on this spell
     */
    float successModifier = 1.0f;

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
     * If a target player is within the radius of the projectile, add the potion effect to the player.
     */
    @Override
    protected void doCheckEffect() {
        if (hasHitTarget())
            kill();

        affectRadius(defaultRadius, false);
    }

    /**
     * Affect targets within the radius.
     *
     * @param radius the radius of the spell
     * @param flair  whether or not to show a visual flair
     */
    void affectRadius(double radius, boolean flair) {
        if (flair)
            Ollivanders2Common.flair(location, (int) radius, 10);

        if (targetSelf) {
            removePotionEffects(player);
            numberOfTargets = numberOfTargets - 1;
        }

        for (LivingEntity livingEntity : getNearbyLivingEntities(radius)) {
            // stop when the limit of targets is reached
            if (numberOfTargets <= 0) {
                kill();
                return;
            }

            if (livingEntity.getUniqueId().equals(player.getUniqueId())) // already handled self above
                continue;

            removePotionEffects(livingEntity);
            numberOfTargets = numberOfTargets - 1;
        }
    }

    /**
     * Remove potion effects from the target player
     *
     * @param target the player to remove effects from
     */
    void removePotionEffects(@NotNull LivingEntity target) {
        for (PotionEffectType effectType : potionEffectTypes) {
            if (checkSuccess()) {
                common.printDebugMessage("Removing " + effectType.getName() + " from " + target.getName(), null, null, false);
                target.removePotionEffect(effectType);
            }
        }
    }

    /**
     * Determine if this spell is successful based on player skill and level of the effect relative to the level of this spell.
     *
     * @return true if the effect can be removed, false otherwise
     */
    boolean checkSuccess() {
        int successRate = (int) (usesModifier / successModifier);
        if (successRate < 1)
            successRate = 1;
        else if (successRate > 100)
            successRate = 100;

        return Math.abs(Ollivanders2Common.random.nextInt() % 100) < successRate;
    }
}
