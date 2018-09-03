package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.O2SpellType;
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
public class THE_DARK_FORCES extends O2Book
{
   public THE_DARK_FORCES (Ollivanders2 plugin)
   {
      super(plugin);

      shortTitle = "The Dark Forces";
      title = "The Dark Forces: A Guide to Self-Protection";
      author = "Quentin Trimble";
      branch = O2MagicBranch.DARK_ARTS;

      spells.add(O2SpellType.LUMOS);
      spells.add(O2SpellType.FINITE_INCANTATEM);
      spells.add(O2SpellType.FUMOS);
      spells.add(O2SpellType.PERICULUM);
      spells.add(O2SpellType.VERDIMILLIOUS);
      spells.add(O2SpellType.FLIPENDO);
   }
}
