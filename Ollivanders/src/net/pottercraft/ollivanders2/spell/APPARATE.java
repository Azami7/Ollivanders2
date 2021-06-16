package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.GsonDAO;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.Ollivanders2WorldGuard;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
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
    /*
    static
    {
        APPARATE.loadApparateLocations();
    }
     */

    /**
     * Apparate locations
     */
    private final static HashMap<String, Location> apparateLocations = new HashMap<>();

    /**
     * The arguments to the apparate spell
     */
    private final String[] wordsArray;

    /**
     * Max distance to apparate for line of sight apparating
     */
    private final int maxLineOfSight = 160;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     */
    public APPARATE()
    {
        super();

        spellType = O2SpellType.APPARATE;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<String>()
        {{
            add("A magical means of transportation.");
            add("Harry felt Dumbledore's arm twist away from him and re-doubled his grip: the next thing he knew everything went black; he was pressed very hard from all directions; he could not breathe, there were iron bands tightening around his chest; his eyeballs were being forced back into his head; his ear-drums were being pushed deeper into his skull.");
            add("\"We just Apparated, didn't we sir?\"\n\"Yes, and quite successfully too, I might add. Most people vomit the first time.\" -Harry Potter and Albus Dumbledore");
        }};

        text = "Apparition is a two sided spell. To apparate to a predetermined location, simply say apparate and list your x, y, and z coordinates. "
                + "To apparate to the location of your cursor, within 140 meters, just say the word apparate. "
                + "Your accuracy is determined by the distance traveled and your experience. "
                + "If there are any entities close to you when you apparate, they will be taken with you as well by side-along apparition.";

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

        Location destination = getDestination();
        if (!canApparateTo(destination))
        {
            p.spellFailedMessage(player);
            kill();
            return;
        }

        Location source = player.getLocation();
        if (!canApparateFrom(source))
        {
            p.spellFailedMessage(player);
            kill();
            return;
        }

        p.addTeleportEvent(player, player.getLocation(), destination, true);

        // also take nearby players with them
        for (Entity e : player.getWorld().getEntities())
        {
            if (source.distance(e.getLocation()) <= 2)
            {
                if (e instanceof Player)
                {
                    p.addTeleportEvent((Player) e, e.getLocation(), destination, true);
                }
                else
                    e.teleport(destination);
            }
        }

        kill();
    }

    /**
     * Get the apparate destination.
     *
     * @return the destination or null if
     */
    @NotNull
    private Location getDestination ()
    {
        Location destination = null;

        if (Ollivanders2.apparateLocations) // player must specify named location
        {
            if (wordsArray.length == 2)
            {
                destination = getLocationByName(wordsArray[1]);
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
                catch (Exception e) { }
            }
        }

        if (destination == null)
            destination = getLineOfSightLocation();

        return destination;
    }

    /**
     * Get a line of sight location for apparating without specifying a location.
     *
     * @return a line of sight location up to max blocks
     */
    @NotNull
    private Location getLineOfSightLocation ()
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

        // check for Nullum Apparebit at destination
        if (Ollivanders2API.getStationarySpells(p).checkLocationForSpell(player.getLocation(), O2StationarySpellType.NULLUM_EVANESCUNT))
        {
            common.printDebugMessage("Nullum Evanescunt at source blocks teleport.", null, null, false);
            return false;
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

        // check for Nullum Apparebit at destination
        if (Ollivanders2API.getStationarySpells(p).checkLocationForSpell(player.getLocation(), O2StationarySpellType.NULLUM_APPAREBIT))
        {
            common.printDebugMessage("Nullum Apparebit at destination blocks teleport.", null, null, false);
            return false;
        }

        return true;
    }

    /**
     * List all apparate locations
     *
     * @param player player to display the list to
     */
    public static void listApparateLocations (@NotNull Player player)
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
    public static boolean addLocation (@NotNull String name, @NotNull World world, double x, double y, double z)
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
    public static boolean removeLocation (@NotNull String name)
    {
        if (apparateLocations.remove(name.toLowerCase()) == null)
            return false;

        return true;
    }

    /**
     * Check if a named apparate location exists.
     *
     * @param name the location name
     * @return true if it exists in the list, false otherwise
     */
    public static boolean doesLocationExist (@NotNull String name)
    {
        return apparateLocations.containsKey(name.toLowerCase());
    }

    /**
     * Return an apparate location by name
     * @param name the location name
     * @return the location if found, null otherwise
     */
    @Nullable
    public Location getLocationByName (@NotNull String name)
    {
        return apparateLocations.get(name.toLowerCase());
    }

    /**
     * Load saved data for this spell
     */
    public static void saveApparateLocations ()
    {
        if (Ollivanders2.apparateLocations)
        {
            GsonDAO gsonLayer = new GsonDAO();
            gsonLayer.writeApparateData(apparateLocations);
        }
    }
}