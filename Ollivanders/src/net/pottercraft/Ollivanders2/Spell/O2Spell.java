package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.O2MagicBranch;

/**
 * Ollivanders2 additions to the original Ollivanders Spell interface.
 *
 * @author Azami7
 */
public interface O2Spell extends Spell
{
   /**
    * Get the description text for this spell.  This can be used to write books, for lessons, or other in-game messages.
    * Description text is required for adding a spell to an Ollivanders2 book.
    *
    * @return the description text for this spell
    */
   String getText ();

   /**
    * Get the flavor text for this spell.  This can be used to make books, lessons, and other descriptions of spells more interesting.
    * Flavor text is optional.
    *
    * @return the flavor text for this spell.
    */
   String getFlavorText ();

   /**
    * Get branch of magic this spell belongs to.
    *
    * @return the branch of magic
    */
   O2MagicBranch getMagicBranch ();
}
