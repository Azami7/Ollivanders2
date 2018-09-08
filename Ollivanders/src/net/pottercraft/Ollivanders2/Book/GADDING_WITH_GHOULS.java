package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.O2SpellType;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Gadding with Ghouls - 2nd year Defense Against the Dark Arts book
 *
 * @since 2.2.4
 * @author Azami7
 */
public class GADDING_WITH_GHOULS extends O2Book
{
   public GADDING_WITH_GHOULS (Ollivanders2 plugin)
   {
      super(plugin);

      title = shortTitle = "Gadding with Ghouls";
      author = "Gilderoy Lockhart";
      branch = O2MagicBranch.DARK_ARTS;

      spells.add(O2SpellType.MOLLIARE);
      spells.add(O2SpellType.OBLIVIATE);
   }
}
