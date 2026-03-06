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
 * Abstract base class for spells that apply one or more {@link net.pottercraft.ollivanders2.effect.O2Effect}
 * instances to a target player.
 *
 * <p>Spells extending this class fire a projectile toward a target and apply their configured effects
 * when the projectile reaches a player. Self-targeting spells ({@link #targetSelf} = true) apply
 * effects to the caster immediately; non-self-targeting spells apply effects to the first nearby
 * player found within {@link #defaultRadius} of the projectile.</p>
 *
 * <p>Effect duration is calculated from the caster's experience level via
 * {@link #calculateEffectDurationInSeconds()}, clamped to [{@link #minDurationInSeconds},
 * {@link #maxDurationInSeconds}]. Subclasses configure behavior by setting fields in their
 * constructors before calling {@link #initSpell()}.</p>
 *
 * <p><strong>Effect class contract:</strong> All {@link net.pottercraft.ollivanders2.effect.O2EffectType}
 * entries in {@link #effectsToAdd} must have an associated class with a constructor of the form
 * {@code (Ollivanders2, int, boolean, UUID)}.</p>
 *
 * @author Azami7
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
     * Checks for nearby targets and applies this spell's effects.
     *
     * <p>If the projectile has already hit a target (set by {@link #move()} hitting a solid block),
     * {@link #kill()} is called to complete cleanup and the current location is still checked for
     * nearby players. For self-targeting spells, effects are applied to the caster directly. For
     * non-self-targeting spells, effects are applied to up to {@link #numberOfTargets} nearby players
     * within {@link #defaultRadius}. The spell kills itself after successfully applying effects.</p>
     */
    @Override
    protected void doCheckEffect() {
        if (hasHitTarget())
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
     * Applies this spell's effects to a target player.
     *
     * <p>Calculates the effect duration from the caster's experience level, then instantiates and
     * registers each effect in {@link #effectsToAdd} via reflection. Effects on the
     * {@link #effectBlacklist} are skipped. Each effect class must provide a constructor of the
     * form {@code (Ollivanders2, int, boolean, UUID)}.</p>
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
     * Calculates and stores the effect duration in seconds based on the caster's experience level.
     *
     * <p>Duration is computed as {@code (int)(usesModifier * durationMultiplier) + durationModifier},
     * then clamped to [{@link #minDurationInSeconds}, {@link #maxDurationInSeconds}]. The result is
     * stored in {@link #durationInSeconds} and can be retrieved via {@link #getDurationInSeconds()}.</p>
     */
    public void calculateEffectDurationInSeconds() {
        durationInSeconds = ((int) (usesModifier * durationMultiplier) + durationModifier);

        if (durationInSeconds > maxDurationInSeconds)
            durationInSeconds = maxDurationInSeconds;
        else if (durationInSeconds < minDurationInSeconds)
            durationInSeconds = minDurationInSeconds;
    }

    /**
     * Returns a copy of the list of effect types this spell will apply to its target.
     *
     * @return a new list containing the effect types configured for this spell
     */
    public List<O2EffectType> getEffectsToAdd() {
        return new ArrayList<>() {{
            addAll(effectsToAdd);
        }};
    }

    /**
     * Returns whether this spell targets the caster rather than a nearby player.
     *
     * @return true if the spell applies its effects to the caster, false if it targets nearby players
     */
    public boolean targetsSelf() {
        return targetSelf;
    }

    /**
     * Returns the minimum effect duration in seconds.
     *
     * @return the minimum number of seconds the effect can last
     */
    public int getMinDurationInSeconds() {
        return minDurationInSeconds;
    }

    /**
     * Returns the maximum effect duration in seconds.
     *
     * @return the maximum number of seconds the effect can last
     */
    public int getMaxDurationInSeconds() {
        return maxDurationInSeconds;
    }

    /**
     * Returns whether this spell applies its effects permanently.
     *
     * @return true if the effects do not expire, false if they have a finite duration
     */
    public boolean isPermanent() {
        return permanent;
    }

    /**
     * Returns the most recently calculated effect duration in seconds.
     *
     * <p>This value is set by {@link #calculateEffectDurationInSeconds()} and reflects the duration
     * that will be applied to the target based on the caster's current experience level.</p>
     *
     * @return the calculated effect duration in seconds
     */
    public int getDurationInSeconds() {
        return durationInSeconds;
    }
}
