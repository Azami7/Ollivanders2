package net.pottercraft.Ollivanders2.Player;

/**
 * Represents the year this player is in school.
 *
 * @author autumnwoz
 * @since 2.2.7
 */
public enum Year {
   YEAR_1 (1),
   YEAR_2 (2),
   YEAR_3 (3),
   YEAR_4 (4),
   YEAR_5 (5),
   YEAR_6 (6),
   YEAR_7 (7);

   Integer intValue;

   Year (int value)
   {
      intValue = value;
   }

   /**
    * Get the int value of this year.
    *
    * @return the int value 1-7
    */
   public Integer getIntValue ()
   {
      return intValue;
   }
}
