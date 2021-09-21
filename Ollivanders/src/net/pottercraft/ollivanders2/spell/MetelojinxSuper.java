package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Creates or ends a storm for a variable duration.
 *
 * @author Azami7
 */
public abstract class MetelojinxSuper extends O2Spell
{
   boolean storm = true;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
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
      if (location.getY() > 256)
      {
         World world = location.getWorld();
         if (world == null)
         {
            common.printDebugMessage("MetelojinxSuper.doCheckEffect: world is null", null, null, true);
            kill();
            return;
         }

         int duration = world.getWeatherDuration();

         if (storm == world.hasStorm()) // storm and hasStorm() are either both true or both false
         {
            world.setWeatherDuration((int) (duration + (usesModifier * 1200)));
         }
         else // storm and hasStorm() are not the same
         {
            duration = duration - (int)(usesModifier * 1200);

            if (duration < 0)
            {
               duration = -duration;
               world.setStorm(storm);
            }
            world.setWeatherDuration(duration);
         }

         kill();
      }

      if (hasHitTarget())
         kill();
   }
}
