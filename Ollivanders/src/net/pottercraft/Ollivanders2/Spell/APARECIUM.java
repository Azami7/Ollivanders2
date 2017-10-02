package net.pottercraft.Ollivanders2.Spell;

import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;

/**
 * The Revealing Charm.  Causes any stationary spell objects to flair with an intensity equal to your level
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class APARECIUM extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public APARECIUM ()
   {
      super();

      flavorText.add("The Revealing Charm will reveal invisible ink and messages hidden by magical means. Simply tap a book or parchment with your wand and any hidden message will be revealed. This spell is more than sufficient to overcome the basic concealing charms and so is a favourite of parents and teachers alike.");
      flavorText.add("The Revealing Charm");
      text = "Causes any area spells to reveal their borders.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
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