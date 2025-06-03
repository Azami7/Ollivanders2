package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * The Dark Forces: A Guide to Self-Protection - 1st and 2nd year Defense Against the Dark Arts
 * https://harrypotter.fandom.com/wiki/The_Dark_Forces:_A_Guide_to_Self-Protection
 *
 * @since 2.2.4
 * @author Azami7
 */
public class THE_DARK_FORCES extends O2Book
{
   public THE_DARK_FORCES(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      bookType = O2BookType.THE_DARK_FORCES;

      spells.add(O2SpellType.LUMOS);
      spells.add(O2SpellType.NOX);
      spells.add(O2SpellType.FLIPENDO);
      spells.add(O2SpellType.FUMOS);
      spells.add(O2SpellType.VERDIMILLIOUS);
      spells.add(O2SpellType.MUCUS_AD_NAUSEAM);
      //todo add vermillious - https://harrypotter.fandom.com/wiki/Red_Sparks
      spells.add(O2SpellType.PERICULUM); // todo remove periculum - not in books, replace with vermillious

      spells.add(O2SpellType.EXPELLIARMUS);
      spells.add(O2SpellType.FINITE_INCANTATEM);
      // todo Verdimillious duo - https://harrypotter.fandom.com/wiki/Verdimillious_Duo_Spell
      // todo vermillious duo - https://harrypotter.fandom.com/wiki/Vermillious_Duo
      // todo vermillious tria - https://harrypotter.fandom.com/wiki/Vermillious_Tria
      spells.add(O2SpellType.PETRIFICUS_TOTALUS);
      spells.add(O2SpellType.SPONGIFY);
   }
}
