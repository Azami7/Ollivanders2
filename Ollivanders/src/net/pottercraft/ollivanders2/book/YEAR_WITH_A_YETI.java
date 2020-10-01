package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Year with a Yeti - 2nd year Defense Against the Dark Arts book
 *
 * Missing spells:
 * Mimblewimble - https://github.com/Azami7/Ollivanders2/issues/54
 *
 * @since 2.2.4
 * @author Azami7
 */
public class YEAR_WITH_A_YETI extends O2Book
{
   public YEAR_WITH_A_YETI(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      title = shortTitle = "Year with a Yeti";
      author = "Gilderoy Lockhart";
      branch = O2MagicBranch.DARK_ARTS;

      spells.add(O2SpellType.ARANIA_EXUMAI);
      spells.add(O2SpellType.OBLIVIATE);
   }
}
