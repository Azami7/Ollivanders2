package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;

/**
 * Practical Defensive Magic - sent to Harry by Sirius and Lupin in his 5th year
 *
 * @since 2.2.4
 * @author Azami7
 */
public class PRACTICAL_DEFENSIVE_MAGIC extends Book
{
   public PRACTICAL_DEFENSIVE_MAGIC ()
   {
      title = shortTitle = "Practical Defensive Magic";
      author = "Unknown";
      branch = O2MagicBranch.DARK_ARTS;

      spellList.add(Spells.EXPELLIARMUS);
      spellList.add(Spells.PROTEGO);
      spellList.add(Spells.PROTEGO_MAXIMA);
      spellList.add(Spells.MUFFLIATO);
      spellList.add(Spells.ARANIA_EXUMAI);
      spellList.add(Spells.FIANTO_DURI);
      spellList.add(Spells.SCUTO_CONTERAM);
   }
}