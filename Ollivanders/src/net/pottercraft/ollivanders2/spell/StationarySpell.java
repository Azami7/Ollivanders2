package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for spells that create a persistent {@link O2StationarySpell} in the world. The spell either lands on a
 * target block or is cast directly on the caster (noProjectile); on landing it creates the stationary spell with a
 * skill-scaled duration and radius, then ends.
 * <p>
 * Subclasses implement {@link #createStationarySpell()}. Unlike most spells, water does not stop the projectile.
 * </p>
 *
 * @see O2StationarySpell
 */
public abstract class StationarySpell extends O2Spell {
    /**
     * The stationary spell's duration in ticks. Set by {@link #setDuration()}.
     */
    int duration;

    /**
     * Seconds added to the base duration; see {@link #setDuration()} for the formula.
     */
    int durationModifierInSeconds = 15;

    /**
     * The stationary spell's radius in blocks. Set by {@link #setRadius()}.
     */
    int radius;

    /**
     * Divisor applied to caster skill when computing radius; see {@link #setRadius()} for the formula.
     */
    int radiusModifier = 1;

    /**
     * If true, a visual flair is shown when the stationary spell is created.
     */
    boolean flair = true;

    /**
     * The size of the flair effect.
     */
    int flairSize = 10;

    /**
     * Upper limit for {@link #duration}, in ticks. Only applies to non-permanent stationary spells.
     */
    int maxDuration = Ollivanders2Common.ticksPerMinute * 30;

    /**
     * Lower limit for {@link #duration}, in ticks. Only applies to non-permanent stationary spells.
     */
    int minDuration = Ollivanders2Common.ticksPerMinute;

    /**
     * Upper limit for {@link #radius}, in blocks.
     */
    int maxRadius = 50;

    /**
     * Lower limit for {@link #radius}, in blocks.
     */
    int minRadius = 5;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public StationarySpell(Ollivanders2 plugin) {
        super(plugin);

        branch = O2MagicBranch.CHARMS;
    }

    /**
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand the wand correctness factor
     */
    public StationarySpell(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        branch = O2MagicBranch.CHARMS;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);

        // pass-through materials
        projectilePassThrough.remove(Material.WATER);

        successMessage = "Stationary spell successfully cast.";
        failureMessage = "Nothing seems to happen.";
    }

    /**
     * On landing (or immediately for noProjectile spells), create the stationary spell with a skill-scaled duration
     * and radius, register it, and end this spell. Sends the failure message if creation fails.
     */
    @Override
    protected void doCheckEffect() {
        if (!noProjectile && !hasHitBlock()) // if we have not hit a target, continue
            return;

        setDuration();
        setRadius();

        O2StationarySpell stationarySpell = createStationarySpell();

        if (stationarySpell != null) {
            if (flair)
                stationarySpell.flair(flairSize);

            Ollivanders2API.getStationarySpells().addStationarySpell(stationarySpell);

            sendSuccessMessage();
        }
        else
            sendFailureMessage();

        kill();
    }

    /**
     * Set {@link #duration} to {@code (usesModifier + durationModifierInSeconds) * ticksPerSecond}, limited to
     * [{@link #minDuration}, {@link #maxDuration}].
     */
    void setDuration() {
        duration = ((int) usesModifier * Ollivanders2Common.ticksPerSecond) + (Ollivanders2Common.ticksPerSecond * durationModifierInSeconds);

        if (duration > maxDuration)
            duration = maxDuration;
        else if (duration < minDuration)
            duration = minDuration;
    }

    /**
     * Set {@link #radius} to {@code floor(usesModifier / radiusModifier)}, limited to [{@link #minRadius},
     * {@link #maxRadius}].
     */
    void setRadius() {
        radius = (int) Math.floor(usesModifier / radiusModifier);
        if (radius > maxRadius)
            radius = maxRadius;
        else if (radius < minRadius)
            radius = minRadius;
    }

    /**
     * Create the stationary spell instance for this spell type, using the calculated {@link #radius} and
     * {@link #duration}.
     *
     * @return the stationary spell instance, or null if it cannot be created (treated as a failed cast)
     */
    @Nullable
    abstract protected O2StationarySpell createStationarySpell();

    /**
     * Gets the calculated duration for this spell.
     *
     * @return the duration in ticks
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Gets the maximum duration for this spell type.
     *
     * @return the maximum duration in ticks
     */
    public int getMaxDuration() {
        return maxDuration;
    }

    /**
     * Gets the minimum duration for this spell type.
     *
     * @return the minimum duration in ticks
     */
    public int getMinDuration() {
        return minDuration;
    }

    /**
     * Gets the calculated radius for this spell.
     *
     * @return the radius in blocks
     */
    public int getRadius() {
        return radius;
    }

    /**
     * Gets the maximum radius for this spell type.
     *
     * @return the maximum radius in blocks
     */
    public int getMaxRadius() {
        return maxRadius;
    }

    /**
     * Gets the minimum radius for this spell type.
     *
     * @return the minimum radius in blocks
     */
    public int getMinRadius() {
        return minRadius;
    }
}
