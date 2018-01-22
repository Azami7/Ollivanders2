package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;

/**
 * A Guide to Advanced Transfiguration - N.E.W.T level Transfiguration book
 *
 * Topics:
 * Conjuration
 * Human Transfiguration
 * Untransfiguration
 *
 * Missing Spells:
 * Animagus - https://github.com/Azami7/Ollivanders2/issues/87
 * Homorphus Charm - https://github.com/Azami7/Ollivanders2/issues/39
 *
 * @since 2.2.4
 * @author Azami7
 */
public class ADVANCED_TRANSFIGURATION extends Book
{
   public ADVANCED_TRANSFIGURATION ()
   {
      shortTitle = "Advanced Transfiguration";
      title = "A Guide to Advanced Transfiguration";
      author = "Unknown";
      branch = O2MagicBranch.TRANSFIGURATION;

      spellList.add(Spells.INCARNATIO_DEVITO);
      spellList.add(Spells.INCARNATIO_EQUUS);
      spellList.add(Spells.INCARNATIO_FELIS);
      spellList.add(Spells.INCARNATIO_LAMA);
      spellList.add(Spells.INCARNATIO_LUPI);
      spellList.add(Spells.INCARNATIO_PORCILLI);
      spellList.add(Spells.INCARNATIO_URSUS);
      spellList.add(Spells.INCARNATIO_VACCULA);
      spellList.add(Spells.GEMINO);
      spellList.add(Spells.REPARIFARGE);
      spellList.add(Spells.AVIS);
      //spellList.add(Spells.HOMORPHUS);
      //spell 13
      //spell 14
   }
}
