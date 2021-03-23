package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Extreme Incantations - Charms book for 2nd year.
 *
 * @since 2.2.4
 * @author Azami7
 */
public class EXTREME_INCANTATIONS extends O2Book
{
   public EXTREME_INCANTATIONS(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      bookType = O2BookType.EXTREME_INCANTATIONS;

      spells.add(O2SpellType.LUMOS_DUO);
      spells.add(O2SpellType.BOMBARDA);
      spells.add(O2SpellType.BOMBARDA_MAXIMA);
      spells.add(O2SpellType.COLOVARIA);
      spells.add(O2SpellType.LUMOS_MAXIMA);
   }
}
