package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Standard Book of Spells Grade 5
 *
 * @since 2.2.4
 * @author Azami7
 */
public class STANDARD_BOOK_OF_SPELLS_GRADE_5 extends O2Book
{
   public STANDARD_BOOK_OF_SPELLS_GRADE_5(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      bookType = O2BookType.STANDARD_BOOK_OF_SPELLS_GRADE_5;

      spells.add(O2SpellType.SILENCIO);
      spells.add(O2SpellType.MUFFLIATO);
      spells.add(O2SpellType.CONFUNDO);
      // todo substantive charm - https://harrypotter.fandom.com/wiki/Substantive_Charm - maybe make armor or weapons stronger (next level up)
      spells.add(O2SpellType.DELETRIUS);
      spells.add(O2SpellType.STUPEFY);
      // todo Rennervate - https://harrypotter.fandom.com/wiki/Reviving_Spell

      spells.add(O2SpellType.PROTEGO);
      spells.add(O2SpellType.EXPELLIARMUS);
      spells.add(O2SpellType.INCENDIO_DUO);
      spells.add(O2SpellType.ARRESTO_MOMENTUM);
      spells.add(O2SpellType.STUPEFY);
   }
}
