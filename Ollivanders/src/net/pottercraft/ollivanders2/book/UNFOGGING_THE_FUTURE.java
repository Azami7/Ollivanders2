package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;

/**
 * Unfogging the Future - 3rd and 4th year Divination textbook
 * http://harrypotter.wikia.com/wiki/Unfogging_the_Future
 *
 * @author Azami7
 * @since 2.2.9
 */
public class UNFOGGING_THE_FUTURE extends O2Book
{
   public UNFOGGING_THE_FUTURE(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      bookType = O2BookType.UNFOGGING_THE_FUTURE;

      spells.add(O2SpellType.ASTROLOGIA);
      // Palmistry
      spells.add(O2SpellType.BAO_ZHONG_CHA);
      spells.add(O2SpellType.INTUEOR);
      // Bird Entrails
      spells.add(O2SpellType.CHARTIA);
   }
}
