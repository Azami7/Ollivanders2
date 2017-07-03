package Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Spells;

/**
 * Glacius Tria has the same effect as Glacius but with 2x the duration and radius.
 *
 * @see GlaciusSuper
 * @author Ollivanders2
 * @author Azami7
 */
public class GLACIUS_DUO extends GlaciusSuper
{
   public GLACIUS_DUO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      // double duration
      strengthModifier = 2;
      // normal radius
      radiusModifier = 1.0;
   }
}