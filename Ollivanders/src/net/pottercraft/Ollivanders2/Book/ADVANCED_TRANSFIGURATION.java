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

      spells.add(Spells.INCARNATIO_DEVITO);
      spells.add(Spells.INCARNATIO_EQUUS);
      spells.add(Spells.INCARNATIO_FELIS);
      spells.add(Spells.INCARNATIO_LAMA);
      spells.add(Spells.INCARNATIO_LUPI);
      spells.add(Spells.INCARNATIO_PORCILLI);
      spells.add(Spells.INCARNATIO_URSUS);
      spells.add(Spells.INCARNATIO_VACCULA);
      spells.add(Spells.GEMINO);
      spells.add(Spells.REPARIFARGE);
      //spells.add(Spells.HOMORPHUS);
   }
}
