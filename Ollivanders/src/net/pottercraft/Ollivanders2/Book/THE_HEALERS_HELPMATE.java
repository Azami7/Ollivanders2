package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;
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

      spells.add(Spells.AGUAMENTI);
      spells.add(Spells.BRACKIUM_EMENDO);
      spells.add(Spells.EPISKEY);
   }
}
