package net.pottercraft.ollivanders2.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.potion.O2PotionType;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class providing common functionality and constants for Ollivanders2.
 * <p>
 * Contains static collections of Minecraft material constants (doors, trapdoors, chests, signs, etc.),
 * shared static utility methods for location operations, string transformations, particle effects,
 * and player messaging. Also manages the global Random instance for consistent random number generation
 * across the plugin.
 * </p>
 * <p>
 * This class serves as a central repository for shared data structures and utility methods that don't
 * require state-specific to individual game objects. Instance methods use the plugin reference to access
 * the logger and configuration.
 * </p>
 *
 * @author Azami7
 */
public class Ollivanders2Common {
    /**
     * Default world label used for serializing and unserializing locations
     */
    public static final String locationWorldLabel = "World";

    /**
     * Default x-coordinate label used for serializing and unserializing locations
     */
    public static final String locationXLabel = "X-Value";

    /**
     * Default y-coordinate label used for serializing and unserializing locations
     */
    public static final String locationYLabel = "Y-Value";

    /**
     * Default z-coordinate label used for serializing and unserializing locations
     */
    public static final String locationZLabel = "Z-Value";

    /**
     * The number of ticks per second for an MC server.
     *
     * @see <a href="https://minecraft.fandom.com/el/wiki/Tick">https://minecraft.fandom.com/el/wiki/Tick</a>
     */
    public static final int ticksPerSecond = 20;

    /**
     * The number of ticks per minute for an MC server.
     *
     * @see <a href="https://minecraft.fandom.com/el/wiki/Tick">https://minecraft.fandom.com/el/wiki/Tick</a>
     */
    public static final int ticksPerMinute = 20 * 60;

    /**
     * The number of ticks per hour for an MC server.
     *
     * @see <a href="https://minecraft.fandom.com/el/wiki/Tick">https://minecraft.fandom.com/el/wiki/Tick</a>
     */
    public static final int ticksPerHour = 20 * 60 * 60;

