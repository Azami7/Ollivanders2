package net.pottercraft.ollivanders2.spell.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event thrown whenever a player apparated by coordinates
 *
 * @author Azami7
 */
public class OllivandersApparateByCoordinatesEvent extends ApparateEvent
{
    private static final HandlerList handlers = new HandlerList();
    Location destination;

    /**
     * Constructor
     *
     * @param player the player apparating
     * @param dest the destination being apparated to
     */
    public OllivandersApparateByCoordinatesEvent(@NotNull Player player, Location dest)
    {
        super(player);

        destination = dest;
    }

    /**
     * The apparate destination
     *
     * @return the destination being apparated to
     */
    public Location getDestination ()
    {
        return destination;
    }
}
