package net.pottercraft.ollivanders2.spell.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when a player apparates to a named location.
 *
 * @author Azami7
 */
public class OllivandersApparateByNameEvent extends ApparateEvent {
    String destinationName;

    Location destination;

    /**
     * @param player   the player apparating
     * @param dest     the destination being apparated to
     * @param destName the name of the destination being apparated to
     */
    public OllivandersApparateByNameEvent(@NotNull Player player, Location dest, String destName) {
        super(player);

        destination = dest;
        destinationName = destName;
    }

    /**
     * @return the name of the destination being apparated to
     */
    public String getDestinationName() {
        return destinationName;
    }

    /**
     * @return the destination being apparated to
     */
    public Location getDestination() {
        return destination;
    }
}
