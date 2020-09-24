package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Jinxes for the Jinxed
 * http://harrypotter.wikia.com/wiki/List_of_books
 *
 * @since 2.2.4
 * @author Azami7
 */
public class JINXES_FOR_THE_JINXED extends O2Book
{
   public JINXES_FOR_THE_JINXED(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      title = shortTitle = "Jinxes for the Jinxed";
      author = "Unknown";
      branch = O2MagicBranch.DARK_ARTS;

      openingPage = "Learn some jinxes to add to your arsenal with this handy volume.";

      spells.add(O2SpellType.ENTOMORPHIS);
      spells.add(O2SpellType.IMPEDIMENTA);
      spells.add(O2SpellType.LEVICORPUS);
      spells.add(O2SpellType.LACARNUM_INFLAMARI);
   }
}
