package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Standard Book of Spells Grade 7
 *
 * Missing spells:
 * Prior Incantato - https://github.com/Azami7/Ollivanders2/issues/62
 *
 * @since 2.2.4
 * @author Azami7
 */
public class STANDARD_BOOK_OF_SPELLS_GRADE_7 extends O2Book
{
   public STANDARD_BOOK_OF_SPELLS_GRADE_7(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      bookType = O2BookType.STANDARD_BOOK_OF_SPELLS_GRADE_7;

      spells.add(O2SpellType.MUFFLIATO);
      spells.add(O2SpellType.REPELLO_MUGGLETON);
      spells.add(O2SpellType.INFORMOUS);
      spells.add(O2SpellType.GLACIUS_TRIA);
      spells.add(O2SpellType.INCENDIO_TRIA);
      spells.add(O2SpellType.DEFODIO);
      spells.add(O2SpellType.APPARATE);
      //8
      //9
      //10
      //11
   }
}
