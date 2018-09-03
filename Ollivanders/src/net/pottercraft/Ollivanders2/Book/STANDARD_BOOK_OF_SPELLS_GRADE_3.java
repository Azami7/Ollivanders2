package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.O2SpellType;
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

      spells.add(O2SpellType.EXPELLIARMUS);
      spells.add(O2SpellType.DRACONIFORS);
      spells.add(O2SpellType.IMMOBULUS);
      spells.add(O2SpellType.LUMOS_DUO);
      spells.add(O2SpellType.REPARO);
      spells.add(O2SpellType.CARPE_RETRACTUM);
      spells.add(O2SpellType.PACK);
      spells.add(O2SpellType.LAPIFORS);
      spells.add(O2SpellType.SNUFFLIFORS);
      //10
      //11
   }
}
