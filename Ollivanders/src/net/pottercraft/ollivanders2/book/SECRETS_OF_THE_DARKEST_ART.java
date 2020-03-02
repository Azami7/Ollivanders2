package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Secrets of the Darkest Art - The only known book that explains how to make a Horcrux.
 *
 * @since 2.2.4
 * @author Azami7
 */
public class SECRETS_OF_THE_DARKEST_ART extends O2Book
{
   public SECRETS_OF_THE_DARKEST_ART (Ollivanders2 plugin)
   {
      super(plugin);
      
      shortTitle = "Darkest Art";
      title = "Secrets of the Darkest Art";
      author = "Owle Bullock";
      branch = O2MagicBranch.DARK_ARTS;

      spells.add(O2SpellType.ET_INTERFICIAM_ANIMAM_LIGAVERIS);
      spells.add(O2SpellType.VENTO_FOLIO);
      spells.add(O2SpellType.SCUTO_CONTERAM);
   }
}
