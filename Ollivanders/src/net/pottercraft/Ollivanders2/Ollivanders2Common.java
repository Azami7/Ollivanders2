package net.pottercraft.Ollivanders2;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Collection;

import net.pottercraft.Ollivanders2.Spell.Spells;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Ocelot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Common functions and data
 *
 * @since 2.2.6
 * @author Azami7
 */

public class Ollivanders2Common
{
   private final String locationWorldLabel = "World";
   private final String locationXLabel = "X-Value";
   private final String locationYLabel = "Y-Value";
   private final String locationZLabel = "Z-Value";

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
      add(Spells.AMATO_ANIMO_ANIMATO_ANIMAGUS);
      add(Spells.AVIFORS);
      add(Spells.DRACONIFORS);
      add(Spells.DUCKLIFORS);
      add(Spells.ENTOMORPHIS);
      add(Spells.EQUUSIFORS);
      add(Spells.INCARNATIO_DEVITO);
      add(Spells.INCARNATIO_EQUUS);
      add(Spells.INCARNATIO_FELIS);
      add(Spells.INCARNATIO_LAMA);
      add(Spells.INCARNATIO_LUPI);
      add(Spells.INCARNATIO_PORCILLI);
      add(Spells.INCARNATIO_URSUS);
      add(Spells.INCARNATIO_VACCULA);
      add(Spells.LAPIFORS);
      add(Spells.SNUFFLIFORS);
      add(Spells.VERA_VERTO);
   }};

   private Ollivanders2 p;

   public Ollivanders2Common (Ollivanders2 plugin)
   {
      p = plugin;
   }

   /**
    * Create a UUID from a string.
    *
    * @param uuid the UUID as a string
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
         if (Ollivanders2.debug)
            e.printStackTrace();
      }

      return pid;
   }

   /**
    * Get an integer from a string.
    *
    * @param intString the integer as a string
    * @return the Integer or null if an exception occurred
    */
   public Integer integerFromString (String intString)
   {
      Integer i = null;

      try
      {
         i = Integer.parseInt(intString);
      }
      catch (Exception e)
      {
         p.getLogger().warning("Failed to parse integer " + intString);
         if (Ollivanders2.debug)
            e.printStackTrace();
      }

      return i;
   }

   /**
    * Get a boolean from a string.
    *
    * @param boolString the boolean as a string
    * @return the Boolean or null if an exception occurred
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
         if (Ollivanders2.debug)
            e.printStackTrace();
      }

      return b;
   }

   /**
    * Get an EntityType enum from a string.
    *
    * @param entityTypeString the entity type as a string
    * @return the EntityType or null if an exception occurred
    */
   public EntityType entityTypeFromString (String entityTypeString)
   {
      EntityType entityType = null;

      try
      {
         entityType = EntityType.valueOf(entityTypeString);
      }
      catch (Exception e)
      {
         p.getLogger().warning("Failed to parse EntityType " + entityTypeString);
         if (Ollivanders2.debug)
            e.printStackTrace();
      }
      return entityType;
   }

   /**
    * Determine if a location is within a radius of another location
    * @param loc1 the source location
    * @param loc2 the location to check
    * @param radius the radius from the source location
    * @return true if loc2 is in the radius of loc1
    */
   public boolean isInside (Location loc1, Location loc2, int radius)
   {
      double distance = loc2.distance(loc1);

      return (distance < radius);
   }

   /**
    * Generate a random Ocelot type.
    *
    * @return the Ocelot type
    */
   public Ocelot.Type randomOcelotType ()
   {
      int rand = Math.abs(Ollivanders2.random.nextInt() % 4);

      Ocelot.Type type;

      switch (rand)
      {
         case 1:
           type = Ocelot.Type.BLACK_CAT;
           break;
         case 2:
            type = Ocelot.Type.RED_CAT;
            break;
         case 3:
            type = Ocelot.Type.SIAMESE_CAT;
            break;
         default:
            type = Ocelot.Type.WILD_OCELOT;
            break;
      }

      return type;
   }

   /**
    * Generate a random primary or secondary color - red, orange, yellow, green, blue, purple
    *
    * @return the dye color
    */
   public DyeColor randomSecondaryDyeColor ()
   {
      DyeColor color;

      int rand = Math.abs(Ollivanders2.random.nextInt() % 4);

      switch (rand)
      {
         case 1:
            color = DyeColor.RED;
            break;
         case 2:
            color = DyeColor.ORANGE;
            break;
         case 3:
            color = DyeColor.YELLOW;
            break;
         case 4:
            color = DyeColor.GREEN;
            break;
         case 5:
            color = DyeColor.BLUE;
            break;
         default:
            color = DyeColor.PURPLE;
            break;
      }

      return color;
   }

   /**
    * Generate a random horse style.
    *
    * @return the horse style
    */
   public Horse.Style randomHorseStyle ()
   {
      Horse.Style style;

      int rand = Math.abs(Ollivanders2.random.nextInt() % 20);

      switch (rand)
      {
         case 1:
            style = Horse.Style.WHITE;
            break;
         case 2:
            style = Horse.Style.WHITE_DOTS;
            break;
         case 3:
            style = Horse.Style.WHITEFIELD;
            break;
         case 4:
            style = Horse.Style.BLACK_DOTS;
            break;
         default:
            style = Horse.Style.NONE;
            break;
      }

      return style;
   }

   /**
    * Generate a random horse color.
    *
    * @return the color
    */
   public Horse.Color randomHorseColor ()
   {
      Horse.Color color;

      int rand = Math.abs(Ollivanders2.random.nextInt() % 7);

      switch (rand)
      {
         case 1:
            color = Horse.Color.BLACK;
            break;
         case 2:
            color = Horse.Color.BROWN;
            break;
         case 3:
            color = Horse.Color.CHESTNUT;
            break;
         case 4:
            color = Horse.Color.CREAMY;
            break;
         case 5:
            color = Horse.Color.DARK_BROWN;
            break;
         case 6:
            color = Horse.Color.GRAY;
            break;
         default:
            color = Horse.Color.WHITE;
            break;
      }

      return color;
   }

   /**
    * Generate random llama color.
    *
    * @return the color
    */
   public Llama.Color randomLlamaColor ()
   {
      Llama.Color color;

      int rand = Math.abs(Ollivanders2.random.nextInt() % 4);

      switch (rand)
      {
         case 1:
            color = Llama.Color.BROWN;
            break;
         case 2:
            color = Llama.Color.CREAMY;
            break;
         case 3:
            color = Llama.Color.GRAY;
            break;
         default:
            color = Llama.Color.WHITE;
            break;
      }

      return color;
   }

   /**
    * Gets all entities within a radius of a specific location
    *
    * @param location the location to search for entities from
    * @param radius - radius within which to get entities
    * @return List of entities within the radius
    */
   public Collection<Entity> getEntitiesInRadius (Location location, double radius)
   {
      return getEntitiesInBounds(location, radius, radius, radius);
   }

   /**
    * Gets all living entities within a radius of a specific location
    *
    * @param location the location to search for entities from
    * @param radius - radius within which to get entities
    * @return List of living entities within the radius
    */
   public List<LivingEntity> getLivingEntitiesInRadius (Location location, double radius)
   {
      Collection<Entity> entities = getEntitiesInRadius(location, radius);
      List<LivingEntity> close = new ArrayList<>();

      for (Entity e : entities)
      {
         if (e instanceof LivingEntity)
         {
            close.add(((LivingEntity)e));
         }
      }

      return close;
   }

   /**
    * Gets all of a specific entity type within a radius of a specific location
    *
    * @param location - the location to search for entities from
    * @param radius - radius within which to get entities
    * @param entityType - the type of entity to look for
    * @return List of entities of the specified type within the radius
    */
   public List<Entity> getTypedCloseEntities (Location location, double radius, EntityType entityType)
   {
      Collection<Entity> entities = getEntitiesInRadius(location, radius);
      List<Entity> close = new ArrayList<>();

      for (Entity e : entities)
      {
         if (e.getType() == entityType)
         {
            close.add(e);
         }
      }
      return close;
   }

   /**
    * Gets the blocks in a radius of a location.
    *
    * @param loc    - The Location that is the center of the block list
    * @param radius - The radius of the block list
    * @return List of blocks that are within radius of the location.
    */
   public List<Block> getBlocksInRadius (Location loc, double radius)
   {
      Block center = loc.getBlock();
      int blockRadius = (int) (radius + 1);
      List<Block> blockList = new ArrayList<>();
      for (int x = -blockRadius; x <= blockRadius; x++)
      {
         for (int y = -blockRadius; y <= blockRadius; y++)
         {
            for (int z = -blockRadius; z <= blockRadius; z++)
            {
               blockList.add(center.getRelative(x, y, z));
            }
         }
      }
      ArrayList<Block> returnList = new ArrayList<>();
      for (Block block : blockList)
      {
         if (block.getLocation().distance(center.getLocation()) < radius)
         {
            returnList.add(block);
         }
      }
      return returnList;
   }

   /**
    * Find the lowercase string that corresponds to an enum
    *
    * @param s the enum as a string
    * @return string such that it is the lowercase version of the spell minus underscores
    */
   @Deprecated
   public String enumRecode (String s)
   {
      String nameLow = s.toLowerCase();
      String[] words = nameLow.split("_");
      String comp = "";
      for (String st : words)
      {
         comp = comp.concat(st);
         comp = comp.concat(" ");
      }
      comp = comp.substring(0, comp.length() - 1);
      return comp;
   }

   /**
    * Converts a string to have it's first letter of each word be in upper case, and all other letters lower case.
    *
    * @param str - String to convert.
    * @return String with correct formatting.
    */
   public String firstLetterCapitalize (String str)
   {
      StringBuilder sb = new StringBuilder();
      String[] wordList = str.split(" ");
      for (String s : wordList)
      {
         sb.append(s.substring(0, 1).toUpperCase());
         if (s.length() > 1)
         {
            sb.append(s.substring(1, s.length()).toLowerCase());
         }
         sb.append(" ");
      }
      return sb.substring(0, sb.length() - 1);
   }
  
   /**
    * Determine if this is the Cloak of Invisibility.
    *
    * @param held Item stack to check
    * @return True if held is an invisibility cloak
    */
   public boolean isInvisibilityCloak (ItemStack held)
   {
      if (held.getType() == Material.CHAINMAIL_CHESTPLATE)
      {
         if (held.getItemMeta().hasLore())
         {
            List<String> lore = held.getItemMeta().getLore();
            if (lore.get(0).equals("Silvery Transparent Cloak"))
            {
               return true;
            }
         }
      }
      return false;
   }

   /**
    * Serialize an Location for saving.
    *
    * @param location the Location to serialize
    * @param labelPrefix the prefix for the label string, assumes not empty or null
    * @return a map of the serialized OLocation data
    */
   public Map<String, String> serializeLocation (Location location, String labelPrefix)
   {
      Map<String, String> locData = new HashMap<>();

      /**
       * Location world
       */
      locData.put(labelPrefix + "_" + locationWorldLabel, location.getWorld().getName());

      /**
       * Location x, y, z
       */
      Double x = location.getX();
      locData.put(labelPrefix + "_" + locationXLabel, x.toString());

      Double y = location.getY();
      locData.put(labelPrefix + "_" + locationYLabel, y.toString());

      Double z = location.getZ();
      locData.put(labelPrefix + "_" + locationZLabel, z.toString());

      return locData;
   }

   /**
    * Returns a Location from serialized data.
    *
    * @return the location if the data was successfully read, null otherwise
    */
   public Location deserializeLocation (Map<String, String> locData, String labelPrefix)
   {
      double x = 0.0;
      double y = 0.0;
      double z = 0.0;
      String worldName = "world";

      for (Entry <String, String> e : locData.entrySet())
      {
         try
         {
            if (e.getKey().equals(labelPrefix + "_" + locationWorldLabel))
            {
               worldName = e.getValue();
            }
            else if (e.getKey().equals(labelPrefix + "_" + locationXLabel))
            {
               x = Double.parseDouble(e.getValue());
            }
            else if (e.getKey().equals(labelPrefix + "_" + locationYLabel))
            {
               y = Double.parseDouble(e.getValue());
            }
            else if (e.getKey().equals(labelPrefix + "_" + locationZLabel))
            {
               z = Double.parseDouble(e.getValue());
            }
         }
         catch (Exception exception)
         {
            p.getLogger().info("Unable to deserialize location");
            if (Ollivanders2.debug)
               exception.printStackTrace();

            return null;
         }
      }

      World world = Bukkit.getServer().getWorld(worldName);

      return new Location(world, x, y, z);
   }

   /**
    * Is this item stack a wand?
    *
    * @param stack stack to be checked
    * @return true if yes, false if no
    */
   public boolean isWand (ItemStack stack)
   {
      if (stack != null)
      {
         if (stack.getType() == Material.STICK || stack.getType() == Material.BLAZE_ROD)
         {
            if (stack.getItemMeta().hasLore())
            {
               List<String> lore = stack.getItemMeta().getLore();

               if (lore.get(0).split(" and ").length == 2)
               {
                  return true;
               }
               else
               {
                  return false;
               }
            }
            else
            {
               return false;
            }
         }
         else
         {
            return false;
         }
      }
      else
      {
         return false;
      }
   }

   /**
    * Finds out if an item is a broom.
    *
    * @param item item in question
    * @return True if yes
    */
   public boolean isBroom (ItemStack item)
   {
      if (item.getType() == Material.getMaterial(p.getFileConfig().getString("broomstick")))
      {
         if (item.containsEnchantment(Enchantment.PROTECTION_FALL))
         {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasLore())
            {
               List<String> lore = meta.getLore();
               if (lore.contains("Flying vehicle used by magical folk"))
               {
                  return true;
               }
            }
         }
      }
      return false;
   }

   /**
    * Gets item entities within radius of the projectile
    *
    * @return List of item entities within radius of projectile
    */
   public List<Item> getItemsInBounds (Location location, double x, double y, double z)
   {
      Collection<Entity> entities = getEntitiesInBounds(location, x, y, z);

      List<Item> items = new ArrayList<>();
      for (Entity e : entities)
      {
         if (e instanceof Item)
         {
            items.add((Item) e);
         }
      }
      return items;
   }

   /**
    * Get all the entities with a bounding box of a specific location.
    *
    * @param location the location to check from
    * @param x the distance +/- on the x-plane
    * @param y the distance +/- on the y-plane
    * @param z the distance +/- on the z-plane
    * @return a Collection of all entities within the bounding box.
    */
   public Collection<Entity> getEntitiesInBounds (Location location, double x, double y, double z)
   {
      World world = location.getWorld();

      return world.getNearbyEntities(location, x, y, z);
   }
}
