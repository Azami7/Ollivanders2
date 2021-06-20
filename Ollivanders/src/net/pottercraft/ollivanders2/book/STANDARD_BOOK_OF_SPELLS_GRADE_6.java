package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Standard Book of Spells Grade 6
 *
 * Missing spells:
 * Expecto Patronum - https://github.com/Azami7/Ollivanders2/issues/32
 *
 * @since 2.2.4
 * @author Azami7
 */
public class STANDARD_BOOK_OF_SPELLS_GRADE_6 extends O2Book
{
   public STANDARD_BOOK_OF_SPELLS_GRADE_6(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      bookType = O2BookType.STANDARD_BOOK_OF_SPELLS_GRADE_6;

      spells.add(O2SpellType.EPISKEY);
      spells.add(O2SpellType.HERBIVICUS);
      spells.add(O2SpellType.EVANESCO);
      spells.add(O2SpellType.AGUAMENTI);
      spells.add(O2SpellType.ALARTE_ASCENDARE);
      spells.add(O2SpellType.LUMOS_SOLEM);
      spells.add(O2SpellType.PARTIS_TEMPORUS);
      //spells.add(O2SpellType.EXPECTO_PATRONUM);
      //9
      //10
      //11
   }
}
