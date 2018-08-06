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

      spells.add(Spells.MUCUS_AD_NAUSEAM);
      spells.add(Spells.IMPEDIMENTA);
      spells.add(Spells.IMMOBULUS);
      spells.add(Spells.OBSCURO);
      spells.add(Spells.LOQUELA_INEPTIAS);
   }
}
