package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;

/**
 * Holidays with Hags - 2nd year Defense Against the Dark Arts book
 *
 * @since 2.2.4
 * @author Azami7
 */
public class HOLIDAYS_WITH_HAGS extends Book
{
   public HOLIDAYS_WITH_HAGS ()
   {
      title = shortTitle = "Holidays with Hags";
      author = "Gilderoy Lockhart";
      branch = O2MagicBranch.DARK_ARTS;

      spellList.add(Spells.MELOFORS);
   }
}
