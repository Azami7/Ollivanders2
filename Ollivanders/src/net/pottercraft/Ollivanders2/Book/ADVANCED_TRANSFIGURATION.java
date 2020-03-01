package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.O2SpellType;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * A Guide to Advanced Transfiguration - N.E.W.T level Transfiguration book
 *
 * Topics:
 * Conjuration
 * Human Transfiguration
 * Untransfiguration
 *
 * Missing Spell:
 * Homorphus Charm - https://github.com/Azami7/Ollivanders2/issues/39
 *
 * @since 2.2.4
 * @author Azami7
 */
public class ADVANCED_TRANSFIGURATION extends O2Book
{
   public ADVANCED_TRANSFIGURATION (Ollivanders2 plugin)
   {
      super(plugin);

      shortTitle = "Advanced Transfiguration";
      title = "A Guide to Advanced Transfiguration";
      author = "Unknown";
      branch = O2MagicBranch.TRANSFIGURATION;

      spells.add(O2SpellType.INCARNATIO_DEVITO);
      spells.add(O2SpellType.INCARNATIO_EQUUS);
      spells.add(O2SpellType.INCARNATIO_FELIS);
      spells.add(O2SpellType.INCARNATIO_LAMA);
      spells.add(O2SpellType.INCARNATIO_LUPI);
      spells.add(O2SpellType.INCARNATIO_PORCILLI);
      spells.add(O2SpellType.INCARNATIO_URSUS);
      spells.add(O2SpellType.INCARNATIO_VACCULA);
      spells.add(O2SpellType.GEMINIO);
      spells.add(O2SpellType.REPARIFARGE);
      //spells.add(O2SpellType.HOMORPHUS);
   }
}
