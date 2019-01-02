package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spell.O2SpellType;

/**
 * Hellenistic astrology book that became the basis for all western astrology, added as a "rare" book for
 * more powerful spells.
 *
 * @author Azami7
 * @since 2.2.9
 */
public class TETRABIBLIOS extends O2Book
{
   public TETRABIBLIOS (Ollivanders2 plugin)
   {
      super(plugin);

      title = shortTitle = "Tetrabilios";
      author = "Ptolemy";
      branch = O2MagicBranch.DIVINATION;

      spells.add(O2SpellType.ASTROLOGIA);
      spells.add(O2SpellType.PROPHETEIA);
   }
}
