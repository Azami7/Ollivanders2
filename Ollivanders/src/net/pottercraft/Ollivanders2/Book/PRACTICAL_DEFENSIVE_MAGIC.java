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

      spells.add(Spells.EXPELLIARMUS);
      spells.add(Spells.PROTEGO);
      spells.add(Spells.PROTEGO_MAXIMA);
      spells.add(Spells.MUFFLIATO);
      spells.add(Spells.ARANIA_EXUMAI);
      spells.add(Spells.FIANTO_DURI);
      spells.add(Spells.SCUTO_CONTERAM);
   }
}