package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;

/**
 * Quintessence: A Quest - 5th year Charms book
 *
 * @since 2.2.4
 * @author Azami7
 */
public class QUINTESSENCE_A_QUEST extends Book
{
   public QUINTESSENCE_A_QUEST ()
   {
      shortTitle = "Quintessence";
      title = "Quintessence: A Quest";
      author = "Unknown";
      branch = O2MagicBranch.CHARMS;

      spells.add(Spells.PROTEGO_TOTALUM);
      spells.add(Spells.PROTEGO_HORRIBILIS);
      spells.add(Spells.NULLUM_APPAREBIT);
      spells.add(Spells.NULLUM_EVANESCUNT);
      spells.add(Spells.APARECIUM);
      spells.add(Spells.CONFUNDO);
   }
}
