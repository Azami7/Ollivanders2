package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Creates or ends a storm for a variable duration.
 */
public abstract class MetelojinxSuper extends O2Spell
{
    /**
     * True to make a storm, false to clear the weather
     */
    boolean storm = true;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public MetelojinxSuper(Ollivanders2 plugin)
    {
        super(plugin);
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public MetelojinxSuper(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);
    }

    @Override
    protected void doCheckEffect()
    {
        kill();

        World world = location.getWorld();
        if (world == null)
        {
            common.printDebugMessage("MetelojinxSuper.doCheckEffect: world is null", null, null, true);
            kill();
            return;
        }

        int duration = world.getWeatherDuration();

        if (!storm && world.hasStorm()) // stop a storm and there is a current storm to stop
            world.setWeatherDuration((int) (duration + (usesModifier * 1200)));
        else if (storm && !world.hasStorm()) // start a storm and there is already a storm
        {
            duration = duration - (int) (usesModifier * 1200);

            if (duration < 0)
            {
                duration = -duration;
                world.setStorm(storm);
            }
            world.setWeatherDuration(duration);
        }
        else
        {
            failureMessage = "You failed to change the weather.";
            sendFailureMessage();
        }
    }
}
