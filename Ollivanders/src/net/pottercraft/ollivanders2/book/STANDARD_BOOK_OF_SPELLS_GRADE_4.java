package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Standard Book of Spells Grade 4
 *
 * Missing spells:
 * Orchideous - https://github.com/Azami7/Ollivanders2/issues/56
 * Four-Points Spell - https://github.com/Azami7/Ollivanders2/issues/97
 *
 * @since 2.2.4
 * @author Azami7
 */
public class STANDARD_BOOK_OF_SPELLS_GRADE_4 extends O2Book
{
   public STANDARD_BOOK_OF_SPELLS_GRADE_4(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      title = shortTitle = "Standard Book of Spells Grade 4";
      author = "Miranda Goshawk";
      branch = O2MagicBranch.CHARMS;

      spells.add(O2SpellType.AVIFORS);
      spells.add(O2SpellType.ACCIO);
      spells.add(O2SpellType.EBUBLIO);
      spells.add(O2SpellType.COLOVARIA);
      spells.add(O2SpellType.ARRESTO_MOMENTUM);
      spells.add(O2SpellType.FINITE_INCANTATEM);
      spells.add(O2SpellType.TERGEO);
      spells.add(O2SpellType.REDUCTO);
      spells.add(O2SpellType.GLACIUS_DUO);
      //10
      //11
   }
}

