package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * The Book of Potions is a book concerned with Potion-making, written by wizard Zygmunt Budge. Like with Miranda
 * Goshawk's Book of Spells, this potions book has the ability to conjure utensils with which the reader can brew the
 * various potions included.
 *
 * http://harrypotter.wikia.com/wiki/Book_of_Potions
 *
 * @since 2.2.7
 * @author Azami7
 */
public class BOOK_OF_POTIONS extends O2Book
{
   public BOOK_OF_POTIONS(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      // bookType = O2BookType.BOOK_OF_POTIONS;

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
