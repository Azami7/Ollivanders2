package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Magick Moste Evile - O2Book of Dark Magic written in the Middle Ages
 *
 * Missing Spells:
 * Homorphus Charm - https://github.com/Azami7/Ollivanders2/issues/39
 *
 * @since 2.2.4
 * @author Azami7
 */
public class MAGICK_MOSTE_EVILE extends O2Book
{
   public MAGICK_MOSTE_EVILE(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      shortTitle = title = "Magick Moste Evile";
      author = "Godelot";
      branch = O2MagicBranch.DARK_ARTS;

      spells.add(O2SpellType.FIENDFYRE);
      spells.add(O2SpellType.AVADA_KEDAVRA);
      spells.add(O2SpellType.FLAGRANTE);
      spells.add(O2SpellType.LEGILIMENS);
      spells.add(O2SpellType.SCUTO_CONTERAM);
      spells.add(O2SpellType.AMATO_ANIMO_ANIMATO_ANIMAGUS);

      closingPage = "\n\nOf the Horcrux, wickedest of magical inventions, we shall not speak nor give direction.";
   }
}
