package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.O2SpellType;
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
public class A_BEGINNERS_GUIDE_TO_TRANSFIGURATION extends O2Book
{
   public A_BEGINNERS_GUIDE_TO_TRANSFIGURATION (Ollivanders2 plugin)
   {
      super(plugin);

      title = "A Beginner's Guide to Transfiguration";
      shortTitle = "Beginners Transfiguration";
      author = "Emeric Switch";
      branch = O2MagicBranch.TRANSFIGURATION;

      spells.add(O2SpellType.DURO);
      spells.add(O2SpellType.FATUUS_AURUM);
      spells.add(O2SpellType.CALAMUS);
      spells.add(O2SpellType.DEPRIMO);
      spells.add(O2SpellType.MULTICORFORS);
      spells.add(O2SpellType.VERA_VERTO);
      spells.add(O2SpellType.SNUFFLIFORS);
   }
}
