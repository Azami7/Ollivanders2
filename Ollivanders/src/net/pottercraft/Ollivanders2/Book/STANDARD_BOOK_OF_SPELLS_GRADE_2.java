package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;
import net.pottercraft.Ollivanders2.Ollivanders2;

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
   public STANDARD_BOOK_OF_SPELLS_GRADE_2 (Ollivanders2 plugin)
   {
      super(plugin);

      title = shortTitle = "Standard Book of Spells Grade 2";
      author = "Miranda Goshawk";
      branch = O2MagicBranch.CHARMS;

      spells.add(Spells.DIFFINDO);
      spells.add(Spells.EXPELLIARMUS);
      spells.add(Spells.ENGORGIO);
      spells.add(Spells.REDUCIO);
      spells.add(Spells.INCENDIO);
      spells.add(Spells.IMMOBULUS);
      spells.add(Spells.FINITE_INCANTATEM);
      spells.add(Spells.OBLIVIATE);
      spells.add(Spells.FLIPENDO);
      //spells.add(Spells.SKURGE);
      //spells.add(Spells.TARANTALLEGRA);
   }
}
