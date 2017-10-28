package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;

/**
 * Voyages with Vampires - 2nd year Defense Against the Dark Arts book
 *
 * @since 2.2.4
 * @author Azami7
 */
public class VOYAGES_WITH_VAMPIRES extends Book
{
   public VOYAGES_WITH_VAMPIRES ()
   {
      title = shortTitle = "Voyages with Vampires";
      author = "Gilderoy Lockhart";
      branch = O2MagicBranch.DARK_ARTS;

      spellList.add(Spells.DEPULSO);
   }
}
