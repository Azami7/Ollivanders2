package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Gadding with Ghouls - 2nd year Defense Against the Dark Arts book
 *
 * @since 2.2.4
 * @author Azami7
 */
public class GADDING_WITH_GHOULS extends O2Book
{
   public GADDING_WITH_GHOULS(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      bookType = O2BookType.GADDING_WITH_GHOULS;

      spells.add(O2SpellType.MOLLIARE);
      spells.add(O2SpellType.OBLIVIATE);
   }
}
