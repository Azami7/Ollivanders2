package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * The O2Book of Potions is a book concerned with Potion-making, written by wizard Zygmunt Budge. Like with Miranda
 * Goshawk's O2Book of Spells, this potions book has the ability to conjure utensils with which the reader can brew the
 * various potions included.
 *
 * http://harrypotter.wikia.com/wiki/Book_of_Potions
 *
 * @since 2.2.7
 * @author Azami7
 */
public class BOOK_OF_POTIONS extends O2Book
{
   public BOOK_OF_POTIONS (Ollivanders2 plugin)
   {
      super(plugin);

      title = shortTitle = "Book Of O2Potions";
      author = "Zygmunt Budge";
      branch = O2MagicBranch.POTIONS;

      openingPage = "You, young potioneer, hold in your hands my masterpiece. With it, my place in history is assured. -Zygmunt Budge";

      // polyjuice
      // Beautification Potion
      // cure for boils
      // Doxycide
      // Felix Felicis
      // Laughing Potion
      // Shrinking Solution
      // Sleeping Potion
   }
}
