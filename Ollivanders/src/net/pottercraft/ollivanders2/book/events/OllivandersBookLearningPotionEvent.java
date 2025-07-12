package net.pottercraft.ollivanders2.book.events;

import net.pottercraft.ollivanders2.potion.O2PotionType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Event whenever a potion is learned/leveled up from book learning
 */
public class OllivandersBookLearningPotionEvent extends PlayerEvent implements Cancellable {
    /**
     * the handlers for this event
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * the potion type learned
     */
    final private O2PotionType potionType;

    /**
     * whether the event is canceled or not
     */
    boolean canceled = false;

    /**
     * Constructor
     *
     * @param player the player who found their wand
     * @param potion the potion type that was learned
     */
    public OllivandersBookLearningPotionEvent(@NotNull Player player, @NotNull O2PotionType potion) {
        super(player);

        potionType = potion;
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
     * get the potion type that was learned
     *
     * @return the potion type
     */
    @NotNull
    public O2PotionType getPotionType() {
        return potionType;
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
