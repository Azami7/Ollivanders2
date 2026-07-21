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
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.potion.O2PotionType;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Shared constants and utility methods for Ollivanders2: material classification, location
 * serialization, string formatting, particle effects, messaging, and the plugin-wide {@link Random}.
 * <p>
 * Instance methods use the plugin reference for logging; static methods stand alone.
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
     * @see <a href="https://minecraft.fandom.com/el/wiki/Tick">Minecraft Wiki - Tick</a>
     */
    public static final int ticksPerSecond = 20;

    /**
     * The number of ticks per minute for an MC server.
     *
     * @see <a href="https://minecraft.fandom.com/el/wiki/Tick">Minecraft Wiki - Tick</a>
     */
    public static final int ticksPerMinute = 20 * 60;

    /**
     * The number of ticks per hour for an MC server.
     *
     * @see <a href="https://minecraft.fandom.com/el/wiki/Tick">Minecraft Wiki - Tick</a>
     */
    public static final int ticksPerHour = 20 * 60 * 60;

    /**
     * Number of ticks in a Minecraft day.
     *
     * @see <a href="https://minecraft.fandom.com/el/wiki/Tick">Minecraft Wiki - Tick</a>
     */
    public static final int ticksPerDay = 24000;

    /**
     * The drag factor for players moving underwater.
     *
     * @see <a href="https://learn.microsoft.com/en-us/minecraft/creator/reference/content/entityreference/examples/entitycomponents/minecraftcomponent_water_movement?view=minecraft-bedrock-stable">Entity Documentation - minecraft:water_movement</a>
     */
    public static final double underWaterDragFactor = 0.8;

    /**
     * The vertical drag factor for players through air
     */
    public static final double airVerticalDragFactor = 0.98;

    /**
     * The horizontal drag factor for players through air
     */
    public static final double airHorizontalDragFactor = 0.91;

    /**
     * Spells that use libsDisguises. Used to disable these spells when we detect libsDisguises is not
     * installed on the server.
     */
    private static final List<O2SpellType> libsDisguisesSpells = new ArrayList<>() {{
        add(O2SpellType.AGNIFORS);
        add(O2SpellType.AMATO_ANIMO_ANIMATO_ANIMAGUS);
        add(O2SpellType.AVIFORS);
        add(O2SpellType.BOS);
        add(O2SpellType.BOVIFORS);
        add(O2SpellType.CANIFORS);
        add(O2SpellType.CANIS);
        add(O2SpellType.DRACONIFORS);
        add(O2SpellType.DUCKLIFORS);
        add(O2SpellType.ENTOMORPHIS);
        add(O2SpellType.EQUIFORS);
        add(O2SpellType.EQUUS);
        add(O2SpellType.FELIFORS_MEDIUS);
        add(O2SpellType.FELIS);
        add(O2SpellType.LAMA);
        add(O2SpellType.LAPIFORS_MEDIUS);
        add(O2SpellType.PULLUS);
        add(O2SpellType.SUIFORS);
        add(O2SpellType.SUS);
        add(O2SpellType.URSIFORS);
        add(O2SpellType.URSUS);

        add(O2SpellType.VERA_VERTO);
    }};

    /**
     * Potions that use libsDisguises. Used to disable these potions when we detect libsDisguises is not
     * installed on the server.
     */
    private static final List<O2PotionType> libDisguisesPotions = new ArrayList<>() {{
        add(O2PotionType.ANIMAGUS_POTION);
    }};

    /**
     * Unbreakable materials.
     */
    private static final List<Material> unbreakableMaterials = new ArrayList<>() {{
        add(Material.BARRIER);
        add(Material.BEDROCK);
        add(Material.ENDER_CHEST);
    }};

    /**
     * All chest materials, precomputed so per-block chest checks don't need version-specific logic.
     */
    private static final List<Material> chestBlocks = new ArrayList<>();

    /**
     * All wall sign materials, precomputed so per-block sign checks don't need version-specific logic.
     */
    private static final List<Material> wallSigns = new ArrayList<>();

    /**
     * All standing sign materials, precomputed so per-block sign checks don't need version-specific logic.
     */
    private static final List<Material> standingSigns = new ArrayList<>();

    /**
     * All hanging sign materials.
     */
    private static final List<Material> hangingSigns = new ArrayList<>();

    /**
     * All door materials, precomputed so per-block door checks don't need version-specific logic.
     */
    private static final List<Material> doors = new ArrayList<>();

    /**
     * All trapdoor materials, precomputed so per-block trapdoor checks don't need version-specific logic.
     */
    public static final List<Material> trapdoors = new ArrayList<>();

    /**
     * All gate materials.
     */
    private static final List<Material> gates = new ArrayList<>();

    /**
     * All hotblock materials. To simplify checks to see if a block is a hotblock.
     */
    private static final List<Material> hotBlocks = new ArrayList<>() {{
        add(Material.LAVA);
        add(Material.FIRE);
        add(Material.CAMPFIRE);
        add(Material.MAGMA_BLOCK);
        add(Material.SOUL_CAMPFIRE);
        add(Material.SOUL_FIRE);
    }};

    /**
     * All natural wood log and nether stem materials, precomputed for wood-block checks.
     */
    private static final List<Material> naturalLogs = new ArrayList<>();

    /**
     * Shared Random for the whole plugin. All components should use this rather than creating their own.
     */
    public final static Random random = new Random();

    /**
     * Reference to the plugin object.
     */
    final private Ollivanders2 p;

    /**
     * Populate the material lists from the materials the running server has loaded, so classification adapts to the
     * MC version's block types. Idempotent: each list is filled only if currently empty.
     */
    public static void initMaterialLists() {
        if (chestBlocks.isEmpty()) {
            chestBlocks.add(Material.CHEST); // special case, we don't want to use the regex "CHEST" in case some future block could match that but not be a chest (like "richest")
            chestBlocks.addAll(getAllMaterialsThatEndWith("_CHEST"));
            chestBlocks.addAll(Tag.SHULKER_BOXES.getValues());
        }

        if (wallSigns.isEmpty()) {
            wallSigns.addAll(Tag.WALL_SIGNS.getValues());
        }

        if (hangingSigns.isEmpty()) {
            hangingSigns.addAll(Tag.ALL_HANGING_SIGNS.getValues());
        }

        if (standingSigns.isEmpty()) {
            standingSigns.addAll(Tag.STANDING_SIGNS.getValues());
        }

        if (trapdoors.isEmpty()) {
            trapdoors.addAll(Tag.TRAPDOORS.getValues());
        }

        if (gates.isEmpty()) {
            gates.addAll(Tag.FENCE_GATES.getValues());
        }

        if (doors.isEmpty()) {
            doors.addAll(Tag.DOORS.getValues());
        }

        if (naturalLogs.isEmpty()) {
            naturalLogs.addAll(getAllMaterialsThatEndWith("_LOG"));
            naturalLogs.addAll(getAllMaterialsThatEndWith("_STEM"));
        }
    }

    /**
     * Get all registered materials whose name ends with the given suffix.
     *
     * @param endsWith the suffix to match against each material's name, e.g. "_LOG"
     * @return the matching materials; empty if none match
     */
    @NotNull
    public static List<Material> getAllMaterialsThatEndWith(@NotNull String endsWith) {
        ArrayList<Material> materials = new ArrayList<>();

        for (Material material : Material.values()) {
            if (material.toString().endsWith(endsWith)) {
                materials.add(material);
            }
        }

        return materials;
    }

    /**
     * Reseeds the shared {@link #random} with the current system time so sequences differ across plugin reloads.
     *
     * @param plugin a reference to the Ollivanders2 plugin instance, used for logging
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
     * Parse a boolean from a string.
     *
     * @param boolString the boolean as a string
     * @return true if the string equals "true" ignoring case, false otherwise
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
     * Transform an enum name into a human-readable string, e.g. "AVADA_KEDAVRA" -> "avada kedavra".
     *
     * @param s the enum name, typically in CONSTANT_CASE
     * @return the name as lowercase words separated by spaces
     */
    @NotNull
    static public String enumRecode(@NotNull String s) {
        String[] words = s.toLowerCase().split("_");
        return String.join(" ", words);
    }

    /**
     * Convert a space-separated string to title case, e.g. "hello world" -> "Hello World", "HELLO" -> "Hello".
     *
     * @param str the space-separated string to convert
     * @return the string with each word's first letter uppercased and the rest lowercased
     */
    @NotNull
    static public String firstLetterCapitalize(@NotNull String str) {
        StringBuilder sb = new StringBuilder();
        for (String s : str.split(" ")) {
            // skip empty tokens from leading, trailing, or repeated spaces so substring(0, 1) can't overrun
            if (s.isEmpty())
                continue;

            sb.append(s.substring(0, 1).toUpperCase());
            if (s.length() > 1) {
                sb.append(s.substring(1).toLowerCase());
            }
            sb.append(" ");
        }

        // sb is empty when str had no words; otherwise drop the trailing space
        return sb.isEmpty() ? "" : sb.substring(0, sb.length() - 1);
    }

    /**
     * Serialize a Location into a string map keyed by "{labelPrefix}_{field}".
     *
     * @param location    the Location to serialize
     * @param labelPrefix the prefix for each key; must be non-empty
     * @return the serialized world, x, y, and z values, or null if the location has no world
     */
    @Nullable
    public Map<String, String> serializeLocation(@NotNull Location location, @NotNull String labelPrefix) {
        Map<String, String> locData = new HashMap<>();

        if (location.getWorld() == null) {
            printDebugMessage("serializeLocation: location world is null", null, null, false);
            return null;
        }
        locData.put(labelPrefix + "_" + locationWorldLabel, location.getWorld().getName());

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
     * Spawn SMOKE particles in a spherical shell around a location. No-op if location is null.
     *
     * @param location  the center of the effect; no-op if null
     * @param radius    the radius of the sphere in blocks
     * @param intensity the particle density; higher is denser, values above 10 are capped at 10
     */
    public static void flair(Location location, int radius, int intensity) {
        if (location == null)
            return;

        flair(location, radius, intensity, Particle.SMOKE);
    }

    /**
     * Spawn legacy Bukkit {@link Effect} particles in a spherical shell around a location. No-op if location is null.
     *
     * @param location   the center of the effect; no-op if null
     * @param radius     the radius of the sphere in blocks
     * @param intensity  the particle density; higher is denser, values above 10 are capped at 10
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
     * Spawn Bukkit {@link Particle} particles in a spherical shell around a location. No-op if location is null.
     *
     * @param location     the center of the effect; no-op if null
     * @param radius       the radius of the sphere in blocks
     * @param intensity    the particle density; higher is denser, values above 10 are capped at 10
     * @param particleType the particle type to spawn at each point, e.g. SMOKE, FLAME
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
     * Convert spherical coordinates to a Cartesian vector relative to the origin.
     *
     * @param sphere the {@code [inclination, azimuth]} angles, in radians; inclination is the polar angle from the
     *               Y-axis (0 to π) and azimuth is the horizontal angle (0 to 2π)
     * @param radius the distance from the origin
     * @return the equivalent Cartesian vector
     */
    @NotNull
    public static Vector sphereToVector(double[] sphere, int radius) {
        double inclusion = sphere[0];
        double azimuth = sphere[1];
        double x = radius * Math.sin(inclusion) * Math.cos(azimuth);
        double z = radius * Math.sin(inclusion) * Math.sin(azimuth);
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
     * Remove from the recipient set every player farther than the dropoff distance from the source location, so
     * distant players do not receive spell or magic chat.
     *
     * @param recipients the set of players to filter; players out of range are removed in-place
     * @param dropoff    the maximum distance in blocks for a player to remain a recipient
     * @param location   the source location from which distance is measured
     */
    static public void chatDropoff(Set<Player> recipients, int dropoff, Location location) {
        // iterate a copy so we can remove from the original without a ConcurrentModificationException
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
        return isDoor(block.getType());
    }

    /**
     * Determine if a material is a door
     *
     * @param material the material to check
     * @return true if it is a door, false otherwise
     */
    static public boolean isDoor(Material material) {
        return doors.contains(material) || trapdoors.contains(material) || gates.contains(material);
    }

    /**
     * Get all the doors - doors, trapdoors, gates
     *
     * @return the list of doors
     */
    static public List<Material> getDoors() {
        ArrayList<Material> allDoors = new ArrayList<>();

        allDoors.addAll(doors);
        allDoors.addAll(trapdoors);
        allDoors.addAll(gates);

        return allDoors;
    }

    /**
     * Get all the strict doors - ends in "_DOOR", not trapdoor or gates
     *
     * @return the list of doors
     */
    static public List<Material> getDoorsStrict() {
        return new ArrayList<>(doors);
    }

    /**
     * Determine if a block is a chest
     *
     * @param block the block to check
     * @return true if it is a chest, false otherwise
     */
    static public boolean isChest(Block block) {
        return isChest(block.getType());
    }

    /**
     * Determine if a material is a chest
     *
     * @param material the material to check
     * @return true if it is a chest, false otherwise
     */
    static public boolean isChest(Material material) {
        return chestBlocks.contains(material);
    }

    /**
     * Determine if a block is a sign
     *
     * @param block the block to check
     * @return true if it is a sign, false otherwise
     */
    static public boolean isSign(Block block) {
        return isSign(block.getType());
    }

    /**
     * Determine if a material is a sign
     *
     * @param material the material to check
     * @return true if it is a sign, false otherwise
     */
    static public boolean isSign(Material material) {
        return wallSigns.contains(material) || standingSigns.contains(material) || hangingSigns.contains(material);
    }

    /**
     * Determine if a block is a wall sign
     *
     * @param block the block to check
     * @return true if it is a wall sign, false otherwise
     */
    static public boolean isWallSign(Block block) {
        return isWallSign(block.getType());
    }

    /**
     * Determine if a material is a wall sign
     *
     * @param material the material to check
     * @return true if it is a wall sign, false otherwise
     */
    static public boolean isWallSign(Material material) {
        return wallSigns.contains(material);
    }

    /**
     * Determine if a block is a hot block
     *
     * @param block the block to check
     * @return true if it is a hot block, false otherwise
     */
    static public boolean isHotBlock(Block block) {
        return isHotBlock(block.getType());
    }

    /**
     * Determine if a material is a hot block
     *
     * @param material the material to check
     * @return true if it is a hot block, false otherwise
     */
    static public boolean isHotBlock(Material material) {
        return hotBlocks.contains(material);
    }

    /**
     * Get a list of all the hot blocks in the game. Example: fires, lava, magma
     *
     * @return the list of hot blocks
     */
    static public List<Material> getHotBlocks() {
        return new ArrayList<>() {{
            addAll(hotBlocks);
        }};
    }

    /**
     * Determine if a block is a natural log
     *
     * @param block the block to check
     * @return true if it is a natural log, false otherwise
     */
    static public boolean isNaturalLog(Block block) {
        return isNaturalLog(block.getType());
    }

    /**
     * Determine if a material is a natural log
     *
     * @param material the material to check
     * @return true if it is a natural log, false otherwise
     */
    static public boolean isNaturalLog(Material material) {
        return naturalLogs.contains(material);
    }

    /**
     * Determine if a spell requires LibsDisguises
     *
     * @param spellType the spell type to check
     * @return true if it requires LibsDisguises, false otherwise
     */
    static public boolean requiresLibsDisguises(O2SpellType spellType) {
        return libsDisguisesSpells.contains(spellType);
    }

    /**
     * Determine if a potion requires LibsDisguises
     *
     * @param potionType the potion type to check
     * @return true if it requires LibsDisguises, false otherwise
     */
    static public boolean requiresLibsDisguises(O2PotionType potionType) {
        return libDisguisesPotions.contains(potionType);
    }

    /**
     * Get all the unbreakbable materials.
     *
     * @return the list of unbreakable materials
     */
    static public List<Material> getUnbreakableMaterials() {
        return new ArrayList<>(unbreakableMaterials);
    }

    /**
     * Calculate the velocity needed to travel a target distance in a target direction with a given drag factor.
     *
     * @param source     the location to calculate direction from
     * @param distance   the number of blocks to travel
     * @param dragFactor the drag factor
     * @param towards    is the velocity towards the source (pull) or away (push)
     * @return the calculated velocity needed to reach the target distance
     */
    public static Vector calculateVelocityForDistance(Location source, double distance, double dragFactor, boolean towards) {
        Vector velocity = source.getDirection();

        if (towards) {
            velocity.multiply(-1);
        }

        // an exact solution requires calculus; this approximation is good enough for our purposes
        double magnitude = distance + (distance * (1 - dragFactor));

        Ollivanders2API.common.printDebugMessage("magnitude = " + magnitude, null, null, false);

        return velocity.multiply(magnitude);
    }

    /**
     * Calculate the velocity needed to travel in +/-Y a target distance with a given drag factor.
     *
     * @param distance   the number of blocks to travel
     * @param dragFactor the drag factor
     * @param upwards    is the velocity in the +Y direction, else -Y
     * @return the calculated velocity vector
     */
    public static Vector calculateVerticalVelocity(double distance, double dragFactor, boolean upwards) {
        // an exact solution requires calculus; this approximation is good enough for our purposes
        double magnitude = distance + (distance * (1 - dragFactor));

        Ollivanders2API.common.printDebugMessage("magnitude = " + magnitude, null, null, false);

        Vector velocity = new Vector(0, magnitude, 0);

        if (!upwards) {
            velocity.setY(-magnitude);
        }

        return velocity;
    }
}
