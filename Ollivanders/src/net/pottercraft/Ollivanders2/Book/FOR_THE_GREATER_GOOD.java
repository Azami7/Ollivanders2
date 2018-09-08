package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spell.O2SpellType;

/**
 * Non-cannon book written by Gellert Grindelwald that is the only location of Morsmordre appears, which makes it
 * Grindelwald's mark that Voldemort took for his own purposes.
 *
 * @author Azami7
 * @since 2.2.8
 */
public class FOR_THE_GREATER_GOOD extends O2Book
{
   public FOR_THE_GREATER_GOOD (Ollivanders2 plugin)
   {
      super(plugin);

      shortTitle = title = "For The Greater Good";
      author = "Gellert Grindelwald";
      branch = O2MagicBranch.DARK_ARTS;

      openingPage = "We've lived in the shadows for far too long, scuttling like rats in the gutter, forced to hide lest we be discovered, forced to conceal our true nature. I refuse to bow down any longer.";

      spells.add(O2SpellType.MORSMORDRE);
      spells.add(O2SpellType.IMMOBULUS);
      spells.add(O2SpellType.LEVICORPUS);
      spells.add(O2SpellType.LEGILIMENS);
      //Crucio
      //Imperio
   }
}
