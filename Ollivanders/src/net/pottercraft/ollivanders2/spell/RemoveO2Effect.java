package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Remove an O2Effect from a target player.
 */
abstract public class RemoveO2Effect extends O2Spell {
    /**
     * The maximum number of targets for this spell
     */
    int maxTargets = 1;

    /**
     * Number of targets that can be affected
     */
    int targetsRemaining = 1;

    /**
     * The modifier for success rate on this spell
     */
    float successModifier = 1.0f;

    /**
     * The effects this spell can target
     */
    List<O2EffectType> effectsAllowList = new ArrayList<>();

    /**
     * List of effects that cannot be removed by any spell.
     */
    List<O2EffectType> effectBlockedList = new ArrayList<>() {{
        add(O2EffectType.ANIMAGUS_EFFECT);
        add(O2EffectType.ANIMAGUS_INCANTATION);
        add(O2EffectType.LYCANTHROPY);
        add(O2EffectType.LYCANTHROPY_SPEECH);
        add(O2EffectType.LYCANTHROPY_RELIEF);
    }};

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public RemoveO2Effect(Ollivanders2 plugin) {
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
    public RemoveO2Effect(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        branch = O2MagicBranch.CHARMS;
    }

    /**
     * If a target player is within the radius of the projectile, add the potion effect to the player.
     */
    @Override
    protected void doCheckEffect() {
        affectRadius(defaultRadius, false);

        if (hasHitTarget())
            kill();
    }

    /**
     * Affect targets within the radius.
     *
     * @param radius the radius of the spell
     * @param flair  whether to show a visual flair
     */
    void affectRadius(double radius, boolean flair) {
        if (flair)
            Ollivanders2Common.flair(location, (int) radius, 10);

        for (Player target : getNearbyPlayers(radius)) {
            if (target.getUniqueId().equals(player.getUniqueId()))
                continue;

            removeEffects(target);

            targetsRemaining = targetsRemaining - 1;

            // stop when the limit of targets is reached
            if (targetsRemaining <= 0) {
                kill();
                return;
            }
        }
    }

    /**
     * Remove 1 or more O2Effects from the target player
     *
     * @param target the player to remove effects from
     */
    void removeEffects(@NotNull Player target) {
        for (O2EffectType effectType : effectsAllowList) {
            // just in case a blocked effect was added to the allowed list
            if (effectBlockedList.contains(effectType))
                continue;

            if (!Ollivanders2API.getPlayers().playerEffects.hasEffect(target.getUniqueId(), effectType))
                continue;

            if (checkSuccess(effectType))
                Ollivanders2API.getPlayers().playerEffects.removeEffect(target.getUniqueId(), effectType);

            targetsRemaining -= 1;
            if (targetsRemaining <= 0) {
                kill();
                break;
            }
        }
    }

    /**
     * Determine if this spell is successful based on player skill and level of the effect relative to the level of this spell.
     *
     * @param effectType the type of effect targeted
     * @return true if the effect can be removed, false otherwise
     */
    boolean checkSuccess(O2EffectType effectType) {
        if (effectType.getLevel().ordinal() > this.getLevel().ordinal())
            return false;

        int successRate = (int) (usesModifier / successModifier);
        if (successRate < 1)
            successRate = 1;
        else if (successRate > 100)
            successRate = 100;

        return Math.abs(Ollivanders2Common.random.nextInt() % 100) < successRate;
    }
}
