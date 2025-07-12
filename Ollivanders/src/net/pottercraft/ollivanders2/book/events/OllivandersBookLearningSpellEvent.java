package net.pottercraft.ollivanders2.book.events;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * The event that is fired every time a spell is learned/leveled up from book learning
 */
public class OllivandersBookLearningSpellEvent extends PlayerEvent implements Cancellable {
    /**
     * the handlers for this event
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * the spell learned
     */
    private final O2SpellType spellType;

    /**
     * whether the event is canceled or not
     */
    boolean canceled = false;

    /**
     * Constructor
     *
     * @param player the player who found their wand
     * @param spell  the spell type that was learned
     */
    public OllivandersBookLearningSpellEvent(@NotNull Player player, @NotNull O2SpellType spell) {
        super(player);

        spellType = spell;
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
     * get the spell type that was learned
     *
     * @return the spell type
     */
    @NotNull
    public O2SpellType getSpellType() {
        return spellType;
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