    /**
     * Spells that use libsDisguises. Used to disable these spells when we detect libsDisguises is not
     * installed on the server.
     */
    public static final List<O2SpellType> libsDisguisesSpells = new ArrayList<>() {{
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
    public static final List<O2PotionType> libDisguisesPotions = new ArrayList<>() {{
        add(O2PotionType.ANIMAGUS_POTION);
    }};

    /**
     * Unbreakable materials.
     */
    public static final List<Material> unbreakableMaterials = new ArrayList<>() {{
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
    public static final List<Material> chestBlocks = new ArrayList<>() {{
        // chests
        add(Material.CHEST);
        add(Material.ENDER_CHEST);
        add(Material.TRAPPED_CHEST);
        // shulker boxes
        add(Material.BLACK_SHULKER_BOX);
        add(Material.BLUE_SHULKER_BOX);
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
        add(Material.SHULKER_BOX);
        add(Material.YELLOW_SHULKER_BOX);
        add(Material.WHITE_SHULKER_BOX);
    }};

    /**
     * All wall sign materials. Newer versions of minecraft have made checks to determine if a block is
     * a wall sign a lot harder than the olden days.
     */
    public static final List<Material> wallSigns = new ArrayList<>() {{
        add(Material.ACACIA_WALL_SIGN);
        add(Material.BAMBOO_WALL_SIGN);
        add(Material.BIRCH_WALL_SIGN);
        add(Material.CHERRY_WALL_SIGN);
        add(Material.CRIMSON_WALL_SIGN);
        add(Material.DARK_OAK_WALL_SIGN);
        add(Material.JUNGLE_WALL_SIGN);
        add(Material.MANGROVE_WALL_SIGN);
        add(Material.OAK_WALL_SIGN);
        add(Material.PALE_OAK_WALL_SIGN);
        add(Material.SPRUCE_WALL_SIGN);
        add(Material.WARPED_WALL_SIGN);
    }};

    /**
     * All standing sign materials. Newer versions of minecraft have made checks to determine if a block is
     * a sign a lot harder than the olden days.
     */
    public static final List<Material> standingSigns = new ArrayList<>() {{
        add(Material.ACACIA_SIGN);
        add(Material.BAMBOO_SIGN);
        add(Material.BIRCH_SIGN);
        add(Material.CHERRY_SIGN);
        add(Material.CRIMSON_SIGN);
        add(Material.DARK_OAK_SIGN);
        add(Material.JUNGLE_SIGN);
        add(Material.MANGROVE_SIGN);
        add(Material.OAK_SIGN);
        add(Material.PALE_OAK_SIGN);
        add(Material.SPRUCE_SIGN);
        add(Material.WARPED_SIGN);
    }};

    /**
     * All sign materials. Newer versions of minecraft have made checks to determine if a block is
     * a sign a lot harder than the olden days.
     */
    public static final List<Material> signs = new ArrayList<>() {{
        addAll(wallSigns);
        addAll(standingSigns);
    }};

    /**
     * All door materials. Newer versions of minecraft have made checks to determine if a block is
     * a door a lot harder than the olden days.
     */
    public static final List<Material> doors = new ArrayList<>() {{
        add(Material.ACACIA_DOOR);
        add(Material.BAMBOO_DOOR);
        add(Material.BIRCH_DOOR);
        add(Material.CHERRY_DOOR);
        add(Material.COPPER_DOOR);
        add(Material.CRIMSON_DOOR);
        add(Material.DARK_OAK_DOOR);
        add(Material.EXPOSED_COPPER_DOOR);
        add(Material.IRON_DOOR);
        add(Material.JUNGLE_DOOR);
        add(Material.MANGROVE_DOOR);
        add(Material.OAK_DOOR);
        add(Material.OXIDIZED_COPPER_DOOR);
        add(Material.PALE_OAK_DOOR);
        add(Material.SPRUCE_DOOR);
        add(Material.WARPED_DOOR);
        add(Material.WAXED_COPPER_DOOR);
        add(Material.WAXED_EXPOSED_COPPER_DOOR);
        add(Material.WAXED_WEATHERED_COPPER_DOOR);
        add(Material.WAXED_OXIDIZED_COPPER_DOOR);
        add(Material.WEATHERED_COPPER_DOOR);
    }};

    /**
     * All trapdoor materials. Newer versions of minecraft have made checks to determine if a block is
     * a trapdoor a lot harder than the olden days.
     */
    public static final List<Material> trapdoors = new ArrayList<>() {{
        add(Material.ACACIA_TRAPDOOR);
        add(Material.BAMBOO_TRAPDOOR);
        add(Material.BIRCH_TRAPDOOR);
        add(Material.CHERRY_TRAPDOOR);
        add(Material.COPPER_TRAPDOOR);
        add(Material.CRIMSON_TRAPDOOR);
        add(Material.DARK_OAK_TRAPDOOR);
        add(Material.EXPOSED_COPPER_TRAPDOOR);
        add(Material.IRON_TRAPDOOR);
        add(Material.JUNGLE_TRAPDOOR);
        add(Material.MANGROVE_TRAPDOOR);
        add(Material.OAK_TRAPDOOR);
        add(Material.OXIDIZED_COPPER_TRAPDOOR);
        add(Material.PALE_OAK_TRAPDOOR);
        add(Material.SPRUCE_TRAPDOOR);
        add(Material.WARPED_TRAPDOOR);
        add(Material.WAXED_COPPER_TRAPDOOR);
        add(Material.WAXED_EXPOSED_COPPER_TRAPDOOR);
        add(Material.WAXED_OXIDIZED_COPPER_TRAPDOOR);
        add(Material.WAXED_WEATHERED_COPPER_TRAPDOOR);
        add(Material.WEATHERED_COPPER_TRAPDOOR);
    }};

    /**
     * All hotblock materials. To simplify checks to see if a block is a hotblock.
     */
    public static final List<Material> hotBlocks = new ArrayList<>() {{
        add(Material.LAVA);
        add(Material.FIRE);
        add(Material.CAMPFIRE);
        add(Material.MAGMA_BLOCK);
        add(Material.SOUL_CAMPFIRE);
        add(Material.SOUL_FIRE);
    }};

    /**
     * All natural wood log materials in Minecraft.
     * Includes logs from all wood types (oak, birch, spruce, jungle, acacia, dark oak, mangrove, pale oak, cherry)
     * and nether stems (crimson stem, warped stem). Used to simplify checks for wood-based blocks.
     */
    public static final List<Material> naturalLogs = new ArrayList<>() {{
        add(Material.ACACIA_LOG);
        add(Material.BIRCH_LOG);
        add(Material.CHERRY_LOG);
        add(Material.CRIMSON_STEM);
        add(Material.DARK_OAK_LOG);
        add(Material.JUNGLE_LOG);
        add(Material.MANGROVE_LOG);
        add(Material.OAK_LOG);
        add(Material.PALE_OAK_LOG);
        add(Material.SPRUCE_LOG);
        add(Material.WARPED_STEM);
    }};

    /**
     * Global Random instance for generating random numbers across the plugin.
     * <p>
     * Seeded with {@link System#currentTimeMillis()} in the constructor to ensure different sequences
     * across plugin reloads. All plugin components should use this shared instance rather than creating
     * their own to maintain consistency in random number generation.
     * </p>
     */
    public final static Random random = new Random();

    /**
     * Reference to the plugin object
     */
    final private Ollivanders2 p;

    /**
     * Constructor that initializes the common utility class.
     * <p>
     * Stores a reference to the plugin for accessing the logger and configuration, and seeds the global
     * Random instance with the current system time to ensure different random sequences across plugin reloads.
     * </p>
     *
     * @param plugin a reference to the Ollivanders2 plugin instance
     */
    public Ollivanders2Common(@NotNull Ollivanders2 plugin) {
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
    public UUID uuidFromString(@NotNull String uuid) {
        UUID pid = null;

        try {
            pid = UUID.fromString(uuid);
        }
        catch (Exception e) {
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
    public Integer integerFromString(@NotNull String intString) {
        Integer i = null;

        try {
            i = Integer.parseInt(intString);
        }
        catch (Exception e) {
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
    @NotNull
    public Boolean booleanFromString(@NotNull String boolString) {
        return Boolean.valueOf(boolString);
    }

    /**
     * Determine if a location is within a radius of another location
     *
     * @param sourceLocation the source location
     * @param checkLocation  the location to check
     * @param radius         the radius from the source location
     * @return true if checkLocation is in the radius of sourceLocation
     */
    static public boolean isInside(@NotNull Location sourceLocation, @NotNull Location checkLocation, int radius) {
        double distance;

        if (sourceLocation.getWorld() != checkLocation.getWorld())
            return false;

        try {
            distance = checkLocation.distance(sourceLocation);
        }
        catch (Exception e) {
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
    static public List<Block> getBlocksInRadius(@NotNull Location loc, double radius) {
        Block center = loc.getBlock();
        int blockRadius = (int) (radius);
        List<Block> blockList = new ArrayList<>();
        for (int x = -blockRadius; x <= blockRadius; x++) {
            for (int y = -blockRadius; y <= blockRadius; y++) {
                for (int z = -blockRadius; z <= blockRadius; z++) {
                    Block block = center.getRelative(x, y, z);
                    if (block.getLocation().distance(center.getLocation()) < radius) { // make sure we aee still in the radius since we're rounding a square
                        blockList.add(block);
                    }
                }
            }
        }

        return blockList;
    }

    /**
     * Transform an enum name string to a human-readable format.
     * <p>
     * Converts the input to lowercase, splits on underscores, and joins the parts with spaces.
     * For example: "AVADA_KEDAVRA" → "avada kedavra"
     * </p>
     *
     * @param s the enum name as a string (typically in CONSTANT_CASE format)
     * @return a space-separated lowercase string with underscores removed
     */
    @NotNull
    static public String enumRecode(@NotNull String s) {
        String[] words = s.toLowerCase().split("_");
        return String.join(" ", words);
    }

    /**
     * Capitalize the first letter of each word while lowercasing the rest.
     * <p>
     * Transforms each word in the string so that only the first letter is uppercase
     * and all subsequent letters are lowercase. Words are separated by spaces.
     * For example: "hello world" → "Hello World", "HELLO" → "Hello"
     * </p>
     *
     * @param str the string to convert
     * @return a string with proper title case formatting
     */
    @NotNull
    static public String firstLetterCapitalize(@NotNull String str) {
        StringBuilder sb = new StringBuilder();
        String[] wordList = str.split(" ");
        for (String s : wordList) {
            sb.append(s.substring(0, 1).toUpperCase());
            if (s.length() > 1) {
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
    public Map<String, String> serializeLocation(@NotNull Location location, @NotNull String labelPrefix) {
        Map<String, String> locData = new HashMap<>();

        //
        // Location world
        //
        if (location.getWorld() == null) {
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
     * @param locData     the serialized location data
     * @param labelPrefix the label for the location field, ex. "Spell_Loc_Y-Value": "75.49627835773515" the prefix is "Spell_Loc"
     * @return the location if the data was successfully read, null otherwise
     */
    @Nullable
    public Location deserializeLocation(@NotNull Map<String, String> locData, @NotNull String labelPrefix) {
        double x = 0.0;
        double y = 0.0;
        double z = 0.0;
        String worldName = "world";

        // make sure the serialized data contains all keys
        if (!locData.containsKey(labelPrefix + "_" + locationWorldLabel)) {
            printLogMessage("Ollivanders2Common.deserializeLocation(): locData does not contain world name", null, null, true);
            return null;
        }
        else if (!locData.containsKey(labelPrefix + "_" + locationXLabel)) {
            printLogMessage("Ollivanders2Common.deserializeLocation(): locData does not contail X coord", null, null, true);
            return null;
        }
        else if (!locData.containsKey(labelPrefix + "_" + locationYLabel)) {
            printLogMessage("Ollivanders2Common.deserializeLocation(): locData does not contain Y coord", null, null, true);
            return null;
        }
        else if (!locData.containsKey(labelPrefix + "_" + locationZLabel)) {
            printLogMessage("Ollivanders2Common.deserializeLocation(): locData does not contain Z coord", null, null, true);
            return null;
        }

        for (Entry<String, String> e : locData.entrySet()) {
            try {
                if (e.getKey().equals(labelPrefix + "_" + locationWorldLabel))
                    worldName = e.getValue();
                else if (e.getKey().equals(labelPrefix + "_" + locationXLabel))
                    x = Double.parseDouble(e.getValue());
                else if (e.getKey().equals(labelPrefix + "_" + locationYLabel))
                    y = Double.parseDouble(e.getValue());
                else if (e.getKey().equals(labelPrefix + "_" + locationZLabel))
                    z = Double.parseDouble(e.getValue());
            }
            catch (Exception exception) {
                printDebugMessage("Ollivanders2Common.deserializeLocation(): Unable to deserialize location", exception, null, true);
                return null;
            }
        }

        World world = Bukkit.getServer().getWorld(worldName);
        if (world == null) {
            printDebugMessage("Ollivanders2Common.deserializeLocation(): location world is null", null, null, true);
            return null;
        }

        return new Location(world, x, y, z);
    }

    /**
     * Determine if a player is facing a block type.
     *
     * @param player    the player to check
     * @param blockType the block type to check
     * @return the block if a player is facing a block of this type, null otherwise
     */
    static public Block playerFacingBlockType(@NotNull Player player, @NotNull Material blockType) {
        List<Block> blocksInFront = player.getLineOfSight(null, 3);
        Block target = null;

        for (Block block : blocksInFront) {
            if (block.getType() == blockType) {
                target = block;
                break;
            }
        }

        return target;
    }

    /**
     * Create a particle flair effect around a location using SMOKE particles.
     * <p>
     * Spawns particles in a spherical pattern around the given location. The particles are distributed
     * evenly across the sphere using spherical coordinates (inclination and azimuth angles).
     * Defaults to SMOKE particles by calling {@link #flair(Location, int, int, Particle)}.
     * </p>
     *
     * @param location  the center location for the particle effect
     * @param radius    the radius of the sphere in blocks
     * @param intensity the density of particles (0-10+, capped at 10). Higher intensity means more particles.
     */
    public static void flair(Location location, int radius, int intensity) {
        if (location == null)
            return;

        flair(location, radius, intensity, Particle.SMOKE);
    }

    /**
     * Create a particle flair effect around a location using the Bukkit Effect API.
     * <p>
     * Spawns particles in a spherical pattern around the given location using legacy Effect objects.
     * Particles are distributed evenly across the sphere by iterating through spherical coordinates:
     * inclination (polar angle, 0 to π) and azimuth (horizontal angle, 0 to 2π). The step size
     * between points is determined by dividing π (or 2π) by the intensity value.
     * </p>
     *
     * @param location   the center location for the particle effect
     * @param radius     the radius of the sphere in blocks
     * @param intensity  the density of particles (0-10+, capped at 10). Higher intensity means more particles
     *                   spaced closer together
     * @param effectType the legacy Bukkit Effect to spawn at each point
     */
    public static void flair(Location location, int radius, int intensity, Effect effectType) {
        if (location == null)
            return;

        if (intensity > 10)
            intensity = 10;

        // Random offset prevents aligned grid patterns; starts at random point instead of 0
        for (double inclusion = (Math.random() * Math.PI) / intensity; inclusion < Math.PI; inclusion += Math.PI / intensity) {
            for (double azimuth = (Math.random() * Math.PI) / intensity; azimuth < 2 * Math.PI; azimuth += Math.PI / intensity) {
                double[] sphere = new double[2];
                sphere[0] = inclusion;
                sphere[1] = azimuth;
                Location effectLocation = location.clone().add(sphereToVector(sphere, radius));

                if (effectLocation.getWorld() != null)
                    effectLocation.getWorld().playEffect(effectLocation, effectType, 4);
            }
        }
    }

    /**
     * Create a particle flair effect around a location using Bukkit Particle API.
     * <p>
     * Spawns particles in a spherical pattern around the given location. Particles are distributed
     * evenly across the sphere by iterating through spherical coordinates: inclination (polar angle,
     * 0 to π) and azimuth (horizontal angle, 0 to 2π). The step size between points is determined
     * by dividing π (or 2π) by the intensity value. This is the modern particle spawning method.
     * </p>
     *
     * @param location     the center location for the particle effect
     * @param radius       the radius of the sphere in blocks
     * @param intensity    the density of particles (0-10+, capped at 10). Higher intensity means more particles
     *                     spaced closer together
     * @param particleType the particle type to spawn at each point (e.g., SMOKE, FLAME, SPARK)
     */
    public static void flair(Location location, int radius, int intensity, Particle particleType) {
        if (location == null)
            return;

        if (intensity > 10)
            intensity = 10;

        // Random offset prevents aligned grid patterns; starts at random point instead of 0
        for (double inc = (Math.random() * Math.PI) / intensity; inc < Math.PI; inc += Math.PI / intensity) {
            for (double azi = (Math.random() * Math.PI) / intensity; azi < 2 * Math.PI; azi += Math.PI / intensity) {
                double[] sphere = new double[2];
                sphere[0] = inc;
                sphere[1] = azi;
                Location effectLocation = location.clone().add(sphereToVector(sphere, radius));

                if (effectLocation.getWorld() != null)
                    effectLocation.getWorld().spawnParticle(particleType, effectLocation, 4, 0, 0, 0, 0);
            }
        }
    }

    /**
     * Convert spherical coordinates to a Cartesian vector.
     * <p>
     * Transforms spherical coordinates (inclination and azimuth) into a 3D Cartesian vector
     * relative to the origin. The inclination angle is the polar angle measured from the positive Y-axis,
     * and the azimuth angle is measured from the positive X-axis in the horizontal plane.
     * </p>
     * <p>
     * Used by flair effects to distribute particles in a spherical pattern around a center location.
     * </p>
     *
     * @param sphere a 2-element array where:
     *               - sphere[0] is the inclination (polar angle in radians, 0 to π)
     *               - sphere[1] is the azimuth (horizontal angle in radians, 0 to 2π)
     * @param radius the radius of the sphere, determining the distance from the origin
     * @return a Vector representing the 3D position in Cartesian coordinates
     */
    @NotNull
    public static Vector sphereToVector(double[] sphere, int radius) {
        double inclusion = sphere[0];
        double azimuth = sphere[1];
        // X and Z form the horizontal plane (radius from Y-axis), azimuth determines direction
        double x = radius * Math.sin(inclusion) * Math.cos(azimuth);
        double z = radius * Math.sin(inclusion) * Math.sin(azimuth);
        // Y is the vertical component; cos(inclusion) ranges from -1 to 1 as inclusion goes 0 to π
        double y = radius * Math.cos(inclusion);

        return new Vector(x, y, z);
    }

    /**
     * Send a title message to all players.
     *
     * @param title    the title message
     * @param subtitle the subtitle
     * @param players  message recipients
     */
    static public void sendTitleMessage(@NotNull String title, @Nullable String subtitle, @NotNull List<Player> players) {
        for (Player player : players) {
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
    static public void sendMessageInRadius(@NotNull String message, @NotNull Location location, int radius) {
        for (LivingEntity entity : EntityCommon.getLivingEntitiesInRadius(location, radius)) {
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
    public void printDebugMessage(@NotNull String message, @Nullable Exception exception, @Nullable RuntimeException runtimeException, boolean isWarning) {
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
    public void printLogMessage(@NotNull String message, @Nullable Exception exception, @Nullable RuntimeException runtimeException, boolean isWarning) {
        if (isWarning) {
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
    static public boolean locationEquals(@NotNull Location loc1, @NotNull Location loc2) {
        return (loc1.getWorld() == loc2.getWorld() && loc1.getX() == loc2.getX() && loc1.getY() == loc2.getY() && loc1.getZ() == loc2.getZ());
    }

    /**
     * Filter the recipient set to only include players within the dropoff distance.
     * <p>
     * Modifies the recipients set in-place, removing all players who are outside the specified distance
     * from the source location. This is used to implement chat distance limitations where players too far
     * away cannot hear spell or magic-related messages.
     * </p>
     * <p>
     * A copy of the original recipient set is iterated to safely remove players from the original set
     * without causing concurrent modification exceptions.
     * </p>
     *
     * @param recipients the set of players to filter (modified by this method)
     * @param dropoff    the maximum distance in blocks for a player to remain in the recipient set
     * @param location   the source location from which to measure distance
     */
    static public void chatDropoff(Set<Player> recipients, int dropoff, Location location) {
        // handle spell chat dropoff
        Set<Player> temp = new HashSet<>(recipients);
        for (Player recipient : temp) {
            if (!Ollivanders2Common.isInside(location, recipient.getLocation(), dropoff)) {
                try {
                    recipients.remove(recipient);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Determine if a block is a door or trapdoor
     *
     * @param block the block to check
     * @return true if it is a door or trapdoor, false otherwise
     */
    static public boolean isDoor(Block block) {
        Material blockType = block.getType();
        if (doors.contains(blockType) || trapdoors.contains(blockType)) {
            return true;
        }

        return false;
    }

    /**
     * Determine if a block is a chest
     *
     * @param block the block to check
     * @return true if it is a chest, false otherwise
     */
    static public boolean isChest(Block block) {
        Material blockType = block.getType();
        if (chestBlocks.contains(blockType)) {
            return true;
        }

        return false;
    }

    /**
     * Check if a block is adjacent to another block
     *
     * @param block1 the first block
     * @param block2 the second block
     * @return true if the blocks are adjacent, false otherwise
     */
    static public boolean isAdjacentTo(@NotNull Block block1, @NotNull Block block2) {
        // Check all six faces
        return block1.getRelative(BlockFace.UP).equals(block2) ||
                block1.getRelative(BlockFace.DOWN).equals(block2) ||
                block1.getRelative(BlockFace.NORTH).equals(block2) ||
                block1.getRelative(BlockFace.SOUTH).equals(block2) ||
                block1.getRelative(BlockFace.EAST).equals(block2) ||
                block1.getRelative(BlockFace.WEST).equals(block2);
    }
}
