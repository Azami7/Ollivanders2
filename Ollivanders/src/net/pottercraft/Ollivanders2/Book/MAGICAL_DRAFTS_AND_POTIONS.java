package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Potion.*;

/**
 * Magical Drafts and Potions - 1st year potions book
 *
 * @since 2.2.7
 * @author Azami7
 */
public class MAGICAL_DRAFTS_AND_POTIONS extends Book
{
   public MAGICAL_DRAFTS_AND_POTIONS ()
   {
      title = shortTitle = "Magical Drafts and Potions";
      author = "Arsenius Jigger";
      branch = O2MagicBranch.POTIONS;

      potions.add(new WIT_SHARPENING_POTION(p).getName());
      // forgetfulness potion
      // swelling potion
      // cure for boils
      // herbicide potion
      // sleeping draught
      // swelling solution
      // wideye potion
   }
}
