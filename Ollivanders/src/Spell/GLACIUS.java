package Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Spells;

/**
 * Glacius will cause a great cold to descend in a radius from it's impact point which freezes blocks. The radius and
 * duration of the freeze depend on your experience.
 *
 * @see GlaciusSuper
 * @author Ollivanders2
 * @author Azami7
 */
public class GLACIUS extends GlaciusSuper
{
   public GLACIUS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      // normal duration
      strengthModifier = 1;
      // half radius
      radiusModifier = 0.5;
   }

}