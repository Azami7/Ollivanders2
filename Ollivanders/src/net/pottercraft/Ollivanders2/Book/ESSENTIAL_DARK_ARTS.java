package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;

/**
 * Defensive Magical Theory - 5th year defense against the dark arts
 *
 * @since 2.2.4
 * @author Azami7
 */
public class ESSENTIAL_DARK_ARTS extends Book
{
   public ESSENTIAL_DARK_ARTS ()
   {
      shortTitle = "Essential Defense";
      title = "The Essential Defence Against the Dark Arts";
      author = "Arsenius Jigger";
      branch = O2MagicBranch.DARK_ARTS;

      spellList.add(Spells.CARPE_RETRACTUM);
      spellList.add(Spells.GLACIUS);
      spellList.add(Spells.LUMOS_DUO);
      spellList.add(Spells.AQUA_ERUCTO);
      spellList.add(Spells.GLACIUS_DUO);
      spellList.add(Spells.ALARTE_ASCENDARE);
      spellList.add(Spells.DISSENDIUM);
      spellList.add(Spells.FUMOS_DUO);
   }
}
