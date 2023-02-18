package net.pottercraft.ollivanders2;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;

/**
 * With MC 1.14, triggering PlayerTeleportEvents from other events is no longer
 * thread-safe. Need to create a queue of teleport events like we use for things
 * like spell projectiles and effects.
 *
 * @author Azami7
 * @since 2.4
 */
public class Ollivanders2TeleportEvents {
	final private Ollivanders2 p;
	private Ollivanders2Common common;

	/**
	 * The list of all queued teleport events
	 */
	final private List<O2TeleportEvent> teleportEvents = new ArrayList<>();

	/**
	 * A teleport event
	 */
	static public class O2TeleportEvent {
		/**
		 * The player to teleport
		 */
		final private Player player;

		/**
		 * The location they are teleporting from
		 */
		final private Location fromLocation;

		/**
		 * The location they are teleporting to
		 */
		final private Location toLocation;

		/**
		 * Create explosion effect on teleport
		 */
		private boolean explosionOnTeleport = false;

		/**
		 * Constructor
		 *
		 * @param p    the player teleporting
		 * @param from the location they are teleporting from
		 * @param to   the location they are teleporting to
		 */
		O2TeleportEvent(@NotNull Player p, @NotNull Location from, @NotNull Location to) {
			player = p;
			fromLocation = from;
			toLocation = to;
		}

		/**
		 * Constructor
		 *
		 * @param from      the location they are teleporting from
		 * @param to        the location they are teleporting to
		 * @param explosion should this teleport create an explosion effect when it
		 *                  happens
		 */
		O2TeleportEvent(@NotNull Player p, @NotNull Location from, @NotNull Location to, boolean explosion) {
			player = p;
			fromLocation = from;
			toLocation = to;

			explosionOnTeleport = explosion;
		}

		@NotNull
		public Player getPlayer() {
			return player;
		}

		@NotNull
		public Location getToLocation() {
			return toLocation;
		}

		@NotNull
		public Location getFromLocation() {
			return fromLocation;
		}

		public boolean isExplosionOnTeleport() {
			return explosionOnTeleport;
		}
	}

	/**
	 * Constructor
	 *
	 * @param plugin a callback to the MC plugin
	 */
	public Ollivanders2TeleportEvents(@NotNull Ollivanders2 plugin) {
		p = plugin;
		common = new Ollivanders2Common(p);
	}

	/**
	 * Get all of the teleport events.
	 *
	 * @return an array of the pending teleport events
	 */
	@NotNull
	public List<O2TeleportEvent> getTeleportEvents() {
		return new ArrayList<>(teleportEvents);
	}

	/**
	 * Add a teleport event to the list.
	 *
	 * @param player the player teleporting
	 * @param from   the location they are teleporting from
	 * @param to     the location they are teleporting to
	 */
	public void addTeleportEvent(@NotNull Player player, @NotNull Location from, @NotNull Location to) {
		addTeleportEvent(player, from, to, false);
	}

	/**
	 * Add a teleport event to the list.
	 *
	 * @param player              the player teleporting
	 * @param from                the location they are teleporting from
	 * @param to                  the location they are teleporting to
	 * @param explosionOnTeleport should there be an explosion effect on teleport
	 */
	public void addTeleportEvent(@NotNull Player player, @NotNull Location from, @NotNull Location to,
			boolean explosionOnTeleport) {
		O2TeleportEvent teleportEvent = new O2TeleportEvent(player, from, to, explosionOnTeleport);

		common.printDebugMessage(
				"Created teleport event: " + player.getName() + " from " + from.toString() + " to " + to.toString(),
				null, null, true);
		teleportEvents.add(teleportEvent);

		to.getChunk().load();
	}

	/**
	 * Remove a teleport event from the list.
	 *
	 * @param event the teleport event to remove
	 */
	public void removeTeleportEvent(@NotNull O2TeleportEvent event) {
		if (teleportEvents.contains(event)) {
			common.printDebugMessage("Removing teleport event for " + event.getPlayer().getName(), null, null, false);
			teleportEvents.remove(event);
		} else {
			common.printDebugMessage("Unable to remove teleport event, not found.", null, null, false);
		}
	}
}
