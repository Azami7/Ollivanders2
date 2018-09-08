package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.O2SpellType;
import net.pottercraft.Ollivanders2.Ollivanders2;

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
   public WANDERINGS_WITH_WEREWOLVES (Ollivanders2 plugin)
   {
      super(plugin);

      title = shortTitle = "Wanderings with Werewolves";
      author = "Gilderoy Lockhart";
      branch = O2MagicBranch.DARK_ARTS;

      spells.add(O2SpellType.CONFUNDUS_DUO);
      spells.add(O2SpellType.OBLIVIATE);
   }
}
