package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Basic Hexes for the Busy and Vexed
 *
 * @since 2.2.4
 * @author Azami7
 */
public class BASIC_HEXES extends O2Book
{
   public BASIC_HEXES (Ollivanders2 plugin)
   {
      super(plugin);

      shortTitle = "Basic Hexes";
      title = "Basic Hexes for the Busy and Vexed";
      author = "Unknown";
      branch = O2MagicBranch.DARK_ARTS;

      spells.add(Spells.MUCUS_AD_NAUSEAM);
      spells.add(Spells.IMPEDIMENTA);
      spells.add(Spells.IMMOBULUS);
      spells.add(Spells.OBSCURO);
      spells.add(Spells.LOQUELA_INEPTIAS);
   }
}
