package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Magick Moste Evile - Book of Dark Magic written in the Middle Ages
 *
 * Missing Spells:
 * Animagus - https://github.com/Azami7/Ollivanders2/issues/87
 * Homorphus Charm - https://github.com/Azami7/Ollivanders2/issues/39
 *
 * @since 2.2.4
 * @author Azami7
 */
public class MAGICK_MOSTE_EVILE extends Book
{
   public MAGICK_MOSTE_EVILE (Ollivanders2 plugin)
   {
      super(plugin);

      shortTitle = title = "Magick Moste Evile";
      author = "Godelot";
      branch = O2MagicBranch.DARK_ARTS;

      spells.add(Spells.FIENDFYRE);
      spells.add(Spells.AVADA_KEDAVRA);
      spells.add(Spells.FLAGRANTE);
      spells.add(Spells.LEGILIMENS);

      closingPage = "\n\nOf the Horcrux, wickedest of magical inventions, we shall not speak nor give direction.";
   }
}
