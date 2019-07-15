package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.O2Spell;
import net.pottercraft.Ollivanders2.Spell.O2SpellType;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Practical Defensive Magic - sent to Harry by Sirius and Lupin in his 5th year
 *
 * @since 2.2.4
 * @author Azami7
 */
public class PRACTICAL_DEFENSIVE_MAGIC extends O2Book
{
   public PRACTICAL_DEFENSIVE_MAGIC (Ollivanders2 plugin)
   {
      super(plugin);

      title = shortTitle = "Practical Defensive Magic Volume One";
      author = "Unknown";
      branch = O2MagicBranch.DARK_ARTS;

      spells.add(O2SpellType.EXPELLIARMUS);
      spells.add(O2SpellType.PROTEGO);
      spells.add(O2SpellType.PROTEGO_MAXIMA);
      spells.add(O2SpellType.MUFFLIATO);
      spells.add(O2SpellType.ARANIA_EXUMAI);
      spells.add(O2SpellType.FIANTO_DURI);
      spells.add(O2SpellType.PRIOR_INCANTATO);
   }
}