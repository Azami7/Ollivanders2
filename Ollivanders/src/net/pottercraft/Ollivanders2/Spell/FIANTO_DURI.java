package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;

/**
 * Lengthens the duration of shield spells.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class FIANTO_DURI extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public FIANTO_DURI ()
   {
      super();

      flavorText.add("\"Protego Maxima. Fianto Duri. Repello Inimicum.\" - Filius Flitwick");
      flavorText.add("");
      text = "Fianto Duri lengthens the duration of a stationary spell.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public FIANTO_DURI (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
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