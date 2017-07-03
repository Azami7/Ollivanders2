package Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.World;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Created by Azami7 on 7/2/17.
 *
 * Creates or ends a storm for a variable duration.
 *
 * @author Azami7
 */
public abstract class MeteolojinxSuper extends SpellProjectile implements Spell
{
   boolean storm = true;

   public MeteolojinxSuper (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

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
