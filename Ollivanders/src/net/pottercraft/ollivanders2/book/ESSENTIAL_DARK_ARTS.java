package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Defensive Magical Theory - 5th year defense against the dark arts
 *
 * @since 2.2.4
 * @author Azami7
 */
public class ESSENTIAL_DARK_ARTS extends O2Book
{
   public ESSENTIAL_DARK_ARTS(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      bookType = O2BookType.ESSENTIAL_DARK_ARTS;

      spells.add(O2SpellType.CARPE_RETRACTUM);
      spells.add(O2SpellType.GLACIUS);
      spells.add(O2SpellType.LUMOS_DUO);
      spells.add(O2SpellType.AQUA_ERUCTO);
      spells.add(O2SpellType.GLACIUS_DUO);
      spells.add(O2SpellType.ALARTE_ASCENDARE);
      spells.add(O2SpellType.DISSENDIUM);
      spells.add(O2SpellType.FUMOS_DUO);
   }
}
