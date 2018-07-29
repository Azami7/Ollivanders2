package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;

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
public class INTERMEDIATE_TRANSFIGURATION extends Book
{
   public INTERMEDIATE_TRANSFIGURATION ()
   {
      shortTitle = title = "Intermediate Transfiguration";
      author = "Unknown";
      branch = O2MagicBranch.TRANSFIGURATION;

      spells.add(Spells.DRACONIFORS);
      spells.add(Spells.DUCKLIFORS);
      spells.add(Spells.AVIFORS);
      spells.add(Spells.EQUUSIFORS);
      spells.add(Spells.EVANESCO);
      spells.add(Spells.PIERTOTUM_LOCOMOTOR);
      spells.add(Spells.DELETRIUS);
      spells.add(Spells.HERBIFORS);
      spells.add(Spells.LAPIFORS);
      spells.add(Spells.AMATO_ANIMO_ANIMATO_ANIMAGUS);
      //spell 11
   }
}
