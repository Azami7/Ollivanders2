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
 * Parent class for all spells that create a stationary spell.
 *
 * <p>StationarySpell extends O2Spell to handle spells that create persistent effects in the world.
 * These spells either hit a target block (with a projectile) or are cast directly on the caster
 * (noProjectile). When the spell hits its target (or is cast), it calculates duration and radius
 * based on the caster's experience, creates the underlying stationary spell instance, and displays
 * flair effects if configured.</p>
 *
 * <p>Subclasses must implement {@link #createStationarySpell()} to define the spell-specific
 * behavior and instance type. They can also override the default duration/radius modifiers and
 * min/max values to customize spell scaling.</p>
 *
 * <p>Key behavior:</p>
 *
 * <ul>
 * <li>Duration is calculated as: (experience × ticksPerSecond) + (ticksPerSecond × durationModifierInSeconds)</li>
 * <li>Radius is calculated as: floor(experience / radiusModifier)</li>
 * <li>Both duration and radius are clamped to their configured min/max bounds</li>
 * <li>Water is NOT a pass-through material (unlike other O2Spell subclasses)</li>
 * <li>The spell is killed after the stationary spell is created</li>
 * </ul>
 *
 * @see O2StationarySpell
 */
public abstract class StationarySpell extends O2Spell {
    /**
     * The time left for this stationary spell.
     */
    int duration;

    /**
     * Duration modifier
     */
    int durationModifierInSeconds = 15;

    /**
     * Radius of the spell
     */
    int radius;

    /**
     * Radius modifier
     */
    int radiusModifier = 1;

    /**
     * Do flair when casting the stationary spell
     */
    boolean flair = true;

    /**
     * Flair size
     */
    int flairSize = 10;

    /**
     * The maximum duration a stationary spell can last, if it is not permanent
     */
    int maxDuration = Ollivanders2Common.ticksPerMinute * 30; // 30 minutes, only applies to non-permanent stationary spells.

    /**
     * The minimum duration a stationary spell can last
     */
    int minDuration = Ollivanders2Common.ticksPerMinute; // 1 minute, only applies to non-permanent stationary spells

    /**
     * The maximum radius for this stationary spell type
     */
    int maxRadius = 50;

    /**
     * The minimum radius for this stationary spell type
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
     * Constructs a new StationarySpell cast by a player.
     *
     * <p>Initializes spell-specific configuration including WorldGuard flags and pass-through materials.
     * Sets default success/failure messages for stationary spell casting.</p>
     *
     * @param plugin    a callback to the MC plugin (not null)
     * @param player    the player who cast this spell (not null)
     * @param rightWand the wand correctness factor (not null)
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
     * Creates the stationary spell with duration and radius based on the caster's skill.
     *
     * <p>Called each tick after the spell has hit its target (or immediately for noProjectile spells).
     * Calculates duration and radius, creates the stationary spell instance via {@link #createStationarySpell()},
     * adds it to the spell registry, displays flair effects if configured, and kills this spell.</p>
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
     * Calculates the duration for the stationary spell based on caster experience.
     *
     * <p>Duration formula: (experience × ticksPerSecond) + (ticksPerSecond × durationModifierInSeconds)
     * The result is clamped to [minDuration, maxDuration].</p>
     */
    void setDuration() {
        duration = ((int) usesModifier * Ollivanders2Common.ticksPerSecond) + (Ollivanders2Common.ticksPerSecond * durationModifierInSeconds);

        if (duration > maxDuration)
            duration = maxDuration;
        else if (duration < minDuration)
            duration = minDuration;
    }

    /**
     * Calculates the radius for the stationary spell based on caster experience.
     *
     * <p>Radius formula: floor(experience / radiusModifier)
     * The result is clamped to [minRadius, maxRadius].</p>
     */
    void setRadius() {
        radius = (int) Math.floor(usesModifier / radiusModifier);
        if (radius > maxRadius)
            radius = maxRadius;
        else if (radius < minRadius)
            radius = minRadius;
    }

    /**
     * Creates the stationary spell instance for this spell type.
     *
     * <p>Must be implemented by subclasses to instantiate the appropriate O2StationarySpell subclass.
     * The spell is created with the calculated radius and duration. If this method returns null,
     * the spell cast is considered a failure and a failure message is sent to the caster.</p>
     *
     * @return the stationary spell instance, or null if the spell cannot be created
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
