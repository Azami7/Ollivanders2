package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Potion.*;

/**
 * Moste Potente Potions is a book by Phineas Bourne that gives instructions on how to brew advanced potions.
 *
 * http://harrypotter.wikia.com/wiki/Moste_Potente_Potions
 *
 * @since 2.2.7
 * @author Azami7
 */
public class MOSTE_POTENTE_POTIONS extends Book
{
   public MOSTE_POTENTE_POTIONS ()
   {
      title = shortTitle = "Moste Potente Potions";
      author = "Phineas Bourne";
      branch = O2MagicBranch.POTIONS;

      potions.add(new BARUFFIOS_BRAIN_ELIXIR().getName());
      potions.add(new WOLFSBANE_POTION().getName());
      // polyjuice potion
      // laxative potion
   }
}
