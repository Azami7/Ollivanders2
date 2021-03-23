package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Wanderings with Werewolves - 2nd year Defense Against the Dark Arts book
 *
 * Missing Spells:
 * Rictusempra - https://github.com/Azami7/Ollivanders2/issues/92
 *
 * @author Azami7
 */
public class WANDERINGS_WITH_WEREWOLVES extends O2Book
{
   public WANDERINGS_WITH_WEREWOLVES(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      bookType = O2BookType.WANDERINGS_WITH_WEREWOLVES;

      spells.add(O2SpellType.CONFUNDUS_DUO);
      spells.add(O2SpellType.OBLIVIATE);
   }
}
