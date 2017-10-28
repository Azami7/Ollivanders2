package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;

/**
 * World of Color - spells for changing colors
 *
 * @since 2.2.4
 * @author Azami7
 */
public final class WORLD_OF_COLOR extends Book
{
   public WORLD_OF_COLOR ()
   {
      shortTitle = title = "World of Color";
      author = "Andromeda Tonks";
      branch = O2MagicBranch.CHARMS;

      openingPage = "\"Mere color, unspoiled by meaning, and unallied with definite form, can speak to the soul in a thousand different ways.\"";

      spellList.add(Spells.COLORO_AURANTIACO);
      spellList.add(Spells.COLORO_CAERULUS);
      spellList.add(Spells.COLORO_FLAVO);
      spellList.add(Spells.COLORO_OSTRUM);
      spellList.add(Spells.COLORO_VERIDI);
      spellList.add(Spells.COLORO_VERMICULO);
   }
}
