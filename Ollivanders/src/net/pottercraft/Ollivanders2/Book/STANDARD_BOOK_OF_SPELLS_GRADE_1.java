package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.O2SpellType;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Standard O2Book of O2SpellType Grade 1
 *
 * @since 2.2.4
 * @author Azami7
 */
public class STANDARD_BOOK_OF_SPELLS_GRADE_1 extends O2Book
{
   public STANDARD_BOOK_OF_SPELLS_GRADE_1 (Ollivanders2 plugin)
   {
      super(plugin);

      title = shortTitle = "Standard O2Book of O2SpellType Grade 1";
      author = "Miranda Goshawk";
      branch = O2MagicBranch.CHARMS;

      spells.add(O2SpellType.COLLOPORTUS);
      spells.add(O2SpellType.ALOHOMORA);
      spells.add(O2SpellType.LUMOS);
      spells.add(O2SpellType.NOX);
      spells.add(O2SpellType.REPARO);
      spells.add(O2SpellType.DIFFINDO);
      spells.add(O2SpellType.WINGARDIUM_LEVIOSA);
      spells.add(O2SpellType.INCENDIO);
      //9
      //10
      //11
   }
}
