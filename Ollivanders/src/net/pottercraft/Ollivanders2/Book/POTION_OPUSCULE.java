package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Potion.*;

/**
 * Potion Opuscule is an elementary book on potions by Jigger.
 *
 * http://harrypotter.wikia.com/wiki/Potion_Opuscule
 *
 * @since 2.2.7
 * @author Azami7
 */
public class POTION_OPUSCULE extends Book
{
   public POTION_OPUSCULE (Ollivanders2 plugin)
   {
      super(plugin);

      title = shortTitle = "Potion Opuscule";
      author = "Arsenius Jigger";
      branch = O2MagicBranch.POTIONS;

      potions.add(O2PotionType.ANIMAGUS_POTION);
      potions.add(O2PotionType.BABBLING_BEVERAGE);
   }
}
