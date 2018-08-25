package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Defensive Magical Theory - 5th year defense against the dark arts
 *
 * @since 2.2.4
 * @author Azami7
 */
public class ESSENTIAL_DARK_ARTS extends O2Book
{
   public ESSENTIAL_DARK_ARTS (Ollivanders2 plugin)
   {
      super(plugin);

      shortTitle = "Essential Dark Arts Defence";
      title = "The Essential Defence Against the Dark Arts";
      author = "Arsenius Jigger";
      branch = O2MagicBranch.DARK_ARTS;

      spells.add(Spells.CARPE_RETRACTUM);
      spells.add(Spells.GLACIUS);
      spells.add(Spells.LUMOS_DUO);
      spells.add(Spells.AQUA_ERUCTO);
      spells.add(Spells.GLACIUS_DUO);
      spells.add(Spells.ALARTE_ASCENDARE);
      spells.add(Spells.DISSENDIUM);
      spells.add(Spells.FUMOS_DUO);
   }
}
