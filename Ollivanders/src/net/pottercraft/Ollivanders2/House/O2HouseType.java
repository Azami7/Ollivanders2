package net.pottercraft.Ollivanders2.House;

import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

/**
 * House types
 *
 * @author Azami7
 * @since 2.2.8
 */
public enum O2HouseType
{
   GRYFFINDOR ("Gryffindor", ChatColor.DARK_RED),
   HUFFLEPUFF ("Hufflepuff", ChatColor.GOLD),
   RAVENCLAW ("Ravenclaw", ChatColor.BLUE),
   SLYTHERIN ("Slytherin", ChatColor.DARK_GREEN);

   private String name;
   private String prefix;
   private ChatColor color;
   private Integer score;

   private Map<ChatColor, String> colorPrefixes = new HashMap<ChatColor, String>() {{
      put(ChatColor.DARK_BLUE, "§1");
      put(ChatColor.DARK_GREEN, "§2");
      put(ChatColor.DARK_AQUA, "§3");
      put(ChatColor.DARK_RED, "§4");
      put(ChatColor.DARK_PURPLE, "§5");
      put(ChatColor.GOLD, "§6");
      put(ChatColor.GRAY, "§7");
      put(ChatColor.DARK_GRAY, "§8");
      put(ChatColor.BLUE, "§9");
      put(ChatColor.GREEN, "§a");
      put(ChatColor.AQUA, "§b");
      put(ChatColor.RED, "§c");
      put(ChatColor.LIGHT_PURPLE, "§d");
      put(ChatColor.YELLOW, "§e");
      put(ChatColor.WHITE, "§f");
   }};

   /**
    * Constructor
    *
    * @param name the display name for this house
    * @param color the chat color for this house
    */
   O2HouseType (String name, ChatColor color)
   {
      this.name = name;
      this.color = color;
      prefix = colorPrefixes.get(color);
      score = 0;
   }

   /**
    * Get the display name for this house
    *
    * @return the name of this house
    */
   public String getName()
   {
      return name;
   }

   /**
    * Set the display name for this house. Should only be done from within the House package.
    *
    * @param n the display name for this house
    */
   void setName (String n)
   {
      name = n;
   }

   /**
    * Get the chat color for this house
    *
    * @return the ChatColor for this house
    */
   public ChatColor getColor()
   {
      return color;
   }

   /**
    * Get the string prefix for this house color
    *
    * @return the string prefix that sets this color
    */
   public String getColorPrefix ()
   {
      return prefix;
   }

   /**
    * Set the color for this house. Should only be done from within the House package. No change is
    * made if the string is not a valid ChatColor value.
    *
    * @param name the color as a string
    */
   void setColor (String name)
   {
      ChatColor c = null;

      try
      {
         c = ChatColor.valueOf(name);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

      if (c != null)
         color = c;

      prefix = colorPrefixes.get(color);
   }

   /**
    * Get the current score for this house.
    *
    * @return the score for this house
    */
   public Integer getScore ()
   {
      return score;
   }

   /**
    * Get text for displaying the ranking of a specific house in the house cup.
    *
    * @param houseType the house to get text for, cannot be null
    * @return a string like "in 4th place" or "tied for 3rd place"
    */
   public static String getHousePlaceTxt (O2HouseType houseType)
   {
      if (houseType == null)
         return null;

      int score = houseType.getScore();
      boolean tied = false;
      int place = 1;

      for (O2HouseType type : O2HouseType.values())
      {
         if (type == houseType)
            continue;

         if (type.getScore() > score)
            place ++;
         else if (type.getScore() == score)
            tied = true;
      }

      StringBuilder placeString = new StringBuilder();
      if (tied)
         placeString.append("tied for ");
      else
         placeString.append("in ");

      if (place == 1)
         placeString.append("1st ");
      else if (place == 2)
         placeString.append("2nd ");
      else if (place == 3)
         placeString.append("3rd ");
      else
         placeString.append("4th ");

      placeString.append("place");

      return placeString.toString();
   }

   /**
    * Set the score for this house. Should only be done from within the House package.
    * @param s the score to set
    */
   void setScore (Integer s)
   {
      score = s;
   }
}