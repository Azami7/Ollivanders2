package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;

/**
 * Advanced Fireworks for Fun and Profit
 *
 * @since 2.2.4
 * @author Azami7
 */
public final class ADVANCED_FIREWORKS extends Book
{
   public ADVANCED_FIREWORKS ()
   {
      title = "Advanced Fireworks for Fun and Profit";
      shortTitle = "Advanced Fireworks";
      author = "Fred and George Weasley";
      branch = O2MagicBranch.CHARMS;

      spellList.add(Spells.BOTHYNUS_DUO);
      spellList.add(Spells.COMETES_DUO);
      spellList.add(Spells.PERICULUM_DUO);
      spellList.add(Spells.PORFYRO_ASTERI_DUO);
      spellList.add(Spells.VERDIMILLIOUS_DUO);
      spellList.add(Spells.BOTHYNUS_TRIA);
      spellList.add(Spells.PORFYRO_ASTERI_TRIA);
   }
}
