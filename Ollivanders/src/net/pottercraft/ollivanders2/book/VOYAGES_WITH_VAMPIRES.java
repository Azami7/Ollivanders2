package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Voyages with Vampires - 2nd year Defense Against the Dark Arts book
 *
 * @since 2.2.4
 * @author Azami7
 */
public class VOYAGES_WITH_VAMPIRES extends O2Book
{
   public VOYAGES_WITH_VAMPIRES (Ollivanders2 plugin)
   {
      super(plugin);

      title = shortTitle = "Voyages with Vampires";
      author = "Gilderoy Lockhart";
      branch = O2MagicBranch.DARK_ARTS;
      
      spells.add(O2SpellType.DEPULSO);
      spells.add(O2SpellType.OBLIVIATE);
   }
}
