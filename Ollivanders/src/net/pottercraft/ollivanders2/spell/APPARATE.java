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
 * Apparition code for players who have over 100 uses in apparition.
 *
 * @author Azami7
 * @version Ollivanders2
 */
public final class APPARATE extends O2Spell
{
    /**
     * The maximum apparate distance, value of 0 or less means no limit
     */
    public static int maxApparateDistance = 0;

    /**
     * Apparate locations
     */
    private static HashMap<String, Location> apparateLocations = new HashMap<>();

    /**
     * The arguments to the apparate spell
     */
    private final String[] wordsArray;
    private String namedLocation = "unset";
    private Location destination;
    private ApparateEvent apparateEvent;

    /**
     * Max distance to apparate for line of sight apparating
     */
    private final int maxLineOfSight = 160;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public APPARATE(Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2SpellType.APPARATE;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>()
        {{
            add("A magical means of transportation.");
            add("Harry felt Dumbledore's arm twist away from him and re-doubled his grip: the next thing he knew everything went black; he was pressed very hard from all directions; he could not breathe, there were iron bands tightening around his chest; his eyeballs were being forced back into his head; his ear-drums were being pushed deeper into his skull.");
            add("\"We just Apparated, didn't we sir?\"\n\"Yes, and quite successfully too, I might add. Most people vomit the first time.\" -Harry Potter and Albus Dumbledore");
        }};

        if (Ollivanders2.apparateLocations)
        {
            text = "To apparate to a predetermined location, simply say apparate and list your x, y, and z coordinates. "
                    + "To apparate to the location of your cursor, within 140 meters, just say the word apparate. "
                    + "Your accuracy is determined by the distance traveled and your experience.";
        }
        else
        {
            text = "To apparate to a predetermined location, simply say apparate and the name of the place you wish to apparate to. "
                    + "To apparate to the location of your cursor, within 140 meters, just say the word apparate. "
                    + "Your accuracy is determined by the distance traveled and your experience.";
        }

        wordsArray = null;
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     * @param wordsArray the arguments to the apparate spell
     */
    public APPARATE(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand, @NotNull String[] wordsArray)
    {
        super(plugin, player, rightWand);
        spellType = O2SpellType.APPARATE;
        branch = O2MagicBranch.CHARMS;

        initSpell();

        this.wordsArray = wordsArray;
    }

    /**
     * Teleport the caster to the location, or close to it, depending on skill level
     */
    @Override
    public void checkEffect()
    {
        if (!isSpellAllowed())
        {
            kill();
            return;
        }

        boolean destinationParsed = false;
        destination = getDestination();

        if (destination != null)
            destinationParsed = true;
        else
        {
            if (Ollivanders2.apparateLocations)
            {
                player.sendMessage(Ollivanders2.chatColor + "That location does not exist.");
                kill();
                return;
            }
            else
                destination = getLineOfSightLocation();
        }

        // are they allowed to apparate to that location?
        if (!canApparateTo(destination))
        {
            p.spellFailedMessage(player);
            kill();
            return;
        }

        // are they allowed to apparate from this location?
        Location source = player.getLocation();
        if (!canApparateFrom(source))
        {
            p.spellFailedMessage(player);
            kill();
            return;
        }

        // does this exceed the max apparate distance?
        if (exceedsMaxDistance(source, destination))
        {
            player.sendMessage(Ollivanders2.chatColor + "Your magic is not powerful enough to apparate that far.");
            kill();
            return;
        }

        if (Ollivanders2.apparateLocations && destinationParsed)
            apparateEvent = new OllivandersApparateByNameEvent(player, destination, namedLocation);
        else
            apparateEvent = new OllivandersApparateByCoordinatesEvent(player, destination);

        p.getServer().getPluginManager().callEvent(apparateEvent);

        // check to see if the event was canceled
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                doApparate();
            }
        }.runTaskLater(p, Ollivanders2Common.ticksPerSecond * 2);

        kill();
    }

    private void doApparate()
    {
        if (!apparateEvent.isCancelled())
        {
            p.getServer().getLogger().info("adding teleport event");
            p.addTeleportEvent(player, destination, true);
        }
    }

    /**
     * Get the apparate destination.
     *
     * @return the destination or null if no destination specified
     */
    @Nullable
    private Location getDestination()
    {
        Location destination = null;

        if (Ollivanders2.apparateLocations) // player must specify named location
        {
            if (wordsArray.length == 2)
            {
                namedLocation = wordsArray[1];
                destination = getLocationByName(namedLocation);
            }
        }
        else
        {
            if (wordsArray.length == 4)
            {
                String xCoord = wordsArray[1];
                String yCoord = wordsArray[2];
                String zCoord = wordsArray[3];
                double x;
                double y;
                double z;

                try
                {
                    x = Double.parseDouble(xCoord);
                    y = Double.parseDouble(yCoord);
                    z = Double.parseDouble(zCoord);
                    destination = new Location(player.getWorld(), x, y, z);
                }
                catch (Exception e) {}
            }
        }

        return destination;
    }

    /**
     * Get a line of sight location for apparating without specifying a location.
     *
     * @return a line of sight location up to max blocks
     */
    @NotNull
    private Location getLineOfSightLocation()
    {
        Location currentLocation = player.getEyeLocation();
        Material material = currentLocation.getBlock().getType();

        int distance = 0;
        while ((projectilePassThrough.contains(material)) && distance < maxLineOfSight)
        {
            currentLocation = currentLocation.add(currentLocation.getDirection());
            distance++;
            material = currentLocation.getBlock().getType();
        }

        return currentLocation.subtract(currentLocation.getDirection()).clone();
    }

    /**
     * Check to see if the player can apparate out of this location.
     *
     * @return true if the player can apparate, false otherwise
     */
    private boolean canApparateFrom(@NotNull Location source)
    {
        // check world guard permissions at location
        if (Ollivanders2.worldGuardEnabled)
        {
            Ollivanders2WorldGuard wg = new Ollivanders2WorldGuard(p);

            if (!wg.checkWGFlag(player, source, Flags.EXIT_VIA_TELEPORT))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Check to see if the player can apparate to the destination
     *
     * @param destination the target location to apparate to
     * @return true if the player can apparate, false otherwise
     */
    private boolean canApparateTo(@NotNull Location destination)
    {
        // check world guard permissions at destination
        if (Ollivanders2.worldGuardEnabled)
        {
            Ollivanders2WorldGuard wg = new Ollivanders2WorldGuard(p);

            if (!wg.checkWGFlag(player, destination, Flags.ENTRY))
            {
                common.printDebugMessage("Player does not have ENTRY permissions to destination", null, null, false);
                return false;
            }
        }

        return true;
    }

    /**
     * Determine if the distance a player is trying to apparate exceeds the max apparate distance in the X or Z coordinates.
     *
     * @param fromLoc the location they are apparating from
     * @param toLoc the location they are apparating to
     * @return true if the locations are less than maxApparateDistance apart, false otherwise
     */
    private boolean exceedsMaxDistance(@NotNull Location fromLoc, @NotNull Location toLoc)
    {
        // value less 1 means there is no limit
        if (maxApparateDistance < 1)
            return false;

        // ensure the player is not trying to go more than maxApparateDistance blocks in the x and z directions
        if (Math.abs(fromLoc.getX() - toLoc.getX()) > maxApparateDistance)
            return true;

        if (Math.abs(fromLoc.getZ() - toLoc.getZ()) > maxApparateDistance)
            return true;

        return false;
    }

    /**
     * List all apparate locations
     *
     * @param player player to display the list to
     */
    public static void listApparateLocations(@NotNull Player player)
    {
        if (apparateLocations.size() < 1)
        {
            player.sendMessage(Ollivanders2.chatColor + "No apparate locations.");
        }
        else
        {
            StringBuilder builder = new StringBuilder();
            builder.append("Apparate locations:");
            for (Map.Entry<String, Location> entry : apparateLocations.entrySet())
            {
                builder.append("\n").append(entry.getKey());
                Location loc = entry.getValue();
                builder.append(" ").append((int)(loc.getX()));
                builder.append(" ").append((int)(loc.getY()));
                builder.append(" ").append((int)(loc.getZ()));
            }

            player.sendMessage(Ollivanders2.chatColor + builder.toString());
        }
    }

    /**
     * Add an apparate location
     *
     * @param name the name of the location to add
     * @param world the location world
     * @param x x-coord
     * @param y y-coord
     * @param z z-coord
     * @return true if successfully added, false otherwise
     */
    public static boolean addLocation(@NotNull String name, @NotNull World world, double x, double y, double z)
    {
        Location location = new Location(world, x, y, z);

        apparateLocations.put(name.toLowerCase(), location);

        return false;
    }

    /**
     * Remove an apparate location
     *
     * @param name the name of the location to remove
     * @return true if successfully removed, false otherwise
     */
    public static void removeLocation(@NotNull String name)
    {
        apparateLocations.remove(name.toLowerCase());
    }

    /**
     * Check if a named apparate location exists.
     *
     * @param name the location name
     * @return true if it exists in the list, false otherwise
     */
    public static boolean doesLocationExist(@NotNull String name)
    {
        return apparateLocations.containsKey(name.toLowerCase());
    }

    /**
     * Return an apparate location by name
     * @param name the location name
     * @return the location if found, null otherwise
     */
    @Nullable
    public Location getLocationByName(@NotNull String name)
    {
        return apparateLocations.get(name.toLowerCase());
    }

    /**
     * Return a map of all apparate locations
     *
     * @return a map of all apparate locations
     */
    @NotNull
    public static Map<String, Location> getAllApparateLocations()
    {
        return new HashMap<>(apparateLocations);
    }

    /**
     * Clears all apparate locations
     */
    public static void clearApparateLocations()
    {
        apparateLocations = new HashMap<>();
    }

    /**
     * Save all apparate locations
     */
    public static void saveApparateLocations()
    {
        if (!Ollivanders2.apparateLocations)
            return;

        GsonDAO gsonLayer = new GsonDAO();
        gsonLayer.writeApparateData(apparateLocations);
    }

    /**
     * Load all saved apparate locations
     *
     * @param p a callback to the plugin
     */
    public static void loadApparateLocations(Ollivanders2 p)
    {
        if (!Ollivanders2.apparateLocations)
            return;

        GsonDAO gsonLayer = new GsonDAO();
        HashMap<String, Location> loadedLoactions = gsonLayer.readApparateLocation();

        if (loadedLoactions != null)
            apparateLocations = loadedLoactions;

        p.getLogger().info("Loaded " + apparateLocations.size() + " apparate locations.");
    }

    @Override
    protected void doCheckEffect() {}
}