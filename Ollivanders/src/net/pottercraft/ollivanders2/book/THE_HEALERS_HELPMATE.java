package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * The Healer's Helpmate
 *
 * @since 2.2.4
 * @author Azami7
 */
public class THE_HEALERS_HELPMATE extends O2Book
{
   public THE_HEALERS_HELPMATE(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      bookType = O2BookType.THE_HEALERS_HELPMATE;

      spells.add(O2SpellType.AGUAMENTI);
      spells.add(O2SpellType.BRACKIUM_EMENDO);
      spells.add(O2SpellType.EPISKEY);
      potions.add(O2PotionType.COMMON_ANTIDOTE_POTION);
      // wound cleaning potion
      // pepperup potion
      potions.add(O2PotionType.WIDEYE_POTION);
      // burn healing paste
   }
}
