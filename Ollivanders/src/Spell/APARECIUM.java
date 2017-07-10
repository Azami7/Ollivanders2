package Spell;

import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;
import net.pottercraft.Ollivanders2.StationarySpellObj;

/**
 * Spell which causes any stationary spell objects to flair with
 * an intensity equal to your level
 *
 * @author lownes
 */
public class APARECIUM extends SpellProjectile implements Spell
{
   public APARECIUM (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      List<StationarySpellObj> stationaries = p.checkForStationary(location);
      for (StationarySpellObj stationary : stationaries)
      {
         //stationary.flair(Math.sqrt(p.getSpellNum(player, name))/rightWand);
         int level = (int) usesModifier;
         if (level > 10)
         {
            level = 10;
         }
         stationary.flair(level);
      }
   }
}