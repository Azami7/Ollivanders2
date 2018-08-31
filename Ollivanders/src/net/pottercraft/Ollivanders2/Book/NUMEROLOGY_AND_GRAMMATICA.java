package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spell.O2SpellType;

/**
 * Numerology and Grammatica - Arithmancy text
 *
 * @since 2.2.4
 * @author Azami7
 */
public class NUMEROLOGY_AND_GRAMMATICA extends O2Book
{
   public NUMEROLOGY_AND_GRAMMATICA (Ollivanders2 plugin)
   {
      super(plugin);

      shortTitle = title = "Numerology and Grammatica";
      author = "Unknown";
      branch = O2MagicBranch.ARITHMANCY;

      openingPage = "The study of Arithmancy is not for the weak of mind. With work and dedication, one can learn the secrets of the Universe by understanding the language of numbers.";

      spells.add(O2SpellType.INFORMOUS);
   }
}
