package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Achievements in Charming - Charms book for 1st year.
 *
 * @since 2.2.4
 * @author Azami7
 */
public class ACHIEVEMENTS_IN_CHARMING extends O2Book
{
   public ACHIEVEMENTS_IN_CHARMING (Ollivanders2 plugin)
   {
      super(plugin);

      shortTitle = title = "Achievements in Charming";
      author = "Unknown";
      branch = O2MagicBranch.CHARMS;

      spells.add(Spells.LUMOS);
      spells.add(Spells.WINGARDIUM_LEVIOSA);
      spells.add(Spells.SPONGIFY);
   }
}
