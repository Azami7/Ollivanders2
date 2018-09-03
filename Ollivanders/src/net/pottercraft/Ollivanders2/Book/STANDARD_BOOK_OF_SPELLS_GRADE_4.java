package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;
import net.pottercraft.Ollivanders2.Ollivanders2;

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
public class STANDARD_BOOK_OF_SPELLS_GRADE_4 extends O2Book
{
   public STANDARD_BOOK_OF_SPELLS_GRADE_4 (Ollivanders2 plugin)
   {
      super(plugin);

      title = shortTitle = "Standard Book of Spells Grade 4";
      author = "Miranda Goshawk";
      branch = O2MagicBranch.CHARMS;

      spells.add(Spells.AVIFORS);
      spells.add(Spells.ACCIO);
      spells.add(Spells.EBUBLIO);
      spells.add(Spells.COLOVARIA);
      spells.add(Spells.ARRESTO_MOMENTUM);
      spells.add(Spells.FINITE_INCANTATEM);
      spells.add(Spells.TERGEO);
      spells.add(Spells.REDUCTO);
      spells.add(Spells.GLACIUS_DUO);
      //10
      //11
   }
}

