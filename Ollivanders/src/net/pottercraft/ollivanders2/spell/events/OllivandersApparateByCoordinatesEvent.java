package net.pottercraft.ollivanders2.spell.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when a player apparates to a set of coordinates.
 *
 * @author Azami7
 */
public class OllivandersApparateByCoordinatesEvent extends ApparateEvent {
    Location destination;

    /**
     * @param player the player apparating
     * @param dest   the destination being apparated to
     */
    public OllivandersApparateByCoordinatesEvent(@NotNull Player player, @NotNull Location dest) {
        super(player);

        destination = dest;
    }

    /**
     * @return the destination being apparated to
     */
    @NotNull
    public Location getDestination() {
        return destination;
    }
}
