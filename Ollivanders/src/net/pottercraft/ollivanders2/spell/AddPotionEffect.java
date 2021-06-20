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
 * Spell that adds a potion effect to one or more targets.
 *
 * @author Azami7
 */
public abstract class AddPotionEffect extends O2Spell
{
    /**
     * The duration for this effect.
     */
    int durationInSeconds;

    /**
     * The longest this effect can last.
     */
    int maxDurationInSeconds = 300; // 5 minutes;

    /**
     * The least amount of time this effect can last.
     */
    int minDurationInSeconds = 5; // 5 seconds

    /**
     * Strength modifier, 0 is no modifier.
     */
    int strengthModifier = 0;

    /**
     * Number of targets that can be affected
     */
    int numberOfTargets = 1;

    /**
     * The potion effect. Set to luck by default.
     */
    List<PotionEffectType> effectTypes = new ArrayList<>();

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     */
    public AddPotionEffect()
    {
        super();

        branch = O2MagicBranch.CHARMS;
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public AddPotionEffect(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);

        branch = O2MagicBranch.CHARMS;
        durationInSeconds = minDurationInSeconds;
    }

    /**
     * If a target player is within the radius of the projectile, add the potion effect to the player.
     */
    @Override
    protected void doCheckEffect()
    {
        affectRadius(1.5, false);

        if (hasHitTarget())
        {
            kill();
        }
    }

    /**
     * Affect targets within the radius.
     *
     * @param radius the radius of the spell
     * @param flair  whether or not to show a visual flair
     */
    void affectRadius(double radius, boolean flair)
    {
        if (flair)
        {
            Ollivanders2Common.flair(location, (int) radius, 10);
        }

        for (LivingEntity livingEntity : getLivingEntities(radius))
        {
            if ((livingEntity.getUniqueId() == player.getUniqueId()) || !(livingEntity instanceof Player))
            {
                continue;
            }

            addEffectsToTarget((Player) livingEntity);
            numberOfTargets--;

            // stop when the limit of targets is reached
            if (numberOfTargets <= 0)
            {
                kill();
                return;
            }
        }
    }

    /**
     * Add the effect to a target player.
     *
     * @param target the target player
     */
    void addEffectsToTarget(@NotNull Player target)
    {
        int duration = durationInSeconds * Ollivanders2Common.ticksPerSecond;

        for (PotionEffectType effectType : effectTypes)
        {
            org.bukkit.potion.PotionEffect effect = new org.bukkit.potion.PotionEffect(effectType, duration, strengthModifier);
            target.addPotionEffect(effect);
        }
    }
}
