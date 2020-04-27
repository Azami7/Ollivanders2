package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Curses and Counter-Curses
 * http://harrypotter.wikia.com/wiki/Curses_and_Counter-Curses
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
      author = "Professor Vindictus Viridian";
      branch = O2MagicBranch.DARK_ARTS;

      openingPage = "Bewitch your friends and befuddle your enemies with the latest revenges: Hair loss, Jelly-Legs, Tongue-Tying, and much, much more.";

      spells.add(O2SpellType.LEVICORPUS);
      spells.add(O2SpellType.LIBERACORPUS);
      spells.add(O2SpellType.METELOJINX);
      spells.add(O2SpellType.METELOJINX_RECANTO);
      spells.add(O2SpellType.SILENCIO);
      spells.add(O2SpellType.PETRIFICUS_TOTALUS);
      // jelly legs
      // tongue tying
   }
}
