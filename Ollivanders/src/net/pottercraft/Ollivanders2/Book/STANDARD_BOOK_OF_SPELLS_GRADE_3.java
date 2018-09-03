package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Standard Book of Spells Grade 3
 *
 * Missing spells:
 * Snufflifors - https://github.com/Azami7/Ollivanders2/issues/94
 *
 * @since 2.2.4
 * @author Azami7
 */
public class STANDARD_BOOK_OF_SPELLS_GRADE_3 extends O2Book
{
   public STANDARD_BOOK_OF_SPELLS_GRADE_3 (Ollivanders2 plugin)
   {
      super(plugin);

      title = shortTitle = "Standard Book of Spells Grade 3";
      author = "Miranda Goshawk";
      branch = O2MagicBranch.CHARMS;

      spells.add(Spells.EXPELLIARMUS);
      spells.add(Spells.DRACONIFORS);
      spells.add(Spells.IMMOBULUS);
      spells.add(Spells.LUMOS_DUO);
      spells.add(Spells.REPARO);
      spells.add(Spells.CARPE_RETRACTUM);
      spells.add(Spells.PACK);
      spells.add(Spells.LAPIFORS);
      spells.add(Spells.SNUFFLIFORS);
      //10
      //11
   }
}
