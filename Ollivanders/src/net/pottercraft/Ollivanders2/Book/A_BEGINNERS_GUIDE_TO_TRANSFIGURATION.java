package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;

/**
 * A Beginner's Guide to Transfiguration - 1-3 year transfiguration book
 *
 * Topics:
 * Switching
 *
 * @since 2.2.4
 * @author Azami7
 */
public class A_BEGINNERS_GUIDE_TO_TRANSFIGURATION extends Book
{
   public A_BEGINNERS_GUIDE_TO_TRANSFIGURATION ()
   {
      title = "A Beginner's Guide to Transfiguration";
      shortTitle = "Beginners Transfiguration";
      author = "Emeric Switch";
      branch = O2MagicBranch.TRANSFIGURATION;

      spellList.add(Spells.DURO);
      spellList.add(Spells.FATUUS_AURUM);
      spellList.add(Spells.CALAMUS);
      spellList.add(Spells.DEPRIMO);
      spellList.add(Spells.MULTICORFORS);
      spellList.add(Spells.VERA_VERTO);
   }
}
