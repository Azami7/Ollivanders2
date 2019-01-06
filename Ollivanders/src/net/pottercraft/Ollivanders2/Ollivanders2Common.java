package net.pottercraft.Ollivanders2;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import me.libraryaddict.disguise.disguisetypes.RabbitType;
import net.pottercraft.Ollivanders2.Player.O2PlayerCommon;
import net.pottercraft.Ollivanders2.Spell.O2SpellType;
import net.pottercraft.Ollivanders2.Potion.O2PotionType;

import org.bukkit.Bukkit;
import org.bukkit.Color;
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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

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

   public static final String galleon = "Galleon";
   public static final Material galleonMaterial = Material.GOLD_INGOT;
   public static final String sickle = "Sickle";
   public static final Material sickleMaterial = Material.IRON_INGOT;
   public static final String knut = "Knut";
   public static final Material knutMaterial = Material.NETHER_BRICK;

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

   public static final ArrayList<O2SpellType> libsDisguisesSpells = new ArrayList<O2SpellType>() {{
      add(O2SpellType.AMATO_ANIMO_ANIMATO_ANIMAGUS);
      add(O2SpellType.AVIFORS);
      add(O2SpellType.DRACONIFORS);
      add(O2SpellType.DUCKLIFORS);
      add(O2SpellType.ENTOMORPHIS);
      add(O2SpellType.EQUUSIFORS);
      add(O2SpellType.INCARNATIO_DEVITO);
      add(O2SpellType.INCARNATIO_EQUUS);
      add(O2SpellType.INCARNATIO_FELIS);
      add(O2SpellType.INCARNATIO_LAMA);
      add(O2SpellType.INCARNATIO_LUPI);
      add(O2SpellType.INCARNATIO_PORCILLI);
      add(O2SpellType.INCARNATIO_URSUS);
      add(O2SpellType.INCARNATIO_VACCULA);
      add(O2SpellType.LAPIFORS);
      add(O2SpellType.SNUFFLIFORS);
      add(O2SpellType.VERA_VERTO);
   }};

   public static final ArrayList<O2PotionType> libDisguisesPotions = new ArrayList<O2PotionType>() {{
      add(O2PotionType.ANIMAGUS_POTION);
   }};

   public static enum TimeOfDay
   {
      MIDNIGHT(18000),
      DAWN(23000),
      MIDDAY(6000),
      SUNSET(12000);

      private int gameTick;

      TimeOfDay (int tick)
      {
         gameTick = tick;
      }

      public int getTick ()
      {
         return gameTick;
      }
   }

   public static Random random = new Random();

   private JavaPlugin p;

   public Ollivanders2Common (JavaPlugin plugin)
   {
      p = plugin;

      random.setSeed(System.currentTimeMillis());
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
    * @param sourceLocation the source location
    * @param checkLocation the location to check
    * @param radius the radius from the source location
    * @return true if checkLocation is in the radius of sourceLocation
    */
   public boolean isInside (Location sourceLocation, Location checkLocation, int radius)
   {
      double distance = checkLocation.distance(sourceLocation);

      return (distance < radius);
   }

   /**
    * Generate a random Ocelot type.
    *
    * @return the Ocelot type
    */
   public Ocelot.Type randomOcelotType ()
   {
      int rand = Math.abs(random.nextInt() % 4);

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
    * Get a random rabbit type. Odds are 1/60 to get a Killer Bunny.
    *
    * @return the type variant for the rabbit
    */
   public RabbitType randomRabbitType ()
   {
      RabbitType type;

      int rand = Math.abs(random.nextInt() % 61);

      if (rand < 10)
         type = RabbitType.BROWN;
      else if (rand < 20)
         type = RabbitType.BLACK;
      else if (rand < 30)
         type = RabbitType.WHITE;
      else if (rand < 40)
         type = RabbitType.GOLD;
      else if (rand < 50)
         type = RabbitType.PATCHES;
      else if (rand < 60)
         type = RabbitType.PEPPER;
      else
         type = RabbitType.KILLER_BUNNY;

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

      int rand = Math.abs(random.nextInt() % 4);

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

      int rand = Math.abs(random.nextInt() % 20);

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

      int rand = Math.abs(random.nextInt() % 7);

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

      int rand = Math.abs(random.nextInt() % 4);

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

      //
      // Location world
      //
      locData.put(labelPrefix + "_" + locationWorldLabel, location.getWorld().getName());

      //
      //Location x, y, z

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
      if (item.getType() == Material.getMaterial(p.getConfig().getString("broomstick")))
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

   /**
    * Convert an array of strings to a single string.
    *
    * @param strArray the array to convert
    * @return the array as a single string
    */
   public String stringArrayToString (String[] strArray)
   {
      StringBuilder newString = new StringBuilder();

      for (String str : strArray)
      {
         newString.append(" ").append(str);
      }

      return newString.toString().trim();
   }

   /**
    * Get the basic color associated with a number 1-16
    *
    * @param number a number between 1-16, numbers outside of this range will be set to WHITE
    * @return the color associated with this number
    */
   public Color colorByNumber (int number)
   {
      Color color = Color.WHITE;

      if (number == 0)
         color = Color.AQUA;

      else if (number == 1)
         color = Color.BLACK;

      else if (number == 2)
         color = Color.BLUE;

      else if (number == 3)
         color = Color.FUCHSIA;

      else if (number == 4)
         color = Color.GRAY;

      else if (number == 5)
         color = Color.GREEN;

      else if (number == 6)
         color = Color.LIME;

      else if (number == 7)
         color = Color.MAROON;

      else if (number == 8)
         color = Color.NAVY;

      else if (number == 9)
         color = Color.OLIVE;

      else if (number == 10)
         color = Color.ORANGE;

      else if (number == 11)
         color = Color.PURPLE;

      else if (number == 12)
         color = Color.RED;

      else if (number == 13)
         color = Color.SILVER;

      else if (number == 14)
         color = Color.TEAL;

         // 15 is white again

      else if (number == 16)
         color = Color.WHITE;

      return color;
   }

   /**
    * Get the current timestamp as a string.
    *
    * @return timestamp in the format 2018-09-30-12-15-30
    */
   public String getCurrentTimestamp ()
   {
      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
      Date date = new Date();

      return dateFormat.format(date);
   }

   /**
    * Get all the wands in the game.
    *
    * @return a list of all the wands
    */
   public ArrayList<ItemStack> getAllWands ()
   {
      ArrayList<ItemStack> wands = new ArrayList<>();

      for (String wood : O2PlayerCommon.woodArray)
      {
         for (String core : O2PlayerCommon.coreArray)
         {
            ItemStack wand = new ItemStack(Ollivanders2.wandMaterial);
            List<String> lore = new ArrayList<>();
            lore.add(wood + " and " + core);
            ItemMeta meta = wand.getItemMeta();
            meta.setLore(lore);
            meta.setDisplayName("Wand");
            wand.setItemMeta(meta);
            wand.setAmount(1);
            wands.add(wand);
         }
      }

      return wands;
   }

   /**
    * Get an item stack of Galleons
    *
    * @param amount the number of galleons to create
    * @return the item stack of galleons
    */
   public ItemStack getGalleon (int amount)
   {
      ItemStack gal = new ItemStack(galleonMaterial);
      ItemMeta meta = gal.getItemMeta();

      meta.setDisplayName(galleon);
      gal.setItemMeta(meta);
      gal.setAmount(amount);

      return gal;
   }

   /**
    * Get an item stack of Sickle
    *
    * @param amount the number of sickles to create
    * @return the item stack of sickles
    */
   public ItemStack getSickle (int amount)
   {
      ItemStack sic = new ItemStack(sickleMaterial);
      ItemMeta meta = sic.getItemMeta();

      meta.setDisplayName(sickle);
      sic.setItemMeta(meta);
      sic.setAmount(amount);

      return sic;
   }

   /**
    * Get an item stack of Knuts
    *
    * @param amount the number of knuts to create
    * @return the item stack of knuts
    */
   public ItemStack getKnut (int amount)
   {
      ItemStack knu = new ItemStack(knutMaterial);
      ItemMeta meta = knu.getItemMeta();

      meta.setDisplayName(knut);
      knu.setItemMeta(meta);
      knu.setAmount(amount);

      return knu;
   }


   public void givePlayerKit (Player player, List<ItemStack> kit)
   {
      Location loc = player.getEyeLocation();
      ItemStack[] kitArray = kit.toArray(new ItemStack[kit.size()]);
      HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(kitArray);
      for (ItemStack item : leftover.values())
      {
         player.getWorld().dropItem(loc, item);
      }
   }
}
