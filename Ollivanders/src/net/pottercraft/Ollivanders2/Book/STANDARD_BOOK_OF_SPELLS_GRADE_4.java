package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;

/**
 * Standard Book of Spells Grade 4
 *
 * Missing spells:
 * Orchideous - https://github.com/Azami7/Ollivanders2/issues/56
 * Four-Points Spell - https://github.com/Azami7/Ollivanders2/issues/97
 *
 * @since 2.2.4
 * @author Azami7
 */
public class STANDARD_BOOK_OF_SPELLS_GRADE_4 extends Book
{
   public STANDARD_BOOK_OF_SPELLS_GRADE_4 ()
   {
      title = shortTitle = "Standard Book of Spells Grade 4";
      author = "Miranda Goshawk";
      branch = O2MagicBranch.CHARMS;

      spellList.add(Spells.AVIFORS);
      spellList.add(Spells.ACCIO);
      spellList.add(Spells.EBUBLIO);
      spellList.add(Spells.COLOVARIA);
      spellList.add(Spells.ARRESTO_MOMENTUM);
      spellList.add(Spells.FINITE_INCANTATEM);
      spellList.add(Spells.TERGEO);
      spellList.add(Spells.REDUCTO);
      spellList.add(Spells.GLACIUS_DUO);
   }
}

