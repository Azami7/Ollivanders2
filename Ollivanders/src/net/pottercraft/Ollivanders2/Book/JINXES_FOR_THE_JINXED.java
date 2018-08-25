package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Jinxes for the Jinxed
 *
 * @since 2.2.4
 * @author Azami7
 */
public class JINXES_FOR_THE_JINXED extends O2Book
{
   public JINXES_FOR_THE_JINXED (Ollivanders2 plugin)
   {
      super(plugin);

      title = shortTitle = "Jinxes for the Jinxed";
      author = "Unknown";
      branch = O2MagicBranch.DARK_ARTS;

      spells.add(Spells.ENTOMORPHIS);
      spells.add(Spells.IMPEDIMENTA);
      spells.add(Spells.LEVICORPUS);
      spells.add(Spells.LACARNUM_INFLAMARI);
      spells.add(Spells.VENTO_FOLIO);
   }
}
