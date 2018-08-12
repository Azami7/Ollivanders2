package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Advanced Fireworks for Fun and Profit
 *
 * @since 2.2.4
 * @author Azami7
 */
public final class ADVANCED_FIREWORKS extends Book
{
   public ADVANCED_FIREWORKS (Ollivanders2 plugin)
   {
      super(plugin);

      title = "Advanced Fireworks for Fun and Profit";
      shortTitle = "Advanced Fireworks";
      author = "George Weasley";
      branch = O2MagicBranch.CHARMS;

      spells.add(Spells.BOTHYNUS_DUO);
      spells.add(Spells.COMETES_DUO);
      spells.add(Spells.PERICULUM_DUO);
      spells.add(Spells.PORFYRO_ASTERI_DUO);
      spells.add(Spells.VERDIMILLIOUS_DUO);
      spells.add(Spells.BOTHYNUS_TRIA);
      spells.add(Spells.PORFYRO_ASTERI_TRIA);
   }
}
