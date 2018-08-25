package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Potion.*;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Moste Potente O2Potions is a book by Phineas Bourne that gives instructions on how to brew advanced potions.
 *
 * http://harrypotter.wikia.com/wiki/Moste_Potente_Potions
 *
 * @since 2.2.7
 * @author Azami7
 */
public class MOSTE_POTENTE_POTIONS extends O2Book
{
   public MOSTE_POTENTE_POTIONS (Ollivanders2 plugin)
   {
      super(plugin);

      title = shortTitle = "Moste Potente O2Potions";
      author = "Phineas Bourne";
      branch = O2MagicBranch.POTIONS;

      potions.add(O2PotionType.BARUFFIOS_BRAIN_ELIXIR);
      potions.add(O2PotionType.WOLFSBANE_POTION);
      // polyjuice potion
      // laxative potion
   }
}
