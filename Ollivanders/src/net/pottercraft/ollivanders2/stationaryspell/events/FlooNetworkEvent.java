package net.pottercraft.ollivanders2.stationaryspell.events;

import net.pottercraft.ollivanders2.stationaryspell.ALIQUAM_FLOO;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * A floo network events fire when a player attempts to use a floo fireplace to teleport.
 */
public class FlooNetworkEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    boolean canceled = false;

    /**
     * The name of the teleport destination
     */
    String destinationName;

    /**
     * The location of the teleport destination
     */
    Location destination;

    /**
     * Constructor
     *
     * @param player the player traveling
     * @param dest   the destination floo fireplace being traveled to
     */
    public FlooNetworkEvent(@NotNull Player player, ALIQUAM_FLOO dest) {
        super(player);

        destination = dest.getLocation();
        destinationName = dest.getFlooName();
    }

    /**
     * Get the name of the destination
     *
     * @return the name of the destination being apparated to
     */
    public String getDestinationName() {
        return destinationName;
    }

    /**
     * Get the destination location
     *
     * @return the destination being apparated to
     */
    public Location getDestination() {
        return destination;
    }

    /**
     * Get the handlers for this Event
     *
     * @return the event handlers
     */
    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Get the handlers for this Event
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
