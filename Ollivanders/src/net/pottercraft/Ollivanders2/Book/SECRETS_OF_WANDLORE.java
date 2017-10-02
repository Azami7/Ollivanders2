package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;

/**
 * Secrets of Wandlore
 *
 * @author Azami7
 */
public final class SECRETS_OF_WANDLORE extends Book
{
   public SECRETS_OF_WANDLORE ()
   {
      shortTitle = title = "Secrets of Wandlore";
      author = "Geraint Ollivander";
      branch = O2MagicBranch.CHARMS;

      openingPage = "Wandlore is an ancient, complex, and mysterious branch of magic dealing with the history, abilities, and actions of wands, quasi-sentient magical tools used by wizards and witches to cast spells.";

      spellList.add(Spells.FRANGE_LIGNEA);
      spellList.add(Spells.LIGATIS_COR);
   }
}
