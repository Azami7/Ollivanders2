package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;

/**
 * Extreme Incantations - Charms book for 2nd year.
 *
 * @since 2.2.4
 * @author Azami7
 */
public class EXTREME_INCANTATIONS extends Book
{
   public EXTREME_INCANTATIONS ()
   {
      shortTitle = title = "Extreme Incantations";
      author = "Violeta Stitch";
      branch = O2MagicBranch.CHARMS;

      spellList.add(Spells.LUMOS_DUO);
      spellList.add(Spells.BOMBARDA);
      spellList.add(Spells.COLOVARIA);
      spellList.add(Spells.LUMOS_MAXIMA);
   }
}
