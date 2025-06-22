package net.pottercraft.ollivanders2.player.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Event when a player uses a wand that is not their destined wand
 *
 * @author Azami7
 */
public final class OllivandersPlayerNotDestinedWandEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    /**
     * Constructor
     *
     * @param player the player who used a wand that isnt their destined wand
     */
    public OllivandersPlayerNotDestinedWandEvent(@NotNull Player player) {
        super(player);
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
}
