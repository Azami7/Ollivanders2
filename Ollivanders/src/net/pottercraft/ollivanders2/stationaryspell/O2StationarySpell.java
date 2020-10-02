package net.pottercraft.ollivanders2.stationaryspell;

import org.jetbrains.annotations.NotNull;

/**
 * Ollivanders2 additions to the original Ollivanders StationarySpell interface.
 *
 * @author Azami7
 */
public interface O2StationarySpell extends StationarySpell
{
   /**
    * Get the description text for this spell.  This can be used to write books, for lessons, or other in-game messages.
    * Description text is required for adding a spell to an Ollivanders2 book.
    *
    * @return the description text for this spell
    */
   @NotNull
   String getText ();

   /**
    * Get the flavor text for this spell.  This can be used to make books, lessons, and other descriptions of spells more interesting.
    * Flavor text is optional.
    *
    * @return the flavor text for this spell.
    */
   @NotNull
   String getFlavorText ();
}
