package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Modern Magical Transportation
 *
 * @since 2.2.4
 * @author Azami7
 */
public class MODERN_MAGICAL_TRANSPORTATION extends O2Book
{
   public MODERN_MAGICAL_TRANSPORTATION(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      shortTitle = "Magical Transportation";
      title = "Modern Magical Transportation";
      author = "Azami7";
      branch = O2MagicBranch.CHARMS;

      openingPage = "Understanding magical transportation is important for every witch and wizard.  In this book we will learn three primary means of transport - Brooms, Floo Powder, and Portkeys.";

      spells.add(O2SpellType.VOLATUS);
      spells.add(O2SpellType.ALIQUAM_FLOO);
      spells.add(O2SpellType.PORTUS);
   }
}
