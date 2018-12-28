package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.House.O2HouseType;
import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.O2Spell;
import net.pottercraft.Ollivanders2.Spell.O2SpellType;
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
public class STANDARD_BOOK_OF_SPELLS_GRADE_7 extends O2Book
{
   public STANDARD_BOOK_OF_SPELLS_GRADE_7 (Ollivanders2 plugin)
   {
      super(plugin);

      title = shortTitle = "Standard Book of Spells Grade 7";
      author = "Miranda Goshawk";
      branch = O2MagicBranch.CHARMS;

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
