package Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Spells;

/**
 * Glacius Tria has the same effect as Glacius but with 4x the duration and radius.
 *
 * @see GlaciusSuper
 * @version Ollivanders2
 * @author Azami7
 */
public class GLACIUS_TRIA extends GlaciusSuper
{
   public GLACIUS_TRIA (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      //4x duration
      strengthModifier = 4;
      //normal radius
      radiusModifier = 2.0;
   }
}