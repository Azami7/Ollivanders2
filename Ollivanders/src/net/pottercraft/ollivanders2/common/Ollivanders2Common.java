package net.pottercraft.ollivanders2.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.potion.O2PotionType;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A collection of common functions and data.
 */

public class Ollivanders2Common
{
    /**
     * Default world label used for serializing and unserializing locations
     */
    private static final String locationWorldLabel = "World";

    /**
     * Default x-coord label used for serializing and unserializing locations
     */
    private static final String locationXLabel = "X-Value";

    /**
     * Default y-coord label used for serializing and unserializing locations
     */
    private static final String locationYLabel = "Y-Value";

    /**
     * Default z-coord label used for serializing and unserializing locations
     */
    private static final String locationZLabel = "Z-Value";

    /**
     * The number of ticks per second for an MC server.
     * <p>
     * Reference: https://minecraft.fandom.com/el/wiki/Tick
     */
    public static final int ticksPerSecond = 20;

    /**
     * The number of ticks per minute for an MC server.
     * <p>
     * Reference: https://minecraft.fandom.com/el/wiki/Tick
     */
    public static final int ticksPerMinute = 20 * 60;

    /**
     * The number of ticks per hour for an MC server.
     * <p>
     * Reference: https://minecraft.fandom.com/el/wiki/Tick
     */
    public static final int ticksPerHour = 20 * 60 * 60;

    /**
     * Spells that use libsDisguises. Used to disable these spells when we detect libsDisguises is not
     * installed on the server.
     */
    public static final List<O2SpellType> libsDisguisesSpells = new ArrayList<>()
    {{
        add(O2SpellType.AMATO_ANIMO_ANIMATO_ANIMAGUS);
        add(O2SpellType.AVIFORS);
        add(O2SpellType.DRACONIFORS);
        add(O2SpellType.DUCKLIFORS);
        add(O2SpellType.ENTOMORPHIS);
        add(O2SpellType.EQUUSIFORS);
        add(O2SpellType.PULLUS);
        add(O2SpellType.EQUUS);
        add(O2SpellType.FELIS);
        add(O2SpellType.LAMA);
        add(O2SpellType.CANIS);
        add(O2SpellType.SUS);
        add(O2SpellType.URSUS);
        add(O2SpellType.BOS);
        add(O2SpellType.LAGOMORPHA);
        add(O2SpellType.SNUFFLIFORS);
        add(O2SpellType.VERA_VERTO);
    }};

    /**
     * Potions that use libsDisguises. Used to disable these potions when we detect libsDisguises is not
     * installed on the server.
     */
    public static final List<O2PotionType> libDisguisesPotions = new ArrayList<>()
    {{
        add(O2PotionType.ANIMAGUS_POTION);
    }};

    /**
     * Unbreakable materials.
     */
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

    /**
     * All chest materials. Newer versions of minecraft have made checks to determine if a block is
     * a chest a lot harder than the olden days.
     */
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

    /**
     * All wall sign materials. Newer versions of minecraft have made checks to determine if a block is
     * a wall sign a lot harder than the olden days.
     */
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

    /**
     * All standing sign materials. Newer versions of minecraft have made checks to determine if a block is
     * a sign a lot harder than the olden days.
     */
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

    /**
     * All sign materials. Newer versions of minecraft have made checks to determine if a block is
     * a sign a lot harder than the olden days.
     */
    public static final List<Material> signs = new ArrayList<>()
    {{
        addAll(wallSigns);
        addAll(standingSigns);
    }};

    /**
     * All door materials. Newer versions of minecraft have made checks to determine if a block is
     * a door a lot harder than the olden days.
     */
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

    /**
     * All trapdoor materials. Newer versions of minecraft have made checks to determine if a block is
     * a trapdoor a lot harder than the olden days.
     */
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

    /**
     * All hotblock materials. To simplify checks to see if a block is a hotblock.
     */
    public static final List<Material> hotBlocks = new ArrayList<>()
    {{
        add(Material.LAVA);
        add(Material.FIRE);
        add(Material.CAMPFIRE);
    }};

    /**
     * For generating random numbers
     */
    public final static Random random = new Random();

    /**
     * Reference to the plugin object
     */
    final private Ollivanders2 p;

    /**
     * Constructor
     *
     * @param plugin a reference to the plugin using this common
     */
    public Ollivanders2Common(@NotNull Ollivanders2 plugin)
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
     * Determine if a location is within a radius of another location
     *
     * @param sourceLocation the source location
     * @param checkLocation  the location to check
     * @param radius         the radius from the source location
     * @return true if checkLocation is in the radius of sourceLocation
     */
    static public boolean isInside(@NotNull Location sourceLocation, @NotNull Location checkLocation, int radius)
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
     * Gets the blocks in a radius of a location.
     *
     * @param loc    The Location that is the center of the block list
     * @param radius The radius of the block list
     * @return List of blocks that are within radius of the location.
     */
    @NotNull
    static public List<Block> getBlocksInRadius(@NotNull Location loc, double radius)
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
    static public String enumRecode(@NotNull String s)
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
     * @param str String to convert.
     * @return String with correct formatting.
     */
    @NotNull
    static public String firstLetterCapitalize(@NotNull String str)
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
     * Serialize a Location
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

