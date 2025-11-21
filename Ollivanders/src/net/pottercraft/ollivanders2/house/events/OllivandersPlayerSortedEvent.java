package net.pottercraft.ollivanders2.house.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Event fired when a player is sorted into a Hogwarts house.
 *
 * <p>OllivandersPlayerSortedEvent is a custom Bukkit event that fires whenever a player completes
 * the sorting process and is assigned to a house. This event allows plugins to hook into the sorting
 * system and respond when a player is sorted, such as logging the event, applying house-specific
 * effects, or broadcasting the sorting result to other players.</p>
 *
 * @author Azami7
 */
public class OllivandersPlayerSortedEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    /**
     * Constructor for creating a player sorted event.
     *
     * <p>Creates an event that fires when a player is sorted into a house during the sorting process.</p>
     *
     * @param player the player being sorted into a house
     */
    public OllivandersPlayerSortedEvent(@NotNull Player player) {
        super(player);
    }

    /**
     * Get the handler list for this event.
     *
     * <p>Returns the HandlerList that manages all listeners registered for OllivandersPlayerSortedEvent
     * instances. This is required by the Bukkit event system.</p>
     *
     * @return the handler list for this event
     */
    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Get the static handler list for this event type.
     *
     * <p>Returns the static HandlerList for all OllivandersPlayerSortedEvent instances. This method is
     * required by the Bukkit event system for event listener management.</p>
     *
     * @return the static handler list for OllivandersPlayerSortedEvent
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
