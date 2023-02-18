package net.pottercraft.ollivanders2.spell.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event thrown whenever a player apparates by location name
 *
 * @author Azami7
 */
public class OllivandersApparateByNameEvent extends ApparateEvent {
	private static final HandlerList handlers = new HandlerList();
	String destinationName;
	Location destination;

	/**
	 * Constructor
	 *
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
	 * Get the name of the apparate destination
	 *
	 * @return the name of the destination being apparated to
	 */
	public String getDestinationName() {
		return destinationName;
	}

	/**
	 * Get the destination being apparated to
	 *
	 * @return the destination being apparated to
	 */
	public Location getDestination() {
		return destination;
	}
}
