package net.pottercraft.ollivanders2.stationaryspell.events;

import net.pottercraft.ollivanders2.stationaryspell.ALIQUAM_FLOO;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class FlooNetworkEvent extends PlayerEvent implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    boolean canceled = false;

    String destinationName;
    Location destination;

    /**
     * Constructor
     *
     * @param player the player traveling
     * @param dest the destination floo fireplace being traveled to
     */
    public FlooNetworkEvent(@NotNull Player player, ALIQUAM_FLOO dest)
    {
        super(player);

        destination = dest.location;
        destinationName = dest.getFlooName();
    }

    /**
     * Get the name of the destination
     *
     * @return the name of the destination being apparated to
     */
    public String getDestinationName ()
    {
        return destinationName;
    }

    /**
     * Get the destination location
     *
     * @return the destination being apparated to
     */
    public Location getDestination ()
    {
        return destination;
    }

    @NotNull
    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    @Override
    public boolean isCancelled()
    {
        return canceled;
    }

    @Override
    public void setCancelled (boolean cancel)
    {
        canceled = cancel;
    }
}
