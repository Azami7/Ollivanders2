package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;

/**
 * Numerology and Grammatica - Arithmancy text
 *
 * @since 2.2.4
 * @author Azami7
 */
public class NUMEROLOGY_AND_GRAMMATICA extends Book
{
   public NUMEROLOGY_AND_GRAMMATICA ()
   {
      shortTitle = title = "Numerology and Grammatica";
      author = "Unknown";
      branch = O2MagicBranch.ARITHMANCY;

      openingPage = "The study of Arithmancy is not for the weak of mind. With work and dedication, one can learn the secrets of the Universe by understanding the language of numbers.";

      spellList.add(Spells.INFORMOUS);
   }
}
