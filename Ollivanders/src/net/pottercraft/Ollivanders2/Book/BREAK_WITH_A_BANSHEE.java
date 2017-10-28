package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;

/**
 * Break with a Banshee - 2nd year Defense Against the Dark Arts book
 *
 * @since 2.2.4
 * @author Azami7
 */
public class BREAK_WITH_A_BANSHEE extends Book
{
   public BREAK_WITH_A_BANSHEE ()
   {
      title = shortTitle = "Break With A Banshee";
      author = "Gilderoy Lockhart";
      branch = O2MagicBranch.DARK_ARTS;

      spellList.add(Spells.EXPELLIARMUS);
   }
}
