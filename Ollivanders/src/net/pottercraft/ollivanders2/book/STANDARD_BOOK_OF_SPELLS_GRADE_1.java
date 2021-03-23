package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Standard Book of Spells Grade 1
 *
 * @since 2.2.4
 * @author Azami7
 */
public class STANDARD_BOOK_OF_SPELLS_GRADE_1 extends O2Book
{
   public STANDARD_BOOK_OF_SPELLS_GRADE_1(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      bookType = O2BookType.STANDARD_BOOK_OF_SPELLS_GRADE_1;

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
