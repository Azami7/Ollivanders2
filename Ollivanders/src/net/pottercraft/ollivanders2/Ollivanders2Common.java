package net.pottercraft.ollivanders2;

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
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.player.O2WandCoreType;
import net.pottercraft.ollivanders2.player.O2WandWoodType;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.potion.O2PotionType;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

   public static final int ticksPerSecond = 20;

   public static final ArrayList<EntityType> smallFriendlyAnimals = new ArrayList<EntityType>() {{
      add(EntityType.BAT);
      add(EntityType.CHICKEN);
      add(EntityType.RABBIT);
      add(EntityType.PARROT);
      add(EntityType.COD);
      add(EntityType.SALMON);
      add(EntityType.TROPICAL_FISH);
      add(EntityType.PUFFERFISH);
   }};

   public static final ArrayList<EntityType> mediumFriendlyAnimals = new ArrayList<EntityType>() {{
      add(EntityType.SHEEP);
      add(EntityType.PIG);
      add(EntityType.OCELOT);
      add(EntityType.WOLF);
      add(EntityType.CAT);
      add(EntityType.FOX);
      add(EntityType.TURTLE);
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
      add(EntityType.PANDA);
      add(EntityType.DOLPHIN);
      add(EntityType.TRADER_LLAMA);
   }};

   public static final ArrayList<EntityType> undeadEntities = new ArrayList<EntityType>()
   {{
      add(EntityType.DROWNED);
      add(EntityType.HUSK);
      add(EntityType.PHANTOM);
      add(EntityType.PIG_ZOMBIE);
      add(EntityType.SKELETON);
      add(EntityType.SKELETON_HORSE);
      add(EntityType.STRAY);
      add(EntityType.WITHER);
      add(EntityType.WITHER_SKELETON);
      add(EntityType.ZOMBIE);
      add(EntityType.ZOMBIE_HORSE);
      add(EntityType.ZOMBIE_VILLAGER);
      add(EntityType.GIANT);
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

   public static final ArrayList<Material> unbreakableMaterials = new ArrayList<Material>() {{
      add(Material.BARRIER);
      add(Material.BEDROCK);
      add(Material.ENDER_CHEST);
      add(Material.WHITE_SHULKER_BOX);
      add(Material.BLACK_SHULKER_BOX);
      add(Material.BLUE_SHULKER_BOX);
      add(Material.SHULKER_BOX);
      add(Material.BROWN_SHULKER_BOX);
      add(Material.CYAN_SHULKER_BOX);
      add(Material.GRAY_SHULKER_BOX);
      add(Material.GREEN_SHULKER_BOX);
      add(Material.LIGHT_BLUE_SHULKER_BOX);
      add(Material.LIGHT_GRAY_SHULKER_BOX);
      add(Material.LIME_SHULKER_BOX);
      add(Material.MAGENTA_SHULKER_BOX);
      add(Material.ORANGE_SHULKER_BOX);
      add(Material.PINK_SHULKER_BOX);
      add(Material.PURPLE_SHULKER_BOX);
      add(Material.RED_SHULKER_BOX);
      add(Material.YELLOW_SHULKER_BOX);
   }};

   public static final ArrayList<Material> chests = new ArrayList<Material>()
   {{
      add(Material.CHEST);
      add(Material.ENDER_CHEST);
      add(Material.WHITE_SHULKER_BOX);
      add(Material.BLACK_SHULKER_BOX);
      add(Material.BLUE_SHULKER_BOX);
      add(Material.SHULKER_BOX);
      add(Material.BROWN_SHULKER_BOX);
      add(Material.CYAN_SHULKER_BOX);
      add(Material.GRAY_SHULKER_BOX);
      add(Material.GREEN_SHULKER_BOX);
      add(Material.LIGHT_BLUE_SHULKER_BOX);
      add(Material.LIGHT_GRAY_SHULKER_BOX);
      add(Material.LIME_SHULKER_BOX);
      add(Material.MAGENTA_SHULKER_BOX);
      add(Material.ORANGE_SHULKER_BOX);
      add(Material.PINK_SHULKER_BOX);
      add(Material.PURPLE_SHULKER_BOX);
      add(Material.RED_SHULKER_BOX);
      add(Material.YELLOW_SHULKER_BOX);
   }};

   public static final ArrayList<Material> wallSigns = new ArrayList<Material>()
   {{
      add(Material.ACACIA_WALL_SIGN);
      add(Material.BIRCH_WALL_SIGN);
      add(Material.DARK_OAK_WALL_SIGN);
      add(Material.OAK_WALL_SIGN);
      add(Material.SPRUCE_WALL_SIGN);
   }};

   public static final ArrayList<Material> standingSigns = new ArrayList<Material>()
   {{
      add(Material.ACACIA_SIGN);
      add(Material.BIRCH_SIGN);
      add(Material.DARK_OAK_SIGN);
      add(Material.OAK_SIGN);
      add(Material.SPRUCE_SIGN);
   }};

   public static final ArrayList<Material> signs = new ArrayList<Material>()
   {{
      addAll(wallSigns);
      addAll(standingSigns);
   }};

   public static final ArrayList<Material> hotBlocks = new ArrayList<Material>()
   {{
      add(Material.LAVA);
      add(Material.FIRE);
      add(Material.CAMPFIRE);
   }};

   public enum TimeOfDay
   {
      MIDNIGHT(18000),
      DAWN(23000),
      MIDDAY(6000),
      SUNSET(12000);

      private int gameTick;

      TimeOfDay(int tick)
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
      double distance;

      try
      {
         distance = checkLocation.distance(sourceLocation);
      }
      catch (Exception e)
      {
         // this call can generate java.lang.IllegalArgumentException: Cannot measure distance between world_nether and world
         return false;
      }

      return (distance < radius);
   }

   /**
    * Generate a random Cat type.
    *
    * @return the Cat type
    */
   public Cat.Type getRandomCatType()
   {
      int seed = Math.abs(random.nextInt());

      return getRandomCatType(seed);
   }

   /**
    * Generate a random Cat type.
    *
    * @param seed the base value that the percentile check will use
    * @return the Cat type
    */
   public Cat.Type getRandomCatType(int seed)
   {
      int rand = Math.abs(seed) % 11;

      Cat.Type type;

      switch (rand)
      {
         case 0:
            type = Cat.Type.ALL_BLACK;
            break;
         case 1:
            type = Cat.Type.BLACK;
            break;
         case 2:
            type = Cat.Type.BRITISH_SHORTHAIR;
            break;
         case 3:
            type = Cat.Type.CALICO;
            break;
         case 4:
            type = Cat.Type.JELLIE;
            break;
         case 5:
            type = Cat.Type.PERSIAN;
            break;
         case 6:
            type = Cat.Type.RAGDOLL;
            break;
         case 7:
            type = Cat.Type.RED;
            break;
         case 8:
            type = Cat.Type.SIAMESE;
            break;
         case 9:
            type = Cat.Type.TABBY;
            break;
         default:
            type = Cat.Type.WHITE;
            break;
      }

      return type;
   }

   /**
    * Get a random rabbit type. Odds are 1/60 to get a Killer Bunny.
    *
    * @return the type color for the rabbit
    */
   public RabbitType getRandomRabbitType()
   {
      int seed = Math.abs(random.nextInt());

      return getRandomRabbitType(seed);
   }

   /**
    * Get a random rabbit type. Odds are 1/60 to get a Killer Bunny.
    *
    * @param seed the base value that the percentile check will use
    * @return the type color for the rabbit
    */
   public RabbitType getRandomRabbitType(int seed)
   {
      RabbitType type;

      int rand = Math.abs(seed) % 61;

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
    * Generate a random horse style.
    *
    * @return the horse style
    */
   public Horse.Style getRandomHorseStyle()
   {
      int seed = Math.abs(random.nextInt());

      return getRandomHorseStyle(seed);
   }

   /**
    * Generate a random horse style.
    *
    * @param seed the base value that the percentile check will use
    * @return the horse style
    */
   public Horse.Style getRandomHorseStyle(int seed)
   {
      Horse.Style style;

      int rand = Math.abs(seed) % 20;

      switch (rand)
      {
         case 0:
            style = Horse.Style.WHITE;
            break;
         case 1:
            style = Horse.Style.WHITE_DOTS;
            break;
         case 2:
            style = Horse.Style.WHITEFIELD;
            break;
         case 3:
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
   public Horse.Color getRandomHorseColor()
   {
      int seed = Math.abs(random.nextInt());

      return getRandomHorseColor(seed);
   }

   /**
    * Generate a random horse color.
    *
    * @param seed the base value that the percentile check will use
    * @return the color
    */
   public Horse.Color getRandomHorseColor(int seed)
   {
      Horse.Color color;

      int rand = Math.abs(seed) % 7;

      switch (rand)
      {
         case 0:
            color = Horse.Color.BLACK;
            break;
         case 1:
            color = Horse.Color.BROWN;
            break;
         case 2:
            color = Horse.Color.CHESTNUT;
            break;
         case 3:
            color = Horse.Color.CREAMY;
            break;
         case 4:
            color = Horse.Color.DARK_BROWN;
            break;
         case 5:
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
   public Llama.Color getRandomLlamaColor()
   {
      int seed = Math.abs(random.nextInt());

      return getRandomLlamaColor(seed);
   }

   /**
    * Generate random llama color.
    *
    * @param seed the base value that the percentile check will use
    * @return the color
    */
   public Llama.Color getRandomLlamaColor(int seed)
   {
      Llama.Color color;

      int rand = Math.abs(seed) % 4;

      switch (rand)
      {
         case 0:
            color = Llama.Color.BROWN;
            break;
         case 1:
            color = Llama.Color.CREAMY;
            break;
         case 2:
            color = Llama.Color.GRAY;
            break;
         default:
            color = Llama.Color.WHITE;
            break;
      }

      return color;
   }

   /**
    * Genenrate a random parrot color.
    *
    * @return the color
    */
   public Parrot.Variant getRandomParrotColor()
   {
      int seed = Math.abs(random.nextInt());

      return getRandomParrotColor(seed);
   }

   /**
    * Genenrate a random parrot color.
    *
    * @param seed the base value that the percentile check will use
    * @return the color
    */
   public Parrot.Variant getRandomParrotColor(int seed)
   {
      Parrot.Variant variant;

      int rand = Math.abs(seed) % 5;

      switch (rand)
      {
         case 0:
            variant = Parrot.Variant.CYAN;
            break;
         case 1:
            variant = Parrot.Variant.GRAY;
            break;
         case 2:
            variant = Parrot.Variant.BLUE;
            break;
         case 3:
            variant = Parrot.Variant.GREEN;
            break;
         default:
            variant = Parrot.Variant.RED;
      }

      return variant;
   }

   /**
    * Generate a random natural sheep color
    *
    * @return the color
    */
   public DyeColor getRandomNaturalSheepColor()
   {
      int seed = Math.abs(random.nextInt());

      return getRandomNaturalSheepColor(seed);
   }

   /**
    * Generate a random natural sheep color
    *
    * @param seed the base value that the percentile check will use
    * @return the color
    */
   public DyeColor getRandomNaturalSheepColor(int seed)
   {
      int rand = Math.abs(seed) % 100;

      if (rand < 2) // 2% chance
      {
         return DyeColor.BLACK;
      }
      else if (rand < 22) // 20% chance
      {
         return DyeColor.BROWN;
      }
      else if (rand < 32) // 10% chance
      {
         return DyeColor.LIGHT_GRAY;
      }
      else
         return DyeColor.WHITE;
   }

   /**
    * Gets all entities within a radius of a specific location
    *
    * @param location the location to search for entities from
    * @param radius   - radius within which to get entities
    * @return List of entities within the radius
    */
   public Collection<Entity> getEntitiesInRadius(Location location, double radius)
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
      if (item.getType() == Ollivanders2.broomstickMaterial)
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
   public ArrayList<ItemStack> getAllWands()
   {
      ArrayList<ItemStack> wands = new ArrayList<>();

      for (String wood : O2WandWoodType.getAllWoodsByName())
      {
         for (String core : O2WandCoreType.getAllCoresByName())
         {
            wands.add(makeWands(wood, core, 1));
         }
      }

      return wands;
   }

   /**
    * Make an ItemStack of wands of a specific wood and core
    *
    * @param wood   the wand wood
    * @param core   the wand core
    * @param amount the number of wands to make
    * @return an ItemStack of wands or null if an error
    */
   @Nullable
   public ItemStack makeWands(@NotNull String wood, @NotNull String core, int amount)
   {
      if (amount < 1)
         amount = 1;

      List<String> lore = new ArrayList<>();
      ItemStack wand = Ollivanders2API.getItems().getItemByType(O2ItemType.WAND, 1);

      lore.add(wood + " and " + core);
      ItemMeta meta = wand.getItemMeta();

      if (meta == null)
         return null;

      meta.setLore(lore);
      wand.setItemMeta(meta);
      wand.setAmount(amount);

      return wand;
   }

   /**
    * Determine if a player is facing a block type.
    *
    * @param player the player to check
    * @return the cauldron if a player is facing one, null otherwise
    */
   public Block playerFacingBlockType (Player player, Material blockType)
   {
      List<Block> blocksInFront = player.getLineOfSight(null, 3);
      Block target = null;

      for (Block block : blocksInFront)
      {
         if (block.getType() == blockType)
         {
            target = block;
            break;
         }
      }

      return target;
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

   /**
    * Gives a player an item stack
    *
    * @param player the player to give the items to
    * @param kit    the items to give
    */
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

   /**
    * Is this material a natural log (ie. living tree)
    *
    * @param block the block to check
    * @return true if it is a natural log, false otherwise
    */
   public boolean isNaturalLog (Block block)
   {
      Material material = block.getType();

      if (material == Material.OAK_LOG || material == Material.BIRCH_LOG || material == Material.ACACIA_LOG
            || material == Material.DARK_OAK_LOG || material == Material.JUNGLE_LOG || material == Material.SPRUCE_LOG)
      {
         return true;
      }
      else
      {
         return false;
      }
   }

   /**
    * Makes a particle effect at all points along the radius of
    * spell and at spell loc
    *
    * @param intensity - Intensity of the flair. If greater than 10, is reduced to 10.
    */
   public static void flair (Location location, int radius, double intensity)
   {
      if (intensity > 10)
      {
         intensity = 10;
      }
      for (double inc = (Math.random() * Math.PI) / intensity; inc < Math.PI; inc += Math.PI / intensity)
      {
         for (double azi = (Math.random() * Math.PI) / intensity; azi < 2 * Math.PI; azi += Math.PI / intensity)
         {
            double[] spher = new double[2];
            spher[0] = inc;
            spher[1] = azi;
            Location effectLocation = location.clone().add(spherToVec(spher, radius));
            effectLocation.getWorld().playEffect(effectLocation, Effect.SMOKE, 4);
         }
      }
   }

   /**
    * Translates vector to spherical coords
    *
    * @param vec - Vector to be translated
    * @return Spherical coords in double array with
    * indexes 0=inclination 1=azimuth
    */
   public static double[] vecToSpher (Vector vec)
   {
      double inc = Math.acos(vec.getZ());
      double azi = Math.atan2(vec.getY(), vec.getX());
      double[] ret = new double[2];
      ret[0] = inc;
      ret[1] = azi;

      return ret;
   }

   /**
    * Translates spherical coords to vector
    *
    * @param spher array with indexes 0=inclination 1=azimuth
    * @return Vector
    */
   public static Vector spherToVec(double[] spher, int radius)
   {
      double inc = spher[0];
      double azi = spher[1];
      double x = radius * Math.sin(inc) * Math.cos(azi);
      double z = radius * Math.sin(inc) * Math.sin(azi);
      double y = radius * Math.cos(inc);

      return new Vector(x, y, z);
   }

   /**
    * Send a title message to all players.
    *
    * @param title
    * @param subtitle
    * @param players
    */
   public void sendTitleMessage(String title, String subtitle, ArrayList<Player> players)
   {
      for (Player player : players)
      {
         player.sendTitle(title, subtitle, 10, 70, 20);
      }
   }

   /**
    * Get a list of all the sorted online players
    *
    * @return the list of sorted online players
    */
   public ArrayList<Player> getAllOnlineSortedPlayers()
   {
      ArrayList<Player> sortedPlayers = new ArrayList<>();

      for (Player player : p.getServer().getOnlinePlayers())
      {
         if (Ollivanders2API.getHouses().isSorted(player))
            sortedPlayers.add(player);
      }

      return sortedPlayers;
   }
}
