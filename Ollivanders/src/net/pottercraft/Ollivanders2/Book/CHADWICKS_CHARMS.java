package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Chadwick's Charms - O.W.L level charms book
 *
 * @since 2.2.4
 * @author Azami7
 */
public class CHADWICKS_CHARMS extends O2Book
{
   public CHADWICKS_CHARMS (Ollivanders2 plugin)
   {
      super(plugin);

      shortTitle = "Chadwicks Charms";
      title = "Chadwick's Charms";
      author = "Violeta Stitch";
      branch = O2MagicBranch.CHARMS;

      spells.add(Spells.ASCENDIO);
      spells.add(Spells.BOMBARDA_MAXIMA);
      spells.add(Spells.CRESCERE_PROTEGAT);
      spells.add(Spells.HORREAT_PROTEGAT);
      spells.add(Spells.DEFODIO);
      spells.add(Spells.MOV_FOTIA);
      spells.add(Spells.CONFUNDUS_DUO);
      spells.add(Spells.FINESTRA);
   }
}
