package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Standard O2Book of Spells Grade 6
 *
 * Missing spells:
 * Expecto Patronum - https://github.com/Azami7/Ollivanders2/issues/32
 *
 * @since 2.2.4
 * @author Azami7
 */
public class STANDARD_BOOK_OF_SPELLS_GRADE_6 extends O2Book
{
   public STANDARD_BOOK_OF_SPELLS_GRADE_6 (Ollivanders2 plugin)
   {
      super(plugin);

      title = shortTitle = "Standard O2Book of Spells Grade 6";
      author = "Miranda Goshawk";
      branch = O2MagicBranch.CHARMS;

      spells.add(Spells.EPISKEY);
      spells.add(Spells.HERBIVICUS);
      spells.add(Spells.EVANESCO);
      spells.add(Spells.AGUAMENTI);
      spells.add(Spells.ALARTE_ASCENDARE);
      spells.add(Spells.LUMOS_SOLEM);
      spells.add(Spells.PARTIS_TEMPORUS);
      //spells.add(Spells.EXPECTO_PATRONUM);
   }
}
