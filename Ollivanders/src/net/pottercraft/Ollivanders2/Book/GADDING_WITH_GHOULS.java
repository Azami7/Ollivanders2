package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;

/**
 * Gadding with Ghouls - 2nd year Defense Against the Dark Arts book
 *
 * @author Azami7
 */
public class GADDING_WITH_GHOULS extends Book
{
   public GADDING_WITH_GHOULS ()
   {
      title = shortTitle = "Gadding with Ghouls";
      author = "Gilderoy Lockhart";
      branch = O2MagicBranch.DARK_ARTS;

      spellList.add(Spells.MOLLIARE);
   }
}