        for (Entry<String, String> e : locData.entrySet())
        {
            try
            {
                if (e.getKey().equals(labelPrefix + "_" + locationWorldLabel))
                    worldName = e.getValue();
                else if (e.getKey().equals(labelPrefix + "_" + locationXLabel))
                    x = Double.parseDouble(e.getValue());
                else if (e.getKey().equals(labelPrefix + "_" + locationYLabel))
                    y = Double.parseDouble(e.getValue());
                else if (e.getKey().equals(labelPrefix + "_" + locationZLabel))
                    z = Double.parseDouble(e.getValue());
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
     * Convert an array of strings to a single string.
     *
     * @param strArray the array to convert
     * @return the array as a single string
     */
    @NotNull
    static public String stringArrayToString(@NotNull String[] strArray)
    {
        StringBuilder newString = new StringBuilder();

        for (String str : strArray)
        {
            newString.append(" ").append(str);
        }

        return newString.toString().trim();
    }

    /**
     * Determine if a player is facing a block type.
     *
     * @param player the player to check
     * @return the block if a player is facing a block of this type, null otherwise
     */
    static public Block playerFacingBlockType(@NotNull Player player, @NotNull Material blockType)
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
     * Is this material a natural log (ie. living tree)
     *
     * @param block the block to check
     * @return true if it is a natural log, false otherwise
     */
    static public boolean isNaturalLog(@NotNull Block block)
    {
        Material material = block.getType();

        return (material == Material.OAK_LOG || material == Material.BIRCH_LOG || material == Material.ACACIA_LOG
                || material == Material.DARK_OAK_LOG || material == Material.JUNGLE_LOG || material == Material.SPRUCE_LOG);
    }

    /**
     * Makes a particle effect at all points along the radius of spell and at spell loc.
     *
     * @param location  the location for the center of the flair
     * @param radius    the radius of the flair
     * @param intensity intensity of the flair. If greater than 10, is reduced to 10.
     */
    public static void flair(@NotNull Location location, int radius, double intensity)
    {
        flair(location, radius, intensity, Effect.SMOKE);
    }

    /**
     * Makes a particle effect at all points along the radius of spell and at spell loc.
     *
     * @param location   the location for the center of the flair
     * @param radius     the radius of the flair
     * @param intensity  intensity of the flair. If greater than 10, is reduced to 10.
     * @param effectType the particle effect to use
     */
    public static void flair(@NotNull Location location, int radius, double intensity, Effect effectType)
    {
        if (intensity > 10)
            intensity = 10;

        for (double inc = (Math.random() * Math.PI) / intensity; inc < Math.PI; inc += Math.PI / intensity)
        {
            for (double azi = (Math.random() * Math.PI) / intensity; azi < 2 * Math.PI; azi += Math.PI / intensity)
            {
                double[] spher = new double[2];
                spher[0] = inc;
                spher[1] = azi;
                Location effectLocation = location.clone().add(sphereToVector(spher, radius));

                if (effectLocation.getWorld() != null)
                    effectLocation.getWorld().playEffect(effectLocation, effectType, 4);
            }
        }
    }

    /**
     * Translates vector to spherical coords
     *
     * @param vector Vector to be translated
     * @return Spherical coords in double array with indexes 0=inclination 1=azimuth
     */
    @NotNull
    public static double[] vectorToSphere(@NotNull Vector vector)
    {
        double inc = Math.acos(vector.getZ());
        double azi = Math.atan2(vector.getY(), vector.getX());
        double[] sphere = new double[2];
        sphere[0] = inc;
        sphere[1] = azi;

        return sphere;
    }

    /**
     * Translates spherical coords to vector
     *
     * @param sphere array with indexes 0=inclination 1=azimuth
     * @return Vector
     */
    @NotNull
    public static Vector sphereToVector(@NotNull double[] sphere, int radius)
    {
        double inc = sphere[0];
        double azi = sphere[1];
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
    static public void sendTitleMessage(@NotNull String title, @Nullable String subtitle, @NotNull List<Player> players)
    {
        for (Player player : players)
        {
            player.sendTitle(title, subtitle, 10, 70, 20);
        }
    }

    /**
     * Send a message to all players in a radius of a location
     *
     * @param message  the message to send
     * @param location the location of the message
     * @param radius   the radius around the location
     */
    static public void sendMessageInRadius(@NotNull String message, @NotNull Location location, int radius)
    {
        for (LivingEntity entity : EntityCommon.getLivingEntitiesInRadius(location, radius))
        {
            if (entity instanceof Player)
                entity.sendMessage(Ollivanders2.chatColor + message);
        }
    }

    /**
     * Print debug messages to the server log.
     *
     * @param message          the debug message
     * @param exception        the exception, if applicable
     * @param runtimeException the runtime exception, if applicable
     * @param isWarning        true if this should be at WARNING level, false if it should be INFO level
     */
    public void printDebugMessage(@NotNull String message, @Nullable Exception exception, @Nullable RuntimeException runtimeException, boolean isWarning)
    {
        if (!Ollivanders2.debug)
            return;

        printLogMessage(message, exception, runtimeException, isWarning);
    }

    /**
     * Print debug messages to the server log.
     *
     * @param message          the debug message
     * @param exception        the exception, if applicable
     * @param runtimeException the runtime exception, if applicable
     * @param isWarning        true if this should be at WARNING level, false if it should be INFO level
     */
    public void printLogMessage(@NotNull String message, @Nullable Exception exception, @Nullable RuntimeException runtimeException, boolean isWarning)
    {
        if (isWarning)
        {
            p.getLogger().warning(message);

            if (exception != null)
                exception.printStackTrace();

            if (runtimeException != null)
                runtimeException.printStackTrace();
        }
        else
            p.getLogger().info(message);
    }

    /**
     * Are two locations the same?
     *
     * @param loc1 location 1
     * @param loc2 location 2
     * @return true if they are the same, false otherwise
     */
    static public boolean locationEquals(@NotNull Location loc1, @NotNull Location loc2)
    {
        return (loc1.getWorld() == loc2.getWorld() && loc1.getX() == loc2.getX() && loc1.getY() == loc2.getY() && loc1.getZ() == loc2.getZ());
    }
}
