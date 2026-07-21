package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for weather-altering spells that start or stop a storm, with higher caster skill bringing the change
 * sooner. Subclasses set {@link #storm} to choose which direction the weather changes.
 */
public abstract class MetelojinxBase extends O2Spell {
    /**
     * True to make a storm, false to clear the weather. Set by subclasses.
     */
    protected boolean storm = true;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public MetelojinxBase(Ollivanders2 plugin) {
        super(plugin);
    }

    /**
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public MetelojinxBase(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        failureMessage = "You failed to change the weather.";
    }

    /**
     * Shortens the time until the desired weather change. If the caster's modifier exceeds the remaining
     * time, the weather changes immediately and the leftover time becomes the new weather duration.
     */
    @Override
    protected void doCheckEffect() {
        kill();

        int duration = world.getWeatherDuration();

        if (!storm && world.hasStorm()) { // stop a storm and the world has a storm
            // reduce the current storm's duration
            duration = duration - (int)(usesModifier/10) * Ollivanders2Common.ticksPerMinute;
            if (duration < 0) {
                // the reduction exceeded the remaining storm time, so end the storm now
                duration = 0;
                world.setStorm(false);
            }

            world.setWeatherDuration(duration);
        }
        else if (storm && !world.hasStorm()) { // start a storm and there is no current storm
            duration = (int)(usesModifier/10) * Ollivanders2Common.ticksPerMinute;
            if (duration < Ollivanders2Common.ticksPerMinute) // min duration of 1 minute
                duration = Ollivanders2Common.ticksPerMinute;

            world.setWeatherDuration(duration);
        }
        else {
            sendFailureMessage();
        }
    }
}
