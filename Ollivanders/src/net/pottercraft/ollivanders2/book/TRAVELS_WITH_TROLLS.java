package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Traveling with Trolls - 2nd year Defense Against the Dark Arts book
 *
 * @since 2.2.4
 * @author Azami7
 */
public class TRAVELS_WITH_TROLLS extends O2Book
{
   public TRAVELS_WITH_TROLLS(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      bookType = O2BookType.TRAVELS_WITH_TROLLS;

      spells.add(O2SpellType.VERDIMILLIOUS_DUO);
      spells.add(O2SpellType.OBLIVIATE);
      spells.add(O2SpellType.CONFUNDUS_DUO);
   }
}
