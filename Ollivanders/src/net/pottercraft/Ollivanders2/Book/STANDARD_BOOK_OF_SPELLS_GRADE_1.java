package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Standard O2Book of Spells Grade 1
 *
 * @since 2.2.4
 * @author Azami7
 */
public class STANDARD_BOOK_OF_SPELLS_GRADE_1 extends O2Book
{
   public STANDARD_BOOK_OF_SPELLS_GRADE_1 (Ollivanders2 plugin)
   {
      super(plugin);

      title = shortTitle = "Standard O2Book of Spells Grade 1";
      author = "Miranda Goshawk";
      branch = O2MagicBranch.CHARMS;

      spells.add(Spells.COLLOPORTUS);
      spells.add(Spells.ALOHOMORA);
      spells.add(Spells.LUMOS);
      spells.add(Spells.NOX);
      spells.add(Spells.REPARO);
      spells.add(Spells.DIFFINDO);
      spells.add(Spells.WINGARDIUM_LEVIOSA);
      spells.add(Spells.INCENDIO);
   }
}
