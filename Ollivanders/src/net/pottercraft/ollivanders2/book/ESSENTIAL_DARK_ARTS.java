package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * The Essential Defence Against the Dark Arts. 3rd - 4th year defense against the dark arts
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

      // todo Riddikulus - https://harrypotter.fandom.com/wiki/Boggart-Banishing_Spell
      spells.add(O2SpellType.LUMOS_DUO);
      spells.add(O2SpellType.INCENDIO_DUO);
      spells.add(O2SpellType.IMMOBULUS);
      // todo spell that slows/injures skeletons
      spells.add(O2SpellType.ARANIA_EXUMAI);

      // todo salvio hexia - https://harrypotter.fandom.com/wiki/Hex-deflection
      spells.add(O2SpellType.ALARTE_ASCENDARE);
      spells.add(O2SpellType.AQUA_ERUCTO);
      spells.add(O2SpellType.FUMOS_DUO);
      // todo knockback wolf
      // todo set fire to zombie (short duration)
      // todo kill small slime
   }
}
