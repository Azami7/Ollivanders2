package Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.World;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Ends a storm for a variable duration
 *
 * @author lownes
 */
public class METEOLOJINX_RECANTO extends SpellProjectile implements Spell
{

   public METEOLOJINX_RECANTO (Ollivanders2 plugin, Player player, Spells name,
                               Double rightWand)
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
         if (!world.hasStorm())
         {
            world.setWeatherDuration((int) (duration + (usesModifier * 1200)));
         }
         else
         {
            duration -= usesModifier * 1200;
            if (duration < 0)
            {
               duration = -duration;
               world.setStorm(false);
            }
            world.setWeatherDuration(duration);
         }
         kill();
      }
   }

}