package net.pottercraft.Ollivanders2.Player;

/**
 * Represents the year this player is in school.
 *
 * @author autumnwoz
 * @since 2.2.7
 */
public enum Year {
   YEAR_1(1, "1st"),
   YEAR_2(2, "2nd"),
   YEAR_3(3, "3rd"),
   YEAR_4(4, "4th"),
   YEAR_5(5, "5th"),
   YEAR_6(6, "6th"),
   YEAR_7(7, "7th");

   Integer intValue;
   String displayText;

   Year (int value, String text)
   {
      intValue = value;
      displayText = text;
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

   /**
    * Get the text for writing this year, example: "3rd year"
    *
    * @return
    */
   public String getDisplayText ()
   {
      return displayText;
   }
}
