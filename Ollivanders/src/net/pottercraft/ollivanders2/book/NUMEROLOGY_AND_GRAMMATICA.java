package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;

/**
 * Numerology and Grammatica - Arithmancy text
 *
 * @since 2.2.4
 * @author Azami7
 */
public class NUMEROLOGY_AND_GRAMMATICA extends O2Book
{
   public NUMEROLOGY_AND_GRAMMATICA(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      bookType = O2BookType.NUMEROLOGY_AND_GRAMMATICA;

      openingPage = "The study of Arithmancy is not for the weak of mind. With work and dedication, one can learn the secrets of the Universe by understanding the language of numbers.";

      spells.add(O2SpellType.INFORMOUS);
   }
}
