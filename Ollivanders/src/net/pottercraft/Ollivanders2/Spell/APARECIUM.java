package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
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

      spellType = O2SpellType.APARECIUM;

      flavorText = new ArrayList<String>() {{
         add("The Revealing Charm will reveal invisible ink and messages hidden by magical means. Simply tap a book or parchment with your wand and any hidden message will be revealed. This spell is more than sufficient to overcome the basic concealing charms and so is a favourite of parents and teachers alike.");
         add("The Revealing Charm");
      }};

      text = "Causes any area spells to reveal their borders.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public APARECIUM (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.APARECIUM;
      setUsesModifier();
   }

   public void checkEffect ()
   {
      move();
      List<StationarySpellObj> stationaries = p.stationarySpells.getStationarySpellsAtLocation(location);
      for (StationarySpellObj stationary : stationaries)
      {
         int level = (int) usesModifier;
         if (level > 10)
         {
            level = 10;
         }
         stationary.flair(level);
      }
   }
}