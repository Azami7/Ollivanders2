package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Non-cannon book written by George Weasley on firework making.
 *
 * @since 2.2.4
 * @author Azami7
 */
public final class ADVANCED_FIREWORKS extends O2Book
{
   public ADVANCED_FIREWORKS(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      title = "Advanced Fireworks for Fun and Profit";
      shortTitle = "Advanced Fireworks";
      author = "George Weasley";
      branch = O2MagicBranch.CHARMS;

      spells.add(O2SpellType.BOTHYNUS_DUO);
      spells.add(O2SpellType.COMETES_DUO);
      spells.add(O2SpellType.PERICULUM_DUO);
      spells.add(O2SpellType.PORFYRO_ASTERI_DUO);
      spells.add(O2SpellType.VERDIMILLIOUS_DUO);
      spells.add(O2SpellType.BOTHYNUS_TRIA);
      spells.add(O2SpellType.PORFYRO_ASTERI_TRIA);
   }
}
