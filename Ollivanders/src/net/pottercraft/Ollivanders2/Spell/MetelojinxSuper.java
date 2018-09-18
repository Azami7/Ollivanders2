package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Creates or ends a storm for a variable duration.
 *
 * @author Azami7
 */
public abstract class MetelojinxSuper extends Charms
{
   boolean storm = true;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public MetelojinxSuper ()
   {
      super();
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public MetelojinxSuper (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
   }

   @Override
   public void checkEffect ()
   {
      move();
      if (location.getY() > 256)
      {
         World world = location.getWorld();
         int duration = world.getWeatherDuration();
         if (storm == world.hasStorm()) // storm and hasStorm() are either both true or both false
         {
            world.setWeatherDuration((int) (duration + (usesModifier * 1200)));
         }
         else // storm and hasStorm() are not the same
         {
            duration -= usesModifier * 1200;
            if (duration < 0)
            {
               duration = -duration;
               world.setStorm(storm);
            }
            world.setWeatherDuration(duration);
         }
         kill();
      }
   }
}
