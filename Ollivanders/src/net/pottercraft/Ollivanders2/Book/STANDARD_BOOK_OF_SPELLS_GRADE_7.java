package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Standard Book of Spells Grade 7
 *
 * Missing spells:
 * Prior Incantato - https://github.com/Azami7/Ollivanders2/issues/62
 *
 * @since 2.2.4
 * @author Azami7
 */
public class STANDARD_BOOK_OF_SPELLS_GRADE_7 extends Book
{
   public STANDARD_BOOK_OF_SPELLS_GRADE_7 (Ollivanders2 plugin)
   {
      super(plugin);

      title = shortTitle = "Standard Book of Spells Grade 7";
      author = "Miranda Goshawk";
      branch = O2MagicBranch.CHARMS;

      spells.add(Spells.MUFFLIATO);
      spells.add(Spells.REPELLO_MUGGLETON);
      spells.add(Spells.INFORMOUS);
      spells.add(Spells.GLACIUS_TRIA);
      spells.add(Spells.INCENDIO_TRIA);
   }
}
