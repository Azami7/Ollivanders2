package Spell;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;
import net.pottercraft.Ollivanders2.StationarySpellObj;

/**
 * Grows a stationarySpellObject's radius. Only the player who created the stationarySpellObject can change it's radius.
 *
 * @author lownes
 */
public class CRESCERE_PROTEGAT extends SpellProjectile implements Spell
{

   public CRESCERE_PROTEGAT (Ollivanders2 plugin, Player player, Spells name,
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
         if (spell.isInside(location) && spell.radius < (int) usesModifier)
         {
            inside.add(spell);
            kill();
         }
      }
      //int limit = (int)(usesModifier/inside.size());
      int limit = (int) usesModifier;
      for (StationarySpellObj spell : inside)
      {
         if (spell.radius < limit && spell.getPlayerUUID().equals(player.getUniqueId()))
         {
            spell.radius++;
            spell.flair(10);
         }
      }
   }

}