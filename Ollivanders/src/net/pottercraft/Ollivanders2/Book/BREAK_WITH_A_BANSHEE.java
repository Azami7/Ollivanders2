package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.O2SpellType;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Break with a Banshee - 2nd year Defense Against the Dark Arts book
 *
 * @since 2.2.4
 * @author Azami7
 */
public class BREAK_WITH_A_BANSHEE extends O2Book
{
   public BREAK_WITH_A_BANSHEE (Ollivanders2 plugin)
   {
      super(plugin);

      title = shortTitle = "Break With A Banshee";
      author = "Gilderoy Lockhart";
      branch = O2MagicBranch.DARK_ARTS;

      spells.add(O2SpellType.EXPELLIARMUS);
      spells.add(O2SpellType.OBLIVIATE);
   }
}
