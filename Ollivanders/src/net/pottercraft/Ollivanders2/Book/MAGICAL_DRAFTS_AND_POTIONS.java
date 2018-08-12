package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Potion.*;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Magical Drafts and Potions - OWL potions book
 *
 * @since 2.2.7
 * @author Azami7
 */
public class MAGICAL_DRAFTS_AND_POTIONS extends Book
{
   public MAGICAL_DRAFTS_AND_POTIONS (Ollivanders2 plugin)
   {
      super(plugin);

      title = shortTitle = "Magical Drafts and Potions";
      author = "Arsenius Jigger";
      branch = O2MagicBranch.POTIONS;

      potions.add(new ANTIDOTE_POTION(p).getName());
      potions.add(new WIT_SHARPENING_POTION(p).getName());
      potions.add(new FORGETFULLNESS_POTION(p).getName());
      potions.add(new HERBICIDE_POTION(p).getName());
      // swelling potion
      // cure for boils
      // sleeping draught
      // swelling solution
      // wideye potion
   }
}
