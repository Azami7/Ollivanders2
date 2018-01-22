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
 * Missing spells:
 * Animagus - https://github.com/Azami7/Ollivanders2/issues/87
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

      spellList.add(Spells.DRACONIFORS);
      spellList.add(Spells.DUCKLIFORS);
      spellList.add(Spells.AVIFORS);
      spellList.add(Spells.EQUUSIFORS);
      spellList.add(Spells.EVANESCO);
      spellList.add(Spells.PIERTOTUM_LOCOMOTOR);
      spellList.add(Spells.DELETRIUS);
      spellList.add(Spells.HERBIFORS);
      spellList.add(Spells.LAPIFORS);
      //spellList.add(Spells.ANIMAGUS);
      //spell 11
      //spell 12
      //spell 13
      //spell 14
   }
}
