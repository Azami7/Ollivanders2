package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.effect.O2Effect;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Spell type that adds an O2Effect to a target.
 */
public abstract class AddO2Effect extends O2Spell {
    /**
     * The duration for this effect.
     */
    int durationInSeconds;

    /**
     * If temporary, the longest this effect can last.
     */
    int maxDurationInSeconds = 300; // 5 minutes;

    /**
     * If temporary, the least amount of time this effect can last.
     */
    int minDurationInSeconds = 5; // 5 seconds

    /**
     * Duration modifier
     */
    int durationModifier = 10;

    /**
     * Duration multiplier - this is multiplied with the usesModifier
     */
    double durationMultiplier = 1;

    /**
     * Strength modifier, 1 is no modifier.
     */
    int strengthModifier = 1;

    /**
     * Number of targets that can be affected.
     */
    int numberOfTargets = 1;

    /**
     * Whether the spell has a permanent effect.
     */
    boolean permanent = false;

    /**
     * Whether the spell targets the caster.
     */
    boolean targetSelf = false;

    /**
     * The effects to add.
     */
    List<O2EffectType> effectsToAdd = new ArrayList<>();

    /**
     * Blacklist of effects that cannot be added by a spell.
     */
    List<O2EffectType> effectBlacklist = new ArrayList<>() {{
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
    public AddO2Effect(Ollivanders2 plugin) {
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
    public AddO2Effect(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        branch = O2MagicBranch.CHARMS;
        durationInSeconds = minDurationInSeconds;
    }

    /**
     * If a target player is within the radius of the projectile, add the effect to the player.
     */
    @Override
    protected void doCheckEffect() {
        if (targetSelf) {
            addEffectsToTarget(player);
            kill();
            return;
        }

        for (Player target : getNearbyPlayers(defaultRadius)) {
            if ((target.getUniqueId() == player.getUniqueId()))
                continue;

            addEffectsToTarget(target);

            // if the spell can only target a limited number, stop when the limit is reached
            if (numberOfTargets <= 0) {
                kill();
                return;
            }
        }

        if (hasHitTarget())
            kill();
    }

    /**
     * Add the effects for this spell to a target player.
     *
     * @param target the player to add spells to
     */
    private void addEffectsToTarget(@NotNull Player target) {
        // calculate the duration based on the experience with this spell
        durationInSeconds = ((int)(usesModifier * durationMultiplier) + durationModifier);

        if (durationInSeconds > maxDurationInSeconds)
            durationInSeconds = maxDurationInSeconds;
        else if (durationInSeconds < minDurationInSeconds)
            durationInSeconds = minDurationInSeconds;

        int duration = durationInSeconds * Ollivanders2Common.ticksPerSecond;

        // add the effect
        for (O2EffectType effectType : effectsToAdd) {
            if (effectBlacklist.contains(effectType))
                continue;

            Class<?> effectClass = effectType.getClassName();

            O2Effect effect;
            try {
                effect = (O2Effect) effectClass.getConstructor(Ollivanders2.class, int.class, UUID.class).newInstance(p, duration * strengthModifier, target.getUniqueId());
            }
            catch (Exception e) {
                common.printDebugMessage("Failed to create class for " + effectType, e, null, true);
                continue;
            }

            if (permanent)
                effect.setPermanent(true);

            Ollivanders2API.getPlayers().playerEffects.addEffect(effect);
        }
    }
}
