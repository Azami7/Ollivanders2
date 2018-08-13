package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Potion.*;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Magical Drafts and O2Potions - OWL potions book
 *
 * @since 2.2.7
 * @author Azami7
 */
public class MAGICAL_DRAFTS_AND_POTIONS extends Book
{
   public MAGICAL_DRAFTS_AND_POTIONS (Ollivanders2 plugin)
   {
      super(plugin);

      title = shortTitle = "Magical Drafts and O2Potions";
      author = "Arsenius Jigger";
      branch = O2MagicBranch.POTIONS;

      potions.add(O2PotionType.ANTIDOTE_POTION);
      potions.add(O2PotionType.WIT_SHARPENING_POTION);
      potions.add(O2PotionType.FORGETFULLNESS_POTION);
      potions.add(O2PotionType.HERBICIDE_POTION);
      // swelling potion
      // cure for boils
      // sleeping draught
      // swelling solution
      // wideye potion
   }
}
