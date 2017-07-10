package Spell;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;
import net.pottercraft.Ollivanders2.StationarySpellObj;

/**
 * Lengthens the duration of shield spells
 *
 * @author lownes
 */
public class FIANTO_DURI extends SpellProjectile implements Spell
{
   public FIANTO_DURI (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      List<StationarySpellObj> inside = new ArrayList<StationarySpellObj>();
      for (StationarySpellObj spell : p.getStationary())
      {
         if (spell.isInside(location))
         {
            inside.add(spell);
            kill();
         }
      }
      int addedAmount = (int) ((usesModifier * 1200) / inside.size());
      for (StationarySpellObj spell : inside)
      {
         spell.duration += addedAmount;
         spell.flair(10);
      }
   }
}