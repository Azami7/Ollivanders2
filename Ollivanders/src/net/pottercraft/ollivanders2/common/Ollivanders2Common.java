package net.pottercraft.ollivanders2.common;

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
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
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
import org.bukkit.attribute.AttributeInstance;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Common functions and data
 *
 * @author Azami7
 * @since 2.2.6
 */

public class Ollivanders2Common
{
   private static final String locationWorldLabel = "World";
   private static final String locationXLabel = "X-Value";
   private static final String locationYLabel = "Y-Value";
   private static final String locationZLabel = "Z-Value";

   public static final int ticksPerSecond = 20;
   public static final int ticksPerMinute = ticksPerSecond * 60;
   public static final int ticksPerHour = ticksPerMinute * 60;

   public static final List<EntityType> smallFriendlyMobs = new ArrayList<>()
   {{
      add(EntityType.BAT);
      add(EntityType.CHICKEN);
      add(EntityType.RABBIT);
      add(EntityType.PARROT);
      add(EntityType.COD);
      add(EntityType.SALMON);
      add(EntityType.TROPICAL_FISH);
      add(EntityType.PUFFERFISH);
   }};

   public static final List<EntityType> mediumFriendlyMobs = new ArrayList<>()
   {{
      add(EntityType.SHEEP);
      add(EntityType.PIG);
      add(EntityType.OCELOT);
      add(EntityType.WOLF);
      add(EntityType.CAT);
      add(EntityType.FOX);
      add(EntityType.TURTLE);
   }};

