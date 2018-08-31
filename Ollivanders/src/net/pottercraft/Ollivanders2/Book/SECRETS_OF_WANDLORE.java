package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spell.O2SpellType;

/**
 * Secrets of Wandlore
 *
 * @since 2.2.4
 * @author Azami7
 */
public final class SECRETS_OF_WANDLORE extends O2Book
{
   public SECRETS_OF_WANDLORE (Ollivanders2 plugin)
   {
      super(plugin);

      shortTitle = title = "Secrets of Wandlore";
      author = "Geraint Ollivander";
      branch = O2MagicBranch.CHARMS;

      openingPage = "Wandlore is an ancient, complex, and mysterious branch of magic dealing with the history, abilities, and actions of wands, quasi-sentient magical tools used by wizards and witches to cast spells.";

      spells.add(O2SpellType.FRANGE_LIGNEA);
      spells.add(O2SpellType.LIGATIS_COR);
   }
}
