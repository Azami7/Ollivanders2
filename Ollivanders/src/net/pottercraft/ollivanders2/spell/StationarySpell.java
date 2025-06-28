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
     * Flair size
     */
    int flairSize = 10;

    /**
     * Is the location for this stationary spell centered at the caster or a projectile target
     */
    boolean centerOnCaster = false;

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
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public StationarySpell(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        branch = O2MagicBranch.CHARMS;

        // world guard flags
        // world guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);

        // pass-through materials
        projectilePassThrough.remove(Material.WATER);

        successMessage = "Stationary spell successfully cast.";
        failureMessage = "Nothing seems to happen.";
    }

    /**
     * Create the stationary spell with duration and radius based on the caster's skill.
     */
    @Override
    protected void doCheckEffect() {
        if (!centerOnCaster && !hasHitTarget())
            return;

        // set duration to be base time plus a modifier seconds per experience level for this spell
        duration = ((int) usesModifier * Ollivanders2Common.ticksPerSecond * durationModifierInSeconds);

        if (duration > maxDuration)
            duration = maxDuration;
        else if (duration < minDuration)
            duration = minDuration;

        radius = (int) usesModifier * radiusModifier;
        if (radius > maxRadius)
            radius = maxRadius;
        else if (radius < minRadius)
            radius = minRadius;

        O2StationarySpell stationarySpell = createStationarySpell();

        if (stationarySpell != null) {
            stationarySpell.flair(flairSize);
            Ollivanders2API.getStationarySpells().addStationarySpell(stationarySpell);

            sendSuccessMessage();
        }
        else
            sendFailureMessage();

        kill();
    }

    /**
     * Create the stationary spell. Has to be overridden by child spells to have effect.
     *
     * @return the stationary spell or null if one is not created.
     */
    @Nullable
    abstract protected O2StationarySpell createStationarySpell();
}
