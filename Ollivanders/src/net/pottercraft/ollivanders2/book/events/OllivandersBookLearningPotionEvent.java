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

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    public O2PotionType getPotionType() {
        return potionType;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        canceled = cancel;
    }
}