   public static final List<EntityType> largeFriendlyMobs = new ArrayList<>()
   {{
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

   public static final List<EntityType> undeadEntities = new ArrayList<>()
   {{
      add(EntityType.DROWNED);
      add(EntityType.HUSK);
      add(EntityType.PHANTOM);
      add(EntityType.ZOMBIFIED_PIGLIN);
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

   public static final List<O2SpellType> libsDisguisesSpells = new ArrayList<>()
   {{
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

   public static final List<O2PotionType> libDisguisesPotions = new ArrayList<>()
   {{
      add(O2PotionType.ANIMAGUS_POTION);
   }};

   public static final List<Material> unbreakableMaterials = new ArrayList<>()
   {{
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

   public static final List<Material> chests = new ArrayList<>()
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

   public static final List<Material> wallSigns = new ArrayList<>()
   {{
      add(Material.ACACIA_WALL_SIGN);
      add(Material.BIRCH_WALL_SIGN);
      add(Material.DARK_OAK_WALL_SIGN);
      add(Material.OAK_WALL_SIGN);
      add(Material.SPRUCE_WALL_SIGN);
      add(Material.CRIMSON_WALL_SIGN);
      add(Material.WARPED_WALL_SIGN);
   }};

   public static final List<Material> standingSigns = new ArrayList<>()
   {{
      add(Material.ACACIA_SIGN);
      add(Material.BIRCH_SIGN);
      add(Material.DARK_OAK_SIGN);
      add(Material.OAK_SIGN);
      add(Material.SPRUCE_SIGN);
      add(Material.CRIMSON_SIGN);
      add(Material.WARPED_SIGN);
   }};

   public static final List<Material> signs = new ArrayList<>()
   {{
      addAll(wallSigns);
      addAll(standingSigns);
   }};

   public static final List<Material> doors = new ArrayList<>()
   {{
      add(Material.ACACIA_DOOR);
      add(Material.BIRCH_DOOR);
      add(Material.CRIMSON_DOOR);
      add(Material.DARK_OAK_DOOR);
      add(Material.IRON_DOOR);
      add(Material.JUNGLE_DOOR);
      add(Material.OAK_DOOR);
      add(Material.SPRUCE_DOOR);
      add(Material.WARPED_DOOR);
   }};

   public static final List<Material> trapdoors = new ArrayList<>()
   {{
      add(Material.ACACIA_TRAPDOOR);
      add(Material.BIRCH_TRAPDOOR);
      add(Material.CRIMSON_TRAPDOOR);
      add(Material.DARK_OAK_TRAPDOOR);
      add(Material.IRON_TRAPDOOR);
      add(Material.JUNGLE_TRAPDOOR);
      add(Material.OAK_TRAPDOOR);
      add(Material.SPRUCE_TRAPDOOR);
      add(Material.WARPED_TRAPDOOR);
   }};

   public static final List<Material> hotBlocks = new ArrayList<>()
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

      final private int gameTick;

      TimeOfDay(int tick)
      {
         gameTick = tick;
      }

      public int getTick()
      {
         return gameTick;
      }
   }

   /**
    * The level of magic a spell or potion is
    */
   public enum MagicLevel
   {
      BEGINNER (1),
      OWL (2),
      NEWT (3),
      EXPERT (4);

      final int successModifier;

      MagicLevel (int mod)
      {
         successModifier = mod;
      }

      public int getSuccessModifier()
      {
         return successModifier;
      }
   }

   public final static Random random = new Random();

   final private Ollivanders2 p;

   /**
    * Constructor
    *
    * @param plugin a reference to the plugin using this common
    */
   public Ollivanders2Common (@NotNull Ollivanders2 plugin)
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
   @Nullable
   public UUID uuidFromString(@NotNull String uuid)
   {
      UUID pid = null;

      try
      {
         pid = UUID.fromString(uuid);
      }
      catch (Exception e)
      {
         printDebugMessage("Failed to parse uuid " + uuid, e, null, true);
      }

      return pid;
   }

   /**
    * Get an integer from a string.
    *
    * @param intString the integer as a string
    * @return the Integer or null if an exception occurred
    */
   @Nullable
   public Integer integerFromString(@NotNull String intString)
   {
      Integer i = null;

      try
      {
         i = Integer.parseInt(intString);
      }
      catch (Exception e)
      {
         printDebugMessage("Failed to parse integer " + intString, e, null, true);
      }

      return i;
   }

   /**
    * Get a boolean from a string.
    *
    * @param boolString the boolean as a string
    * @return the Boolean or null if an exception occurred
    */
   @Nullable
   public Boolean booleanFromString(@NotNull String boolString)
   {
      Boolean b = null;

      try
      {
         b = Boolean.valueOf(boolString);
      }
      catch (Exception e)
      {
         printDebugMessage("Failed to parse boolean " + boolString, e, null, true);
      }

      return b;
   }

   /**
    * Get an EntityType enum from a string.
    *
    * @param entityTypeString the entity type as a string
    * @return the EntityType or null if an exception occurred
    */
   @Nullable
   public EntityType entityTypeFromString(@NotNull String entityTypeString)
   {
      EntityType entityType = null;

      try
      {
         entityType = EntityType.valueOf(entityTypeString);
      }
      catch (Exception e)
      {
         printDebugMessage("Failed to parse EntityType " + entityTypeString, e, null, true);
      }
      return entityType;
   }

   /**
    * Determine if a location is within a radius of another location
    *
    * @param sourceLocation the source location
    * @param checkLocation  the location to check
    * @param radius         the radius from the source location
    * @return true if checkLocation is in the radius of sourceLocation
    */
   public boolean isInside(@NotNull Location sourceLocation, @NotNull Location checkLocation, int radius)
   {
      double distance;

      if (sourceLocation.getWorld() != checkLocation.getWorld())
         return false;

      try
      {
         distance = checkLocation.distance(sourceLocation);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         return false;
      }

      return (distance < radius);
   }

   /**
    * Generate a random Cat type.
    *
    * @return the Cat type
    */
   @NotNull
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
   @NotNull
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
   @NotNull
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
   @NotNull
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
   @NotNull
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
   @NotNull
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
   @NotNull
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
   @NotNull
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
   @NotNull
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
   @NotNull
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
   @NotNull
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
   @NotNull
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
   @NotNull
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
   @NotNull
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
   @NotNull
   public Collection<Entity> getEntitiesInRadius(@NotNull Location location, double radius)
   {
      return getEntitiesInBounds(location, radius, radius, radius);
   }

   /**
    * Gets all living entities within a radius of a specific location
    *
    * @param location the location to search for entities from
    * @param radius   - radius within which to get entities
    * @return List of living entities within the radius
    */
   @NotNull
   public List<LivingEntity> getLivingEntitiesInRadius(@NotNull Location location, double radius)
   {
      Collection<Entity> entities = getEntitiesInRadius(location, radius);
      List<LivingEntity> close = new ArrayList<>();

      for (Entity e : entities)
      {
         if (e instanceof LivingEntity)
         {
            close.add(((LivingEntity) e));
         }
      }

      return close;
   }

   /**
    * Gets all of a specific entity type within a radius of a specific location
    *
    * @param location   - the location to search for entities from
    * @param radius     - radius within which to get entities
    * @param entityType - the type of entity to look for
    * @return List of entities of the specified type within the radius
    */
   @NotNull
   public List<Entity> getTypedCloseEntities(@NotNull Location location, double radius, @NotNull EntityType entityType)
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
   @NotNull
   public List<Block> getBlocksInRadius(@NotNull Location loc, double radius)
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
   @NotNull
   public String enumRecode(@NotNull String s)
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
   @NotNull
   public String firstLetterCapitalize(@NotNull String str)
   {
      StringBuilder sb = new StringBuilder();
      String[] wordList = str.split(" ");
      for (String s : wordList)
      {
         sb.append(s.substring(0, 1).toUpperCase());
         if (s.length() > 1)
         {
            sb.append(s.substring(1).toLowerCase());
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
   public boolean isInvisibilityCloak(@NotNull ItemStack held)
   {
      if (held.getType() == Material.CHAINMAIL_CHESTPLATE)
      {
         ItemMeta meta = held.getItemMeta();
         if (meta == null)
            return false;

         if (held.getItemMeta().hasLore())
         {
            List<String> lore = held.getItemMeta().getLore();
            if (lore == null)
               return false;

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
    * @param location    the Location to serialize
    * @param labelPrefix the prefix for the label string, assumes not empty or null
    * @return a map of the serialized OLocation data
    */
   @Nullable
   public Map<String, String> serializeLocation(@NotNull Location location, @NotNull String labelPrefix)
   {
      Map<String, String> locData = new HashMap<>();

      //
      // Location world
      //
      if (location.getWorld() == null)
      {
         printDebugMessage("serializeLocation: location world is null", null, null, false);
         return null;
      }
      locData.put(labelPrefix + "_" + locationWorldLabel, location.getWorld().getName());

      //
      //Location x, y, z
      locData.put(labelPrefix + "_" + locationXLabel, Double.toString(location.getX()));
      locData.put(labelPrefix + "_" + locationYLabel, Double.toString(location.getY()));
      locData.put(labelPrefix + "_" + locationZLabel, Double.toString(location.getZ()));

      return locData;
   }

   /**
    * Returns a Location from serialized data.
    *
    * @return the location if the data was successfully read, null otherwise
    */
   @Nullable
   public Location deserializeLocation(@NotNull Map<String, String> locData, @NotNull String labelPrefix)
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
            printDebugMessage("Unable to deserialize location", exception, null, true);
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
   public boolean isWand(@NotNull ItemStack stack)
   {
      if (stack.getType() == O2ItemType.WAND.getMaterial() || stack.getType() == O2ItemType.ELDER_WAND.getMaterial())
      {
         ItemMeta meta = stack.getItemMeta();
         if (meta == null)
            return false;

         if (stack.getItemMeta().hasLore())
         {
            List<String> lore = stack.getItemMeta().getLore();
            if (lore == null)
               return false;

            return (lore.get(0).split(" and ").length == 2);
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
   public boolean isBroom(@NotNull ItemStack item)
   {
      if (item.getType() == O2ItemType.BROOMSTICK.getMaterial())
      {
         if (item.containsEnchantment(Enchantment.PROTECTION_FALL))
         {
            ItemMeta meta = item.getItemMeta();
            if (meta == null)
               return false;

            if (meta.hasLore())
            {
               List<String> lore = meta.getLore();
               if (lore == null)
                  return false;

               return lore.contains(O2ItemType.BROOMSTICK.getLore());
            }
         }
      }
      return false;
   }

   /**
    * Gets item entities within bounding box of the projectile
    *
    * @return List of item entities within bounding box of projectile
    */
   @NotNull
   public List<Item> getItemsInBounds(@NotNull Location location, double x, double y, double z)
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
    * Gets item entities within radius of the projectile
    *
    * @return List of item entities within radius of projectile
    */
   @NotNull
   public List<Item> getItemsInRadius(@NotNull Location location, double radius)
   {
      return getItemsInBounds(location, radius, radius, radius);
   }

   /**
    * Get all the entities with a bounding box of a specific location.
    *
    * @param location the location to check from
    * @param x        the distance +/- on the x-plane
    * @param y        the distance +/- on the y-plane
    * @param z        the distance +/- on the z-plane
    * @return a Collection of all entities within the bounding box.
    */
   @NotNull
   public Collection<Entity> getEntitiesInBounds(@NotNull Location location, double x, double y, double z)
   {
      World world = location.getWorld();
      if (world == null)
         return new ArrayList<>();

      return world.getNearbyEntities(location, x, y, z);
   }

   /**
    * Convert an array of strings to a single string.
    *
    * @param strArray the array to convert
    * @return the array as a single string
    */
   @NotNull
   public String stringArrayToString(@NotNull String[] strArray)
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
   @NotNull
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
   @NotNull
   public List<ItemStack> getAllWands()
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
      ItemStack wand = Ollivanders2API.getItems(p).getItemByType(O2ItemType.WAND, 1);
      if (wand == null)
         return null;

      lore.add(wood + O2PlayerCommon.wandLoreConjunction + core);
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
   public Block playerFacingBlockType(@NotNull Player player, @NotNull Material blockType)
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
   @NotNull
   public ItemStack getGalleon(int amount)
   {
      ItemStack galleon = new ItemStack(O2ItemType.GALLEON.getMaterial());
      ItemMeta meta = galleon.getItemMeta();

      if (meta == null)
      {
         printDebugMessage("getGalleon: item meta is null", null, null, false);
         return galleon;
      }

      meta.setDisplayName(O2ItemType.GALLEON.getName());
      galleon.setItemMeta(meta);
      galleon.setAmount(amount);

      return galleon;
   }

   /**
    * Get an item stack of Sickle
    *
    * @param amount the number of sickles to create
    * @return the item stack of sickles
    */
   @NotNull
   public ItemStack getSickle(int amount)
   {
      ItemStack sickle = new ItemStack(O2ItemType.SICKLE.getMaterial());
      ItemMeta meta = sickle.getItemMeta();

      if (meta == null)
      {
         printDebugMessage("getSickle: item meta is null", null, null, false);
         return sickle;
      }

      meta.setDisplayName(O2ItemType.SICKLE.getName());
      sickle.setItemMeta(meta);
      sickle.setAmount(amount);

      return sickle;
   }

   /**
    * Get an item stack of Knuts
    *
    * @param amount the number of knuts to create
    * @return the item stack of knuts
    */
   @NotNull
   public ItemStack getKnut(int amount)
   {
      ItemStack knut = new ItemStack(O2ItemType.KNUT.getMaterial());
      ItemMeta meta = knut.getItemMeta();

      if (meta == null)
      {
         printDebugMessage("getKnut: item meta is null", null, null, false);
         return knut;
      }

      meta.setDisplayName(O2ItemType.KNUT.getName());
      knut.setItemMeta(meta);
      knut.setAmount(amount);

      return knut;
   }

   /**
    * Gives a player an item stack
    *
    * @param player the player to give the items to
    * @param kit    the items to give
    */
   public void givePlayerKit(@NotNull Player player, @NotNull List<ItemStack> kit)
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
   public boolean isNaturalLog(@NotNull Block block)
   {
      Material material = block.getType();

      return (material == Material.OAK_LOG || material == Material.BIRCH_LOG || material == Material.ACACIA_LOG
              || material == Material.DARK_OAK_LOG || material == Material.JUNGLE_LOG || material == Material.SPRUCE_LOG);
   }

   /**
    * Makes a particle effect at all points along the radius of
    * spell and at spell loc
    *
    * @param location the location for the center of the flair
    * @param radius the radius of the flair
    * @param intensity intensity of the flair. If greater than 10, is reduced to 10.
    */
   public static void flair(@NotNull Location location, int radius, double intensity)
   {
      flair (location, radius, intensity, Effect.SMOKE);
   }

   /**
    * Makes a particle effect at all points along the radius of
    * spell and at spell loc
    *
    * @param location the location for the center of the flair
    * @param radius the radius of the flair
    * @param intensity intensity of the flair. If greater than 10, is reduced to 10.
    * @param effectType the particle effect to use
    */
   public static void flair(@NotNull Location location, int radius, double intensity, Effect effectType)
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

            if (effectLocation.getWorld() != null)
               effectLocation.getWorld().playEffect(effectLocation, effectType, 4);
         }
      }
   }

   /**
    * Translates vector to spherical coords
    *
    * @param vec - Vector to be translated
    * @return Spherical coords in double array with indexes 0=inclination 1=azimuth
    */
   @NotNull
   public static double[] vecToSpher(@NotNull Vector vec)
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
   @NotNull
   public static Vector spherToVec(@NotNull double[] spher, int radius)
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
    * @param title    the title message
    * @param subtitle the subtitle
    * @param players  message recipients
    */
   public void sendTitleMessage(@NotNull String title, @Nullable String subtitle, @NotNull List<Player> players)
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
   @NotNull
   public List<Player> getAllOnlineSortedPlayers()
   {
      ArrayList<Player> sortedPlayers = new ArrayList<>();

      for (Player player : p.getServer().getOnlinePlayers())
      {
         if (Ollivanders2API.getHouses().isSorted(player))
            sortedPlayers.add(player);
      }

      return sortedPlayers;
   }

   /**
    * Print debug messages to the server log.
    *
    * @param message   the debug message
    * @param e         the exception, if applicable
    * @param r         the runtime exception, if applicable
    * @param isWarning true if this should be at WARNING level, false if it should be INFO level
    */
   public void printDebugMessage(@NotNull String message, @Nullable Exception e, @Nullable RuntimeException r, boolean isWarning)
   {
      if (!Ollivanders2.debug)
         return;

      printLogMessage(message, e, r, isWarning);
   }

   /**
    * Print debug messages to the server log.
    *
    * @param message   the debug message
    * @param e         the exception, if applicable
    * @param r         the runtime exception, if applicable
    * @param isWarning true if this should be at WARNING level, false if it should be INFO level
    */
   public void printLogMessage(@NotNull String message, @Nullable Exception e, @Nullable RuntimeException r, boolean isWarning)
   {
      if (isWarning)
      {
         p.getLogger().warning(message);

         if (e != null)
            e.printStackTrace();

         if (r != null)
            r.printStackTrace();
      }
      else
      {
         p.getLogger().info(message);
      }
   }

   /**
    * Does the item stack match the described item
    *
    * @param itemStack the item stack to check
    * @param material the target material
    * @param name the target item name
    * @param lore the target item lore, match is checked on the 0th index lore string
    * @param amount the amount of the item, < 1 to ignore the amount
    * @return true if the item stack matches, false otherwise
    */
   public boolean matchesItem (@NotNull ItemStack itemStack, @NotNull Material material, @NotNull String name, @NotNull String lore, int amount)
   {
      // check amount
      if (itemStack.getAmount() < 1 || (amount > 0 && (itemStack.getAmount() != amount)))
         return false;

      // check material
      if (itemStack.getType() != material)
         return false;

      // check name and lore
      ItemMeta meta = itemStack.getItemMeta();
      if (meta == null)
         return false;

      String itemName = meta.getDisplayName();
      if (!itemName.equalsIgnoreCase(name))
         return false;

      // don't check lore on written books
      if (material != Material.WRITTEN_BOOK)
      {
         List<String> itemLore = meta.getLore();
         if (itemLore == null || itemLore.size() < 1)
            return false;

         if (!itemLore.get(0).equalsIgnoreCase(lore))
            return false;
      }

      return true;
   }

   /**
    * Are two locations the same?
    *
    * @param loc1 location 1
    * @param loc2 location 2
    * @return true if they are the same, false otherwise
    */
   public boolean locationEquals (@NotNull Location loc1, @NotNull Location loc2)
   {
      return (loc1.getWorld() == loc2.getWorld() && loc1.getX() == loc2.getX() && loc1.getY() == loc2.getY() && loc1.getZ() == loc2.getZ());
   }

   /**
    * Restore a player to full health
    *
    * @param player the player to restore
    */
   public void restoreFullHealth (@NotNull Player player)
   {
      // remove O2Effecs
      Ollivanders2API.getPlayers(p).playerEffects.onDeath(player.getUniqueId());

      // remove other potion effects
      Collection<PotionEffect> potions = player.getActivePotionEffects();
      for (PotionEffect potion : potions)
      {
         player.removePotionEffect(potion.getType());
      }

      // reset health to max
      AttributeInstance playerHealthMax = player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH);
      if (playerHealthMax != null)
         player.setHealth(playerHealthMax.getBaseValue());
   }
}
