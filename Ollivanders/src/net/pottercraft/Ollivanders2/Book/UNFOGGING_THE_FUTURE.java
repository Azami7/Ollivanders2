package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spell.O2Spell;
import net.pottercraft.Ollivanders2.Spell.O2SpellType;

/**
 * Unfogging the Future - 3rd and 4th year Divination textbook
 * http://harrypotter.wikia.com/wiki/Unfogging_the_Future
 *
 * @author Azami7
 * @since 2.2.9
 */
public class UNFOGGING_THE_FUTURE extends O2Book
{
   public UNFOGGING_THE_FUTURE (Ollivanders2 plugin)
   {
      super(plugin);

      title = shortTitle = "Unfogging the Future";
      author = "Cassandra Vablatsky";
      branch = O2MagicBranch.DIVINATION;

      spells.add(O2SpellType.ASTROLOGIA);
      // Palmistry
      // Tessomancy
      spells.add(O2SpellType.INTUEOR);
      // Bird Entrails
   }
}
