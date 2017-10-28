package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;

/**
 * Curses and Counter-Curses -
 *
 * @since 2.2.4
 * @author Azami7
 */
public class CURSES_AND_COUNTERCURSES extends Book
{
   public CURSES_AND_COUNTERCURSES ()
   {
      title = shortTitle = "Curses and Counter-Curses";
      author = "Unknown";
      branch = O2MagicBranch.DARK_ARTS;

      spellList.add(Spells.LEVICORPUS);
      spellList.add(Spells.LIBERACORPUS);
      spellList.add(Spells.METELOJINX);
      spellList.add(Spells.METELOJINX_RECANTO);
      spellList.add(Spells.SILENCIO);
   }
}
