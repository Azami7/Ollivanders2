package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Chadwick's Charms - O.W.L level charms book
 *
 * @since 2.2.4
 * @author Azami7
 */
public class CHADWICKS_CHARMS_VOLUME_1 extends O2Book
{
   public CHADWICKS_CHARMS_VOLUME_1 (Ollivanders2 plugin)
   {
      super(plugin);

      shortTitle = "Chadwicks Charms Volume 1";
      title = "Chadwick's Charms, Volume 1";
      author = "Chadwick Boot";
      branch = O2MagicBranch.CHARMS;

      spells.add(O2SpellType.ASCENDIO);
      spells.add(O2SpellType.CRESCERE_PROTEGAT);
      spells.add(O2SpellType.HORREAT_PROTEGAT);
      spells.add(O2SpellType.MOV_FOTIA);
      spells.add(O2SpellType.FINESTRA);
   }
}
