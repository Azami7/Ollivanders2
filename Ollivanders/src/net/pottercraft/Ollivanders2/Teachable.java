package net.pottercraft.Ollivanders2;

/**
 * Interface for all teachable magic. The functions in this interface can be used to write books, for lessons, or other in-game messages.
 */
public interface Teachable
{
   /**
    * Get the name for this magic. This should not be null.
    *
    * @return the name of this magic
    */
   String getName ();

   /**
    * Get the description text for this magic. This should not be null.
    *
    * @return the description text for this teachable magic
    */
   String getText ();

   /**
    * Get the flavor text for this magic.  This can be used to make descriptions more interesting.
    * Flavor text is optional.
    *
    * @return the flavor text for this teachable magic or null if there is no flavor text
    */
   String getFlavorText ();

   /**
    * Get branch of magic this belongs to.
    *
    * @return the branch of magic
    */
   O2MagicBranch getMagicBranch ();
}
