package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.O2Effect;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Base class for spells that apply one or more {@link O2Effect}s to a target player — the caster when
 * {@link #targetSelf} is set, otherwise nearby players. Effect duration scales with the caster's skill.
 * <p>
 * Each {@link O2EffectType} in {@link #effectsToAdd} must have an implementing class with a
 * {@code (Ollivanders2, int, boolean, UUID)} constructor.
 * </p>
 *
 * @author Azami7
 */
public abstract class AddO2Effect extends O2Spell {
    /**
     * If temporary, the longest this effect can last.
     */
    int maxDurationInSeconds = 300; // 5 minutes;

    /**
     * If temporary, the least amount of time this effect can last.
     */
    int minDurationInSeconds = 5; // 5 seconds

    /**
     * The duration for this effect.
     */
    int durationInSeconds = minDurationInSeconds;

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
    private static final List<O2EffectType> effectBlacklist = new ArrayList<>() {{
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
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public AddO2Effect(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        branch = O2MagicBranch.CHARMS;
    }

    /**
     * Apply this spell's effects to its target(s) and end the spell: the caster when {@link #targetSelf} is set, plus
     * up to {@link #numberOfTargets} nearby players.
     */
    @Override
    protected void doCheckEffect() {
        if (hasHitBlock())
            kill();

        int targets = 0;

        if (targetSelf) {
            common.printDebugMessage("AddO2Effect.doCheckEffect: adding effect to caster", null, null, false);
            addEffectsToTarget(caster);
            targets = targets + 1;
        }

        for (Player target : getNearbyPlayers(defaultRadius)) {
            if ((target.getUniqueId().equals(caster.getUniqueId())))
                continue;

            if (targets >= numberOfTargets)
                break;

            common.printDebugMessage("AddO2Effect.doCheckEffect: adding effect to " + target.getName(), null, null, false);
            addEffectsToTarget(target);
            targets = targets + 1;
        }

        if (targets > 0)
            kill();
    }

    /**
     * Apply this spell's effects to the given player for the skill-scaled duration. Effects on the internal blacklist
     * are skipped.
     *
     * @param target the player to apply effects to
     */
    private void addEffectsToTarget(@NotNull Player target) {
        calculateEffectDurationInSeconds();
        int duration = durationInSeconds * Ollivanders2Common.ticksPerSecond;

        // add the effect
        for (O2EffectType effectType : effectsToAdd) {
            if (effectBlacklist.contains(effectType))
                continue;

            Class<?> effectClass = effectType.getClassName();

            O2Effect effect;
            try {
                effect = (O2Effect) effectClass.getConstructor(Ollivanders2.class, int.class, boolean.class, UUID.class).newInstance(p, duration * strengthModifier, permanent, target.getUniqueId());
            }
            catch (Exception e) {
                common.printDebugMessage("Failed to create class for " + effectType, e, null, true);
                continue;
            }

            Ollivanders2API.getPlayers().playerEffects.addEffect(effect);
        }
    }

    /**
     * Set {@link #durationInSeconds} to {@code usesModifier * durationMultiplier}, limited to
     * [{@link #minDurationInSeconds}, {@link #maxDurationInSeconds}].
     */
    public void calculateEffectDurationInSeconds() {
        durationInSeconds = (int) (usesModifier * durationMultiplier);

        if (durationInSeconds > maxDurationInSeconds)
            durationInSeconds = maxDurationInSeconds;
        else if (durationInSeconds < minDurationInSeconds)
            durationInSeconds = minDurationInSeconds;
    }

    /**
     * @return a copy of the effect types this spell applies
     */
    public List<O2EffectType> getEffectsToAdd() {
        return new ArrayList<>() {{
            addAll(effectsToAdd);
        }};
    }

    /**
     * @return true if this spell applies its effects to the caster rather than nearby players
     */
    public boolean targetsSelf() {
        return targetSelf;
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
     * @return true if this spell's effects do not expire
     */
    public boolean isPermanent() {
        return permanent;
    }

    /**
     * @return the effect duration in seconds most recently set by {@link #calculateEffectDurationInSeconds()}
     */
    public int getDurationInSeconds() {
        return durationInSeconds;
    }
}
