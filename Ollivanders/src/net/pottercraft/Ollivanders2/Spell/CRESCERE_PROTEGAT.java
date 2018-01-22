package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;

/**
 * Grows a Stationary Spell's radius. Only the player who created the Stationary Spell can change it's radius.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class CRESCERE_PROTEGAT extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public CRESCERE_PROTEGAT ()
   {
      super();
      text = "Grows a Stationary Spell's radius. Only the player who created the Stationary Spell can change it's radius.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public CRESCERE_PROTEGAT (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      List<StationarySpellObj> inside = new ArrayList<>();
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