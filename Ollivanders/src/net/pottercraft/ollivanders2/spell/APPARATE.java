package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.GsonDAO;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2WorldGuard;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.events.ApparateEvent;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByCoordinatesEvent;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByNameEvent;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Teleportation spell that allows players to disappear and reappear at a chosen destination.
 *
 * <p>Supports two modes of apparation:</p>
 *
 * <ul>
 * <li>Named locations: Apparate to a pre-registered named location</li>
 * <li>Coordinates: Apparate to specified X, Y, Z coordinates</li>
 * <li>Line of sight: Apparate in the direction the player is looking (up to 160 blocks)</li>
 * </ul>
 *
 * <p>Subject to distance limits, WorldGuard restrictions, and event cancellation.</p>
 *
 * @author Azami7
 * @version Ollivanders2
 */
public final class APPARATE extends O2Spell {
    /**
     * Maximum distance (in blocks) a player can apparate. A value of 0 or less means no limit.
     * Loaded from config on spell cast.
     */
    private static int maxApparateDistance = 0;

    /**
     * Named apparate locations that players can teleport to. Keys are lowercase location names.
     */
    private static HashMap<String, Location> apparateLocations = new HashMap<>();

    /**
     * Command arguments: [0] = spell name, [1..3] = coordinates or location name.
     */
    private final String[] wordsArray;

    /**
     * Name of the apparate location if apparating to a named location.
     */
    private String namedLocation = "unset";

    /**
     * The target location for this apparation.
     */
    private Location destination;

    /**
     * The event fired after all validation, before teleportation occurs.
     */
    private ApparateEvent apparateEvent;

    /**
     * Cached WorldGuard instance for checking location restrictions.
     */
    private Ollivanders2WorldGuard wg = null;

    /**
     * Maximum distance (in blocks) for line-of-sight apparation.
     */
    private static final int maxLineOfSight = 160;

    /**
     * Constructor for spell info generation. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin instance
     */
    public APPARATE(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.APPARATE;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("A magical means of transportation.");
            add("Harry felt Dumbledore's arm twist away from him and re-doubled his grip: the next thing he knew everything went black; he was pressed very hard from all directions; he could not breathe, there were iron bands tightening around his chest; his eyeballs were being forced back into his head; his ear-drums were being pushed deeper into his skull.");
            add("\"We just Apparated, didn't we sir?\"\n\"Yes, and quite successfully too, I might add. Most people vomit the first time.\" -Harry Potter and Albus Dumbledore");
        }};

        if (Ollivanders2.apparateLocations) {
            text = "To apparate to a predetermined location, simply say apparate and list your x, y, and z coordinates. "
                    + "To apparate to the location of your cursor, within 160 meters, just say the word apparate. "
                    + "Your accuracy is determined by the distance traveled and your experience.";
        }
        else {
            text = "To apparate to a predetermined location, simply say apparate and the name of the place you wish to apparate to. "
                    + "To apparate to the location of your cursor, within 160 meters, just say the word apparate. "
                    + "Your accuracy is determined by the distance traveled and your experience.";
        }

        wordsArray = null;
    }

    /**
     * Constructor to cast the apparate spell.
     *
     * @param plugin     the Ollivanders2 plugin instance
     * @param player     the player casting the spell
     * @param rightWand  the wand being used
     * @param wordsArray spell arguments: [0] = "apparate", [1..3] = coordinates or location name
     */
    public APPARATE(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand, @NotNull String[] wordsArray) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.APPARATE;
        branch = O2MagicBranch.CHARMS;

        noProjectile = true;
        this.wordsArray = wordsArray;

        APPARATE.maxApparateDistance = p.getConfig().getInt("maxApparateDistance", 0);

        initSpell();
    }

    /**
     * Validates apparation and schedules the teleport if successful.
     *
     * <p>Performs the following checks in order:</p>
     *
     * <ul>
     * <li>Parses destination from arguments or line-of-sight</li>
     * <li>Checks WorldGuard ENTRY permission at destination</li>
     * <li>Checks WorldGuard EXIT_VIA_TELEPORT permission at source</li>
     * <li>Verifies distance does not exceed maximum</li>
     * </ul>
     *
     * <p>If all checks pass, fires an ApparateEvent and schedules the teleport for 2 ticks later.</p>
     */
    @Override
    protected void doCheckEffect() {
        boolean destinationParsed = false;
        destination = getDestination();

        if (destination != null)
            destinationParsed = true;
        else {
            if (Ollivanders2.apparateLocations) {
                failureMessage = "That location does not exist.";
                sendFailureMessage();
                kill();
                return;
            }
            else
                destination = getLineOfSightLocation();
        }

        // are they allowed to apparate to that location?
        if (!canApparateTo(destination)) {
            failureMessage = "A powerful protective magic blocks your spell.";
            sendFailureMessage();
            kill();
            return;
        }

        // are they allowed to apparate from this location?
        Location source = caster.getLocation();
        if (!canApparateFrom(source)) {
            failureMessage = "A powerful protective magic blocks your spell.";
            sendFailureMessage();
            kill();
            return;
        }

        // does this exceed the max apparate distance?
        if (exceedsMaxDistance(source, destination)) {
            failureMessage = "Your magic is not powerful enough to apparate that far.";
            sendFailureMessage();
            kill();
            return;
        }

        if (Ollivanders2.apparateLocations && destinationParsed)
            apparateEvent = new OllivandersApparateByNameEvent(caster, destination, namedLocation);
        else
            apparateEvent = new OllivandersApparateByCoordinatesEvent(caster, destination);

        p.getServer().getPluginManager().callEvent(apparateEvent);

        // check to see if the event was canceled
        new BukkitRunnable() {
            @Override
            public void run() {
                doApparate();
            }
        }.runTaskLater(p, Ollivanders2Common.ticksPerSecond * 2);

        kill();
    }

    /**
     * Performs the actual teleportation if the apparate event was not canceled.
     */
    private void doApparate() {
        if (!apparateEvent.isCancelled()) {
            common.printDebugMessage("APPARATE.doApparate: adding teleport event", null, null, false);
            p.addTeleportAction(caster, destination, true);
        }
    }

    /**
     * Parses the apparate destination from the spell arguments.
     *
     * <p>If named locations are enabled, expects a location name.
     * Otherwise, expects three numeric coordinates (X, Y, Z).</p>
     *
     * @return the parsed destination, or null if arguments are invalid or incomplete
     */
    @Nullable
    private Location getDestination() {
        Location destination = null;

        if (Ollivanders2.apparateLocations) // player must specify named location
        {
            if (wordsArray.length == 2) {
                namedLocation = wordsArray[1];
                destination = getLocationByName(namedLocation);
            }
        }
        else {
            if (wordsArray.length == 4) {
                // wordsArray[0] = "apparate"
                String xCoordinate = wordsArray[1];
                String yCoordinate = wordsArray[2];
                String zCoordinate = wordsArray[3];
                double x;
                double y;
                double z;

                try {
                    x = Double.parseDouble(xCoordinate);
                    y = Double.parseDouble(yCoordinate);
                    z = Double.parseDouble(zCoordinate);
                    destination = new Location(caster.getWorld(), x, y, z);
                }
                catch (Exception e) {
                }
            }
        }

        return destination;
    }

    /**
     * Determines the apparation destination by tracing the caster's line of sight.
     *
     * <p>Traces forward from the caster's eye location, stopping at the first solid block
     * and returning the location just before it.</p>
     *
     * @return the line-of-sight destination (never null)
     */
    @NotNull
    private Location getLineOfSightLocation() {
        Location currentLocation = caster.getEyeLocation();
        Material material = currentLocation.getBlock().getType();

        int distance = 0;
        while ((projectilePassThrough.contains(material)) && distance < maxLineOfSight) {
            currentLocation = currentLocation.add(currentLocation.getDirection());
            distance = distance + 1;
            material = currentLocation.getBlock().getType();
        }

        return currentLocation.subtract(currentLocation.getDirection()).clone();
    }

    /**
     * Checks if the player has permission to apparate from this location.
     *
     * <p>If WorldGuard is enabled, verifies the EXIT_VIA_TELEPORT flag is allowed.</p>
     *
     * @param source the location to check
     * @return true if apparation is permitted, false otherwise
     */
    private boolean canApparateFrom(@NotNull Location source) {
        // check world guard permissions at location
        if (Ollivanders2.worldGuardEnabled) {
            if (wg == null)
                wg = new Ollivanders2WorldGuard(p);

            if (!wg.checkWGFlag(caster, source, Flags.EXIT_VIA_TELEPORT)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if the player has permission to apparate to this location.
     *
     * <p>If WorldGuard is enabled, verifies the ENTRY flag is allowed.</p>
     *
     * @param destination the target location to check
     * @return true if apparation is permitted, false otherwise
     */
    private boolean canApparateTo(@NotNull Location destination) {
        // check world guard permissions at destination
        if (Ollivanders2.worldGuardEnabled) {
            if (wg == null)
                wg = new Ollivanders2WorldGuard(p);

            if (!wg.checkWGFlag(caster, destination, Flags.ENTRY)) {
                common.printDebugMessage("Player does not have ENTRY permissions to destination", null, null, false);
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if the apparation distance exceeds the configured maximum.
     *
     * @param fromLoc the source location
     * @param toLoc   the destination location
     * @return true if the distance exceeds maxApparateDistance, false otherwise
     */
    private boolean exceedsMaxDistance(@NotNull Location fromLoc, @NotNull Location toLoc) {
        // value less 1 means there is no limit
        if (maxApparateDistance < 1)
            return false;

        // ensure the player is not trying to go more than maxApparateDistance blocks in the x and z directions
        return (fromLoc.distance(toLoc) > maxApparateDistance);
    }

    /**
     * Sends the player a formatted list of all saved apparate locations.
     *
     * @param player the player to send the list to
     */
    public static void listApparateLocations(@NotNull Player player) {
        if (apparateLocations.isEmpty()) {
            player.sendMessage(Ollivanders2.chatColor + "No apparate locations.");
        }
        else {
            StringBuilder builder = new StringBuilder();
            builder.append("Apparate locations:");
            for (Map.Entry<String, Location> entry : apparateLocations.entrySet()) {
                builder.append("\n").append(entry.getKey());
                Location loc = entry.getValue();
                builder.append(" ").append((int) (loc.getX()));
                builder.append(" ").append((int) (loc.getY()));
                builder.append(" ").append((int) (loc.getZ()));
            }

            player.sendMessage(Ollivanders2.chatColor + builder.toString());
        }
    }

    /**
     * Registers a named apparate location that players can teleport to.
     *
     * <p>Blank names are ignored. Names are stored in lowercase.</p>
     *
     * @param name  the location name
     * @param world the world containing the location
     * @param x     x-coordinate
     * @param y     y-coordinate
     * @param z     z-coordinate
     */
    public static void addLocation(@NotNull String name, @NotNull World world, double x, double y, double z) {
        if (name.isBlank())
            return;

        Location location = new Location(world, x, y, z);

        apparateLocations.put(name.toLowerCase(), location);
    }

    /**
     * Removes a named apparate location.
     *
     * @param name the location name (case-insensitive)
     */
    public static void removeLocation(@NotNull String name) {
        apparateLocations.remove(name.toLowerCase());
    }

    /**
     * Checks if a named apparate location exists.
     *
     * @param name the location name (case-insensitive)
     * @return true if the location exists, false otherwise
     */
    public static boolean doesLocationExist(@NotNull String name) {
        return apparateLocations.containsKey(name.toLowerCase());
    }

    /**
     * Retrieves a named apparate location.
     *
     * @param name the location name (case-insensitive)
     * @return the location if found, null otherwise
     */
    @Nullable
    Location getLocationByName(@NotNull String name) {
        return apparateLocations.get(name.toLowerCase());
    }

    /**
     * Returns a defensive copy of all saved apparate locations.
     *
     * @return a map of location names to coordinates (never null)
     */
    @NotNull
    public static Map<String, Location> getAllApparateLocations() {
        return new HashMap<>(apparateLocations);
    }

    /**
     * Removes all saved apparate locations.
     */
    public static void clearApparateLocations() {
        apparateLocations.clear();
    }

    /**
     * Persists all apparate locations to disk (if enabled in config).
     */
    public static void saveApparateLocations() {
        if (!Ollivanders2.apparateLocations)
            return;

        GsonDAO gsonLayer = new GsonDAO();
        gsonLayer.writeApparateData(apparateLocations);
    }

    /**
     * Loads saved apparate locations from disk (if enabled in config).
     *
     * @param p the plugin instance
     */
    public static void loadApparateLocations(Ollivanders2 p) {
        if (!Ollivanders2.apparateLocations)
            return;

        GsonDAO gsonLayer = new GsonDAO();
        HashMap<String, Location> loadedLocations = gsonLayer.readApparateLocation();

        if (loadedLocations != null)
            apparateLocations = loadedLocations;

        p.getLogger().info("Loaded " + apparateLocations.size() + " apparate locations.");
    }

    /**
     * Gets the configured maximum apparate distance.
     *
     * @return the maximum distance in blocks, or 0 if unlimited
     */
    public static int getMaxApparateDistance() {
        return maxApparateDistance;
    }

    /**
     * Gets the maximum distance for line-of-sight apparation.
     *
     * @return the maximum line-of-sight distance in blocks
     */
    public static int getMaxLineOfSight() {
        return maxLineOfSight;
    }
}