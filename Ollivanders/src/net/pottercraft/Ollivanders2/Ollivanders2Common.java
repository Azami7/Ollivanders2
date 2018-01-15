package net.pottercraft.Ollivanders2;

import net.pottercraft.Ollivanders2.Spell.Spells;

import java.util.UUID;

/**
 * Common functions.
 *
 * @since 2.2.6
 * @author Azami7
 */
public class Ollivanders2Common
{
   Ollivanders2 p;

   public Ollivanders2Common (Ollivanders2 plugin)
   {
      p = plugin;
   }

   /**
    * Create a UUID from a string.
    *
    * @param uuid
    * @return the UUID or null if an exception occurred.
    */
   public UUID uuidFromString (String uuid)
   {
      UUID pid = null;

      try
      {
         pid = UUID.fromString(uuid);
      }
      catch (Exception e)
      {
         p.getLogger().warning("Failed to parse uuid " + uuid);
         if (p.debug)
            e.printStackTrace();
      }

      return pid;
   }

   public Integer integerFromString (String intString)
   {
      Integer i = null;

      try
      {
         int p = Integer.parseInt(intString);
         i = new Integer(p);
      }
      catch (Exception e)
      {
         p.getLogger().warning("Failed to parse integer " + intString);
         if (p.debug)
            e.printStackTrace();
      }

      return i;
   }

   public Boolean booleanFromString (String boolString)
   {
      Boolean b = null;

      try
      {
         b = Boolean.valueOf(boolString);
      }
      catch (Exception e)
      {
         p.getLogger().warning("Failed to parse boolean " + boolString);
         if (p.debug)
            e.printStackTrace();
      }

      return b;
   }

   public Spells spellsFromString (String spellString)
   {
      Spells spell = null;

      try
      {
         spell = Spells.valueOf(spellString);
      }
      catch (Exception e)
      {
         p.getLogger().warning("Failed to parse spell " + spellString);
         if (p.debug)
            e.printStackTrace();
      }

      return spell;
   }
}
