package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;
import net.pottercraft.Ollivanders2.Ollivanders2;

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
   public A_BEGINNERS_GUIDE_TO_TRANSFIGURATION (Ollivanders2 plugin)
   {
      super(plugin);

      title = "A Beginner's Guide to Transfiguration";
      shortTitle = "Beginners Transfiguration";
      author = "Emeric Switch";
      branch = O2MagicBranch.TRANSFIGURATION;

      spells.add(Spells.DURO);
      spells.add(Spells.FATUUS_AURUM);
      spells.add(Spells.CALAMUS);
      spells.add(Spells.DEPRIMO);
      spells.add(Spells.MULTICORFORS);
      spells.add(Spells.VERA_VERTO);
      spells.add(Spells.SNUFFLIFORS);
   }
}
