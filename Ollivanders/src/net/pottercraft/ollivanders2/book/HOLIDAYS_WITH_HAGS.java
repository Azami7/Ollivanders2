package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Holidays with Hags - 2nd year Defense Against the Dark Arts book
 *
 * @since 2.2.4
 * @author Azami7
 */
public class HOLIDAYS_WITH_HAGS extends O2Book
{
   public HOLIDAYS_WITH_HAGS(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      bookType = O2BookType.HOLIDAYS_WITH_HAGS;

      spells.add(O2SpellType.MELOFORS);
      spells.add(O2SpellType.OBLIVIATE);
   }
}
