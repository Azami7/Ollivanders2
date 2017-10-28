package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;

/**
 * Modern Magical Transportation
 *
 * @since 2.2.4
 * @author Azami7
 */
public class MODERN_MAGICAL_TRANSPORTATION extends Book
{
   public MODERN_MAGICAL_TRANSPORTATION ()
   {
      shortTitle = "Magical Transportation";
      title = "Modern Magical Transportation";
      author = "Azami7";
      branch = O2MagicBranch.CHARMS;

      openingPage = "Understanding magical transportation is important for every witch and wizard.  In this book we will learn three primary means of transport - Brooms, Floo Powder, and Portkeys.";

      spellList.add(Spells.VOLATUS);
      spellList.add(Spells.ALIQUAM_FLOO);
      spellList.add(Spells.PORTUS);
   }
}
