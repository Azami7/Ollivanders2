package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Chadwick's Charms - O.W.L level charms book
 *
 * @since 2.2.4
 * @author Azami7
 */
public class CHADWICKS_CHARMS_VOLUME_1 extends O2Book
{
   public CHADWICKS_CHARMS_VOLUME_1(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      bookType = O2BookType.CHADWICKS_CHARMS_VOLUME_1;

      spells.add(O2SpellType.ASCENDIO);
      spells.add(O2SpellType.CRESCERE_PROTEGAT);
      spells.add(O2SpellType.HORREAT_PROTEGAT);
      spells.add(O2SpellType.MOV_FOTIA);
      spells.add(O2SpellType.FINESTRA);
      spells.add(O2SpellType.FATUUS_AURUM);
      spells.add(O2SpellType.ABERTO);
   }
}
