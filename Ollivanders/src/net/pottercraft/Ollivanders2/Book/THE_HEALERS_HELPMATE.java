package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;

/**
 * The Healer's Helpmate
 *
 * @since 2.2.4
 * @author Azami7
 */
public class THE_HEALERS_HELPMATE extends Book
{
   public THE_HEALERS_HELPMATE ()
   {
      title = "The Healer's Helpmate";
      shortTitle = "The Healers Helpmate";
      author = "H. Pollingtonious";
      branch = O2MagicBranch.HEALING;

      spellList.add(Spells.AGUAMENTI);
      spellList.add(Spells.BRACKIUM_EMENDO);
      spellList.add(Spells.EPISKEY);
   }
}
