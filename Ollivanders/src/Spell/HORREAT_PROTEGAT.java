package Spell;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;
import net.pottercraft.Ollivanders2.StationarySpellObj;

/**
 * Shrinks a stationarySpellObject's radius. Only the player who created the stationarySpellObject can change it's size.
 *
 * @author lownes
 */
public class HORREAT_PROTEGAT extends SpellProjectile implements Spell
{

   public HORREAT_PROTEGAT (Ollivanders2 plugin, Player player, Spells name,
                            Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      List<StationarySpellObj> inside = new ArrayList<StationarySpellObj>();
      for (StationarySpellObj spell : p.getStationary())
      {
         if (spell.isInside(location) && spell.radius > (int) (10 / usesModifier))
         {
            inside.add(spell);
            kill();
         }
      }
      //int limit = (int)(10/(usesModifier/inside.size()));
      int limit = (int) (10 / usesModifier);
      if (limit < 1)
      {
         limit = 1;
      }
      for (StationarySpellObj spell : inside)
      {
         if (spell.radius > limit && spell.getPlayerUUID().equals(player.getUniqueId()))
         {
            spell.radius--;
            spell.flair(10);
         }
      }
   }

}