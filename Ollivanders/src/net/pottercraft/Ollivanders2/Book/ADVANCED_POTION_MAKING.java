package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Potion.*;
import net.pottercraft.Ollivanders2.Ollivanders2;

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
   public ADVANCED_POTION_MAKING (Ollivanders2 plugin)
   {
      super(plugin);

      title = shortTitle = "Advanced Potion Making";
      author = "Libatius Borage";
      branch = O2MagicBranch.POTIONS;

      potions.add(O2PotionType.REGENERATION_POTION);
      potions.add(O2PotionType.MEMORY_POTION);

      // draught of living death
      // elixir to induce euphoria
      // Hiccoughing Solution
      // Everlasting Elixirs
      // Felix Felicis
      // Poison Antidote (Golpalott's Third Law)
   }
}
