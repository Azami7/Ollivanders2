package net.pottercraft.Ollivanders2.House;

import org.bukkit.ChatColor;

public enum O2HouseType
{
   HUFFLEPUFF ("Hufflepuff", ChatColor.YELLOW),
   GRYFFINDOR ("Gryffindor", ChatColor.RED),
   RAVENCLAW ("Ravenclaw", ChatColor.BLUE),
   SLYTHERIN ("Slytherin", ChatColor.GREEN);

   private String name;
   private ChatColor color;

   O2HouseType (String name, ChatColor color)
   {
      this.name = name;
      this.color = color;
   }

   public String getName()
   {
      return name;
   }

   public void setName (String n)
   {
      name = n;
   }

   public ChatColor getColor()
   {
      return color;
   }

   public void setColor (ChatColor c)
   {
      color = c;
   }
}
