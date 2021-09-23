package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

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
   public ADVANCED_TRANSFIGURATION(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      bookType = O2BookType.ADVANCED_TRANSFIGURATION;

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
      spells.add(O2SpellType.AMATO_ANIMO_ANIMATO_ANIMAGUS);
      //spells.add(O2SpellType.HOMORPHUS);
   }
}
