package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.potion.*;
import org.jetbrains.annotations.NotNull;

/**
 * Potion Opuscule is an elementary book on potions by Jigger.
 *
 * http://harrypotter.wikia.com/wiki/Potion_Opuscule
 *
 * @since 2.2.7
 * @author Azami7
 */
public class POTION_OPUSCULE extends O2Book
{
   public POTION_OPUSCULE(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      bookType = O2BookType.POTION_OPUSCULE;

      potions.add(O2PotionType.ANIMAGUS_POTION);
      potions.add(O2PotionType.BABBLING_BEVERAGE);
   }
}
