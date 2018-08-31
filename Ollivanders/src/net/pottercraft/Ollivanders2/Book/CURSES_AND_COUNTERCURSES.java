package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.O2SpellType;
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

      spells.add(O2SpellType.LEVICORPUS);
      spells.add(O2SpellType.LIBERACORPUS);
      spells.add(O2SpellType.METELOJINX);
      spells.add(O2SpellType.METELOJINX_RECANTO);
      spells.add(O2SpellType.SILENCIO);
   }
}
