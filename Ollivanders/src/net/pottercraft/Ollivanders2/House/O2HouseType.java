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
   RAVENCLAW ("Ravenclaw", ChatColor.DARK_BLUE),
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

      colorPrefixes.put(ChatColor.BLACK, "§0");
      colorPrefixes.put(ChatColor.DARK_BLUE, "§1");
      colorPrefixes.put(ChatColor.DARK_GREEN, "§2");
      colorPrefixes.put(ChatColor.DARK_AQUA, "§3");
      colorPrefixes.put(ChatColor.DARK_RED, "§4");
      colorPrefixes.put(ChatColor.DARK_PURPLE, "§5");
      colorPrefixes.put(ChatColor.GOLD, "§6");
      colorPrefixes.put(ChatColor.GRAY, "§7");
      colorPrefixes.put(ChatColor.DARK_GRAY, "§8");
      colorPrefixes.put(ChatColor.BLUE, "§9");
      colorPrefixes.put(ChatColor.GREEN, "§a");
      colorPrefixes.put(ChatColor.AQUA, "§b");
      colorPrefixes.put(ChatColor.RED, "§c");
      colorPrefixes.put(ChatColor.LIGHT_PURPLE, "§d");
      colorPrefixes.put(ChatColor.YELLOW, "§e");
      colorPrefixes.put(ChatColor.WHITE, "§f");
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
    * Set the score for this house. Should only be done from within the House package.
    * @param s
    */
   void setScore (Integer s)
   {
      score = s;
   }
}
