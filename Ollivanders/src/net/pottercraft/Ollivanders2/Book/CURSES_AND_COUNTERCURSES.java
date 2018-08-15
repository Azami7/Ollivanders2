package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Curses and Counter-Curses -
 *
 * @since 2.2.4
 * @author Azami7
 */
public class CURSES_AND_COUNTERCURSES extends O2Book
{
   public CURSES_AND_COUNTERCURSES (Ollivanders2 plugin)
   {
      super(plugin);

      title = shortTitle = "Curses and Counter-Curses";
      author = "Unknown";
      branch = O2MagicBranch.DARK_ARTS;

      spells.add(Spells.LEVICORPUS);
      spells.add(Spells.LIBERACORPUS);
      spells.add(Spells.METELOJINX);
      spells.add(Spells.METELOJINX_RECANTO);
      spells.add(Spells.SILENCIO);
   }
}
