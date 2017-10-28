package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;

/**
 * Basic Hexes for the Busy and Vexed
 *
 * @since 2.2.4
 * @author Azami7
 */
public class BASIC_HEXES extends Book
{
   public BASIC_HEXES ()
   {
      shortTitle = "Basic Hexes";
      title = "Basic Hexes for the Busy and Vexed";
      author = "Unknown";
      branch = O2MagicBranch.DARK_ARTS;

      spellList.add(Spells.MUCUS_AD_NAUSEAM);
      spellList.add(Spells.IMPEDIMENTA);
      spellList.add(Spells.IMMOBULUS);
      spellList.add(Spells.OBSCURO);
   }
}
