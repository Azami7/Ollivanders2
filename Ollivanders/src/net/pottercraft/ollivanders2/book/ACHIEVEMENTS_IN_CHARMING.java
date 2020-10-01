package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Achievements in Charming - Charms book for 1st year.
 *
 * @since 2.2.4
 * @author Azami7
 */
public class ACHIEVEMENTS_IN_CHARMING extends O2Book
{
   public ACHIEVEMENTS_IN_CHARMING(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      shortTitle = title = "Achievements in Charming";
      author = "Unknown";
      branch = O2MagicBranch.CHARMS;

      spells.add(O2SpellType.LUMOS);
      spells.add(O2SpellType.WINGARDIUM_LEVIOSA);
      spells.add(O2SpellType.SPONGIFY);
      spells.add(O2SpellType.PYROSVESTIRAS);
   }
}
