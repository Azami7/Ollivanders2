package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;

/**
 * Basic Fireworks
 *
 * @since 2.2.4
 * @author Azami7
 */
public final class BASIC_FIREWORKS extends Book
{
   public BASIC_FIREWORKS ()
   {
      title = shortTitle = "Basic Fireworks";
      author = "Fred and George Weasley";
      branch = O2MagicBranch.CHARMS;

      spellList.add(Spells.BOTHYNUS);
      spellList.add(Spells.COMETES);
      spellList.add(Spells.PERICULUM);
      spellList.add(Spells.PORFYRO_ASTERI);
      spellList.add(Spells.VERDIMILLIOUS);
   }
}
