package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;

/**
 * Standard Book of Spells Grade 1
 *
 * @since 2.2.4
 * @author Azami7
 */
public class STANDARD_BOOK_OF_SPELLS_GRADE_1 extends Book
{
   public STANDARD_BOOK_OF_SPELLS_GRADE_1 ()
   {
      title = shortTitle = "Standard Book of Spells Grade 1";
      author = "Miranda Goshawk";
      branch = O2MagicBranch.CHARMS;

      spellList.add(Spells.COLLOPORTUS);
      spellList.add(Spells.ALOHOMORA);
      spellList.add(Spells.LUMOS);
      spellList.add(Spells.NOX);
      spellList.add(Spells.REPARO);
      spellList.add(Spells.DIFFINDO);
      spellList.add(Spells.WINGARDIUM_LEVIOSA);
      spellList.add(Spells.INCENDIO);
   }
}
