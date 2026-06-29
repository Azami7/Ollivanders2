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
 * Abstract base class for spells that remove a single {@link O2EffectType} from a target player.
 * <p>
 * When the projectile reaches a nearby player, the spell attempts to remove the first effect on its
 * {@link #effectsAllowList} that the target currently has, subject to a skill-based success check
 * ({@link #checkSuccess(O2EffectType)}). Effects on {@link #effectBlockedList} can never be removed, regardless of the
 * allow list. Subclasses configure the allow list and {@link #successModifier} in their constructors.
 * </p>
 */
abstract public class RemoveO2Effect extends O2Spell {
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
     * If a target player is within the radius of the projectile, remove the effect from the player.
     */
    @Override
    protected void doCheckEffect() {
        affectRadius(defaultRadius, false);

        if (hasHitBlock())
            kill();
    }

    /**
     * Remove an effect from the first eligible target within the radius, then end the spell.
     *
     * @param radius the radius within which to search for a target
     * @param flair  whether to show a visual flair at the projectile location
     */
    void affectRadius(double radius, boolean flair) {
        if (flair)
            Ollivanders2Common.flair(location, (int) radius, 10);

        for (Player target : getNearbyPlayers(radius)) {
            if (target.getUniqueId().equals(caster.getUniqueId()))
                continue;

            removeEffects(target);
            kill();
            return;
        }
    }

    /**
     * Remove the first allowed effect the target currently has, if the success check passes.
     * <p>
     * Skips any effect on {@link #effectBlockedList} and any the target does not have. On the first allowed, present
     * effect that {@link #checkSuccess(O2EffectType)} approves, the effect is removed and the method returns - at most
     * one effect is removed per call.
     * </p>
     *
     * @param target the player to remove an effect from
     */
    void removeEffects(@NotNull Player target) {
        for (O2EffectType effectType : effectsAllowList) {
            // just in case a blocked effect was added to the allowed list
            if (effectBlockedList.contains(effectType))
                continue;

            if (!Ollivanders2API.getPlayers().playerEffects.hasEffect(target.getUniqueId(), effectType))
                continue;

            if (checkSuccess(effectType)) {
                Ollivanders2API.getPlayers().playerEffects.removeEffect(target.getUniqueId(), effectType);
                return;
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

    /**
     * Get the effects this spell is able to remove.
     *
     * @return a copy of the allow list
     */
    public List<O2EffectType> getEffectsAllowList() {
        return new ArrayList<>(effectsAllowList);
    }
}
