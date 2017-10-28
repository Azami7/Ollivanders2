package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;

/**
 * Standard Book of Spells Grade 2
 *
 * Missing spells:
 * Skurge - https://github.com/Azami7/Ollivanders2/issues/72
 * Tarantallegra - https://github.com/Azami7/Ollivanders2/issues/77
 *
 * @since 2.2.4
 * @author Azami7
 */
public class STANDARD_BOOK_OF_SPELLS_GRADE_2 extends Book
{
   public STANDARD_BOOK_OF_SPELLS_GRADE_2 ()
   {
      title = shortTitle = "Standard Book of Spells Grade 2";
      author = "Miranda Goshawk";
      branch = O2MagicBranch.CHARMS;

      spellList.add(Spells.DIFFINDO);
      spellList.add(Spells.EXPELLIARMUS);
      spellList.add(Spells.ENGORGIO);
      spellList.add(Spells.REDUCIO);
      spellList.add(Spells.INCENDIO);
      spellList.add(Spells.IMMOBULUS);
      spellList.add(Spells.FINITE_INCANTATEM);
      spellList.add(Spells.OBLIVIATE);
      spellList.add(Spells.FLIPENDO);
      //spellList.add(Spells.SKURGE);
      //spellList.add(Spells.TARANTALLEGRA);
   }
}
