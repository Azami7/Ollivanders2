package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Basic Fireworks
 *
 * @since 2.2.4
 * @author Azami7
 */
public final class BASIC_FIREWORKS extends Book
{
   public BASIC_FIREWORKS (Ollivanders2 plugin)
   {
      super(plugin);

      title = shortTitle = "Basic Fireworks";
      author = "George Weasley";
      branch = O2MagicBranch.CHARMS;

      spells.add(Spells.BOTHYNUS);
      spells.add(Spells.COMETES);
      spells.add(Spells.PERICULUM);
      spells.add(Spells.PORFYRO_ASTERI);
      spells.add(Spells.VERDIMILLIOUS);
   }
}
