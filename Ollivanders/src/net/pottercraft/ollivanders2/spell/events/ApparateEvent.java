package net.pottercraft.ollivanders2.spell.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * The event triggered when a player apparates
 */
public abstract class ApparateEvent extends PlayerEvent implements Cancellable {
    /**
     * Event handlers
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * Is this event canceled?
     */
    boolean canceled = false;

    /**
     * Constructor
     *
     * @param player the player apparating
     */
    public ApparateEvent(@NotNull Player player) {
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
