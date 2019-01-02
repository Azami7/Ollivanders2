package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Player.O2Player;
import net.pottercraft.Ollivanders2.Potion.O2Potion;
import net.pottercraft.Ollivanders2.Potion.O2PotionType;
import net.pottercraft.Ollivanders2.Spell.O2Spell;
import net.pottercraft.Ollivanders2.Spell.O2SpellType;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * The Healer's Helpmate
 *
 * @since 2.2.4
 * @author Azami7
 */
public class THE_HEALERS_HELPMATE extends O2Book
{
   public THE_HEALERS_HELPMATE (Ollivanders2 plugin)
   {
      super(plugin);

      title = "The Healer's Helpmate";
      shortTitle = "The Healers Helpmate";
      author = "H. Pollingtonious";
      branch = O2MagicBranch.HEALING;

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
