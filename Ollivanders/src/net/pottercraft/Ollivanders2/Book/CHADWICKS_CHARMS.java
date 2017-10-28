package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;

/**
 * Chadwick's Charms - O.W.L level charms book
 *
 * @since 2.2.4
 * @author Azami7
 */
public class CHADWICKS_CHARMS extends Book
{
   public CHADWICKS_CHARMS ()
   {
      shortTitle = "Chadwicks Charms";
      title = "Chadwick's Charms";
      author = "Violeta Stitch";
      branch = O2MagicBranch.CHARMS;

      spellList.add(Spells.ASCENDIO);
      spellList.add(Spells.BOMBARDA_MAXIMA);
      spellList.add(Spells.CRESCERE_PROTEGAT);
      spellList.add(Spells.HORREAT_PROTEGAT);
      spellList.add(Spells.DEFODIO);
      spellList.add(Spells.MOV_FOTIA);
   }
}
