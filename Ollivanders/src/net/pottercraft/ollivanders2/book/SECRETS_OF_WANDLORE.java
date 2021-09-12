package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;

/**
 * Secrets of Wandlore
 *
 * @since 2.2.4
 * @author Azami7
 */
public final class SECRETS_OF_WANDLORE extends O2Book
{
   public SECRETS_OF_WANDLORE(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      bookType = O2BookType.SECRETS_OF_WANDLORE;

      openingPage = "Wandlore is an ancient, complex, and mysterious branch of magic dealing with the history, abilities, and actions of wands, quasi-sentient magical tools used by wizards and witches to cast spells.";

      spells.add(O2SpellType.FRANGE_LIGNEA);
      spells.add(O2SpellType.LIGATIS_COR);
   }
}
