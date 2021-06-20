package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.potion.*;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Advanced Potion-Making is a book written by Libatius Borage. As the title implies this book contains advanced recipes
 * and various other topics related to potion-making. This textbook has been used for decades in the education of young
 * witches and wizards.
 *
 * http://harrypotter.wikia.com/wiki/Advanced_Potion-Making
 *
 * @since 2.2.7
 * @author Azami7
 */
public class ADVANCED_POTION_MAKING extends O2Book
{
   public ADVANCED_POTION_MAKING(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      bookType = O2BookType.ADVANCED_POTION_MAKING;

      potions.add(O2PotionType.MEMORY_POTION);
      potions.add(O2PotionType.WIGGENWELD_POTION);
      potions.add(O2PotionType.DRAUGHT_OF_LIVING_DEATH);
      // elixir to induce euphoria
      // Hiccoughing Solution
      // Everlasting Elixirs
      // Felix Felicis
      // Poison Antidote (Golpalott's Third Law)
   }
}
