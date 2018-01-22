package net.pottercraft.Ollivanders2;

import net.pottercraft.Ollivanders2.Spell.Spells;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Common functions and data
 *
 * @since 2.2.6
 * @author Azami7
 */
public class Ollivanders2Common
{
   public static final ArrayList<EntityType> smallFriendlyAnimals = new ArrayList<EntityType>() {{
      add(EntityType.BAT);
      add(EntityType.CHICKEN);
      add(EntityType.RABBIT);
      add(EntityType.PARROT);
   }};

   public static final ArrayList<EntityType> mediumFriendlyAnimals = new ArrayList<EntityType>() {{
      add(EntityType.SHEEP);
      add(EntityType.PIG);
      add(EntityType.OCELOT);
      add(EntityType.WOLF);
   }};

   public static final ArrayList<EntityType> largeFriendlyAnimals = new ArrayList<EntityType>() {{
      add(EntityType.COW);
      add(EntityType.DONKEY);
      add(EntityType.HORSE);
      add(EntityType.MUSHROOM_COW);
      add(EntityType.IRON_GOLEM);
      add(EntityType.SNOWMAN);
      add(EntityType.MULE);
      add(EntityType.SQUID);
      add(EntityType.POLAR_BEAR);
      add(EntityType.LLAMA);
      add(EntityType.SHULKER); // not large in size but complex
   }};

   public static final ArrayList<Spells> libsDisguisesSpells = new ArrayList<Spells>() {{
      add(Spells.AVIFORS);
      add(Spells.DRACONIFORS);
      add(Spells.ENTOMORPHIS);
      add(Spells.EQUUSIFORS);
      add(Spells.INCARNATIO_DEVITO);
      add(Spells.INCARNATIO_EQUUS);
      add(Spells.INCARNATIO_LAMA);
      add(Spells.INCARNATIO_LUPI);
      add(Spells.INCARNATIO_PORCILLI);
      add(Spells.INCARNATIO_URSUS);
      add(Spells.INCARNATIO_VACCULA);
      add(Spells.LAPIFORS);
      add(Spells.VERA_VERTO);
   }};

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

   /**
    * Get an integer from a string.
    *
    * @param intString
    * @return
    */
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

   /**
    * Get a boolean from a string.
    *
    * @param boolString
    * @return
    */
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

   /**
    * Get a Spells object from a string.
    *
    * @param spellString
    * @return
    */
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
