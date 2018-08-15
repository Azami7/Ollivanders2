package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Extreme Incantations - Charms book for 2nd year.
 *
 * @since 2.2.4
 * @author Azami7
 */
public class EXTREME_INCANTATIONS extends O2Book
{
   public EXTREME_INCANTATIONS (Ollivanders2 plugin)
   {
      super(plugin);

      shortTitle = title = "Extreme Incantations";
      author = "Violeta Stitch";
      branch = O2MagicBranch.CHARMS;

      spells.add(Spells.LUMOS_DUO);
      spells.add(Spells.BOMBARDA);
      spells.add(Spells.COLOVARIA);
      spells.add(Spells.LUMOS_MAXIMA);
   }
}
