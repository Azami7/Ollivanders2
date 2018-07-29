package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;

/**
 * Wanderings with Werewolves - 2nd year Defense Against the Dark Arts book
 *
 * Missing Spells:
 * Rictusempra - https://github.com/Azami7/Ollivanders2/issues/92
 *
 * @author Azami7
 */
public class WANDERINGS_WITH_WEREWOLVES extends Book
{
   public WANDERINGS_WITH_WEREWOLVES ()
   {
      title = shortTitle = "Wanderings with Werewolves";
      author = "Gilderoy Lockhart";
      branch = O2MagicBranch.DARK_ARTS;

      spells.add(Spells.CONFUNDUS_DUO);
      spells.add(Spells.OBLIVIATE);
   }
}
