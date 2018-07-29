package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Potion.*;

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
public class ADVANCED_POTION_MAKING extends Book
{
   public ADVANCED_POTION_MAKING ()
   {
      title = shortTitle = "Advanced Potion Making";
      author = "Libatius Borage";
      branch = O2MagicBranch.POTIONS;

      potions.add(new REGENERATION_POTION().getName());
      potions.add(new MEMORY_POTION().getName());

      // draught of living death
      // elixir to induce euphoria
      // Hiccoughing Solution
      // Everlasting Elixirs
      // Felix Felicis
      // Poison Antidote (Golpalott's Third Law)

   }
}
