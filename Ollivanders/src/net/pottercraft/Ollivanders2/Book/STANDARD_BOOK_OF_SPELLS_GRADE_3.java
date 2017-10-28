package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;

/**
 * Standard Book of Spells Grade 3
 *
 * Missing spells:
 * Snufflifors - https://github.com/Azami7/Ollivanders2/issues/94
 *
 * @since 2.2.4
 * @author Azami7
 */
public class STANDARD_BOOK_OF_SPELLS_GRADE_3 extends Book
{
   public STANDARD_BOOK_OF_SPELLS_GRADE_3 ()
   {
      title = shortTitle = "Standard Book of Spells Grade 3";
      author = "Miranda Goshawk";
      branch = O2MagicBranch.CHARMS;

      spellList.add(Spells.EXPELLIARMUS);
      spellList.add(Spells.DRACONIFORS);
      spellList.add(Spells.IMMOBULUS);
      spellList.add(Spells.LUMOS_DUO);
      spellList.add(Spells.REPARO);
      spellList.add(Spells.CARPE_RETRACTUM);
      spellList.add(Spells.PACK);
      spellList.add(Spells.LAPIFORS);
      //spellList.add(Spells.SNUFFLIFORS);
   }
}
