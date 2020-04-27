package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Traveling with Trolls - 2nd year Defense Against the Dark Arts book
 *
 * @since 2.2.4
 * @author Azami7
 */
public class TRAVELS_WITH_TROLLS extends O2Book
{
   public TRAVELS_WITH_TROLLS (Ollivanders2 plugin)
   {
      super(plugin);

      title = shortTitle = "Traveling with Trolls";
      author = "Gilderoy Lockhart";
      branch = O2MagicBranch.DARK_ARTS;

      spells.add(O2SpellType.VERDIMILLIOUS_DUO);
      spells.add(O2SpellType.OBLIVIATE);
      spells.add(O2SpellType.CONFUNDUS_DUO);
   }
}
