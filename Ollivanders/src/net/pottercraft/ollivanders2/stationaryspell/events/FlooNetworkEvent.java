package net.pottercraft.ollivanders2.stationaryspell.events;

import net.pottercraft.ollivanders2.stationaryspell.ALIQUAM_FLOO;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when a player attempts to travel through a Floo fireplace to a destination.
 */
public class FlooNetworkEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    boolean canceled = false;

    /**
     * The Floo name of the destination fireplace.
     */
    String destinationName;

    /**
     * The location of the destination fireplace.
     */
    Location destination;

    /**
     * @param player the player traveling
     * @param dest   the destination Floo fireplace being traveled to
     */
    public FlooNetworkEvent(@NotNull Player player, ALIQUAM_FLOO dest) {
        super(player);

        destination = dest.getLocation();
        destinationName = dest.getFlooName();
    }

    /**
     * Get the Floo name of the destination.
     *
     * @return the destination name
     */
    public String getDestinationName() {
        return destinationName;
    }

    /**
     * Get the location of the destination.
     *
     * @return the destination location
     */
    public Location getDestination() {
        return destination;
    }

    /**
     * Get the handler list for this event.
     *
     * @return the event handlers
     */
    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Get the handler list for this event, for Bukkit's static registration.
     *
     * @return the event handlers
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Is this event canceled?
     *
     * @return true if canceled, false otherwise
     */
    @Override
    public boolean isCancelled() {
        return canceled;
    }

    /**
     * Set whether this event is canceled or not
     *
     * @param cancel true if event should be canceled, false otherwise
     */
    @Override
    public void setCancelled(boolean cancel) {
        canceled = cancel;
    }
}
