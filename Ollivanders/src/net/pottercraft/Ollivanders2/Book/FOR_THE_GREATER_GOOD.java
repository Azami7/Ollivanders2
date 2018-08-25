package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spell.Spells;

public class FOR_THE_GREATER_GOOD extends O2Book
{
   public FOR_THE_GREATER_GOOD (Ollivanders2 plugin)
   {
      super(plugin);

      shortTitle = title = "For The Greater Good";
      author = "Gellert Grindelwald";
      branch = O2MagicBranch.DARK_ARTS;

      openingPage = "We've lived in the shadows for far too long, scuttling like rats in the gutter, forced to hide lest we be discovered, forced to conceal our true nature. I refuse to bow down any longer.";

      spells.add(Spells.MORSMORDRE);
      spells.add(Spells.IMMOBULUS);
      spells.add(Spells.LEVICORPUS);
      spells.add(Spells.LEGILIMENS);
      //Crucio
      //Imperio
   }
}
