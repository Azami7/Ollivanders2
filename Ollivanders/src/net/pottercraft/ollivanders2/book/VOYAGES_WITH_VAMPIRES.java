package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Voyages with Vampires - 2nd year Defense Against the Dark Arts book
 *
 * @since 2.2.4
 * @author Azami7
 */
public class VOYAGES_WITH_VAMPIRES extends O2Book
{
   public VOYAGES_WITH_VAMPIRES(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      bookType = O2BookType.VOYAGES_WITH_VAMPIRES;

      spells.add(O2SpellType.DEPULSO);
      spells.add(O2SpellType.OBLIVIATE);
   }
}
