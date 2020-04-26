package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Quintessence: A Quest - 5th year Charms book
 *
 * @since 2.2.4
 * @author Azami7
 */
public class QUINTESSENCE_A_QUEST extends O2Book
{
   public QUINTESSENCE_A_QUEST (Ollivanders2 plugin)
   {
      super(plugin);

      shortTitle = "Quintessence";
      title = "Quintessence: A Quest";
      author = "Unknown";
      branch = O2MagicBranch.CHARMS;

      spells.add(O2SpellType.PROTEGO_TOTALUM);
      spells.add(O2SpellType.PROTEGO_HORRIBILIS);
      spells.add(O2SpellType.NULLUM_APPAREBIT);
      spells.add(O2SpellType.NULLUM_EVANESCUNT);
      spells.add(O2SpellType.APARECIUM);
      spells.add(O2SpellType.CONFUNDO);
      spells.add(O2SpellType.POINT_ME);
   }
}