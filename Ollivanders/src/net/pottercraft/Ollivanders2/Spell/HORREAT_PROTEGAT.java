package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;

/**
 * Shrinks a stationarySpellObject's radius. Only the player who created the stationarySpellObject can change it's size.
 *
 * @author lownes
 * @author Azami7
 */
public final class HORREAT_PROTEGAT extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public HORREAT_PROTEGAT ()
   {
      super();

      flavorText.add("The Spell-Reduction Charm");
      text = "Horreat Protegat will shrink a stationary spell's radius. Only the creator of the stationary spell can affect it with this spell.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public HORREAT_PROTEGAT (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   @Override
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