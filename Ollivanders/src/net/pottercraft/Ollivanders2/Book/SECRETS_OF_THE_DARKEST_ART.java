package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;

/**
 * Secrets of the Darkest Art - The only known book that explains how to make a Horcrux.
 *
 * @since 2.2.4
 * @author Azami7
 */
public class SECRETS_OF_THE_DARKEST_ART extends Book
{
   public SECRETS_OF_THE_DARKEST_ART ()
   {
      shortTitle = "Darkest Art";
      title = "Secrets of the Darkest Art";
      author = "Owle Bullock";
      branch = O2MagicBranch.DARK_ARTS;

      spellList.add(Spells.ET_INTERFICIAM_ANIMAM_LIGAVERIS);
   }
}
