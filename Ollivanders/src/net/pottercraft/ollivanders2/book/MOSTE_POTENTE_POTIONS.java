package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import org.jetbrains.annotations.NotNull;

/**
 * Moste Potente Potions is a book by Phineas Bourne that gives instructions on how to brew advanced potions.
 *
 * http://harrypotter.wikia.com/wiki/Moste_Potente_Potions
 *
 * @since 2.2.7
 * @author Azami7
 */
public class MOSTE_POTENTE_POTIONS extends O2Book
{
   public MOSTE_POTENTE_POTIONS(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      bookType = O2BookType.MOSTE_POTENTE_POTIONS;

      potions.add(O2PotionType.BARUFFIOS_BRAIN_ELIXIR);
      potions.add(O2PotionType.WOLFSBANE_POTION);
      potions.add(O2PotionType.REGENERATION_POTION);
      // polyjuice potion
      // laxative potion
   }
}
