package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;

/**
 * Jinxes for the Jinxed
 *
 * @since 2.2.4
 * @author Azami7
 */
public class JINXES_FOR_THE_JINXED extends Book
{
   public JINXES_FOR_THE_JINXED ()
   {
      title = shortTitle = "Jinxes for the Jinxed";
      author = "Unknown";
      branch = O2MagicBranch.DARK_ARTS;

      spellList.add(Spells.ENTOMORPHIS);
      spellList.add(Spells.IMPEDIMENTA);
      spellList.add(Spells.LEVICORPUS);
      spellList.add(Spells.LACARNUM_INFLAMARI);
      spellList.add(Spells.VENTO_FOLIO);
   }
}
