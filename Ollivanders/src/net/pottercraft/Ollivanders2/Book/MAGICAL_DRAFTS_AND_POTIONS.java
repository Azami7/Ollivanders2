package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Potion.O2PotionType;

/**
 * Magical Drafts and Potions - OWL potions book
 *
 * @since 2.2.7
 * @author Azami7
 */
public class MAGICAL_DRAFTS_AND_POTIONS extends O2Book
{
   public MAGICAL_DRAFTS_AND_POTIONS (Ollivanders2 plugin)
   {
      super(plugin);

      title = shortTitle = "Magical Drafts and Potions";
      author = "Arsenius Jigger";
      branch = O2MagicBranch.POTIONS;

      potions.add(O2PotionType.ANTIDOTE_POTION);
      potions.add(O2PotionType.WIT_SHARPENING_POTION);
      potions.add(O2PotionType.FORGETFULLNESS_POTION);
      potions.add(O2PotionType.HERBICIDE_POTION);
      potions.add(O2PotionType.WIDEYE_POTION);
      potions.add(O2PotionType.CURE_FOR_BOILS);
      potions.add(O2PotionType.SLEEPING_DRAUGHT);
      // swelling solution
   }
}
