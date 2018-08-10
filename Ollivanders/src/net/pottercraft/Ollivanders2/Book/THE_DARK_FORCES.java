package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * The Dark Forces: A Guide to Self-Protection - 1st year Defense Against the Dark Arts
 *
 * Missing Spells:
 * Flipendo - https://github.com/Azami7/Ollivanders2/issues/37
 *
 * @since 2.2.4
 * @author Azami7
 */
public class THE_DARK_FORCES extends Book
{
   public THE_DARK_FORCES (Ollivanders2 plugin)
   {
      super(plugin);

      shortTitle = "The Dark Forces";
      title = "The Dark Forces: A Guide to Self-Protection";
      author = "Quentin Trimble";
      branch = O2MagicBranch.DARK_ARTS;

      spells.add(Spells.LUMOS);
      spells.add(Spells.FINITE_INCANTATEM);
      spells.add(Spells.FUMOS);
      spells.add(Spells.PERICULUM);
      spells.add(Spells.VERDIMILLIOUS);
      spells.add(Spells.FLIPENDO);
   }
}
