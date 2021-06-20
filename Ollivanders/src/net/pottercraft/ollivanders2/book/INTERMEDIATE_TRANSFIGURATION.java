package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Intermediate Transfiguration - an O.W.L level transfiguration book
 *
 * Topics:
 * Animal Transfiguration
 * Self-Transformation
 * Vanishing
 *
 * @since 2.2.4
 * @author Azami7
 */
public class INTERMEDIATE_TRANSFIGURATION extends O2Book
{
   public INTERMEDIATE_TRANSFIGURATION(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      bookType = O2BookType.INTERMEDIATE_TRANSFIGURATION;

      spells.add(O2SpellType.DRACONIFORS);
      spells.add(O2SpellType.DUCKLIFORS);
      spells.add(O2SpellType.AVIFORS);
      spells.add(O2SpellType.EQUUSIFORS);
      spells.add(O2SpellType.EVANESCO);
      spells.add(O2SpellType.PIERTOTUM_LOCOMOTOR);
      spells.add(O2SpellType.DELETRIUS);
      spells.add(O2SpellType.HERBIFORS);
      spells.add(O2SpellType.LAPIFORS);
      spells.add(O2SpellType.AMATO_ANIMO_ANIMATO_ANIMAGUS);
      //spell 11
   }
}
