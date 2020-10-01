package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import org.jetbrains.annotations.NotNull;

/**
 * Magical Drafts and Potions - OWL potions book
 *
 * @since 2.2.7
 * @author Azami7
 */
public class MAGICAL_DRAFTS_AND_POTIONS extends O2Book
{
   public MAGICAL_DRAFTS_AND_POTIONS(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      title = shortTitle = "Magical Drafts and Potions";
      author = "Arsenius Jigger";
      branch = O2MagicBranch.POTIONS;

      potions.add(O2PotionType.COMMON_ANTIDOTE_POTION);
      potions.add(O2PotionType.WIT_SHARPENING_POTION);
      potions.add(O2PotionType.FORGETFULLNESS_POTION);
      potions.add(O2PotionType.HERBICIDE_POTION);
      potions.add(O2PotionType.WIDEYE_POTION);
      potions.add(O2PotionType.CURE_FOR_BOILS);
      potions.add(O2PotionType.SLEEPING_DRAUGHT);
      // swelling solution
   }
}
