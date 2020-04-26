package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Charming Colors - spells for changing colors
 *
 * @since 2.2.4
 * @author Azami7
 */
public final class CHARMING_COLORS extends O2Book
{
   public CHARMING_COLORS (Ollivanders2 plugin)
   {
      super(plugin);

      shortTitle = title = "Charming Colors";
      author = "Nymphadora Tonks";
      branch = O2MagicBranch.CHARMS;

      openingPage = "\"Mere color, unspoiled by meaning, and unallied with definite form, can speak to the soul in a thousand different ways.\"";

      spells.add(O2SpellType.COLOVARIA);
      spells.add(O2SpellType.COLOVARIA_AURANTIACO);
      spells.add(O2SpellType.COLOVARIA_CAERULUS);
      spells.add(O2SpellType.COLOVARIA_FLAVO);
      spells.add(O2SpellType.COLOVARIA_OSTRUM);
      spells.add(O2SpellType.COLOVARIA_VERIDI);
      spells.add(O2SpellType.COLOVARIA_VERMICULO);
   }
}
