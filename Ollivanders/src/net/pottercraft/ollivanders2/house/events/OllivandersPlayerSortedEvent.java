package net.pottercraft.ollivanders2.house.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when a player is sorted into a Hogwarts house, letting listeners react to the assignment.
 *
 * @author Azami7
 */
public class OllivandersPlayerSortedEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    /**
     * Constructor
     *
     * @param player the player being sorted into a house
     */
    public OllivandersPlayerSortedEvent(@NotNull Player player) {
        super(player);
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Get the handler list for this event type, as required by the Bukkit event system.
     *
     * @return the handler list
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
