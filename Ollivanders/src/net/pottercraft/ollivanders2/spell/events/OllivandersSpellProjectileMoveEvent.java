package net.pottercraft.ollivanders2.spell.events;

import net.pottercraft.ollivanders2.spell.O2Spell;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * The event that is triggered when a spell projectile moves
 */
public class OllivandersSpellProjectileMoveEvent extends PlayerEvent implements Cancellable {
    /**
     * event handlers
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * Is this event canceled
     */
    boolean canceled = false;

    /**
     * The spell the projectile is
     */
    O2Spell spell;

    /**
     * Where the projectile moved from
     */
    Location from;

    /**
     * Where the projectile moved to
     */
    Location to;

    /**
     * Constructor
     *
     * @param player the player who created the spell projectile
     * @param spell  the spell projectile
     * @param from   where it moved from
     * @param to     where it moved to
     */
    public OllivandersSpellProjectileMoveEvent(@NotNull Player player, @NotNull O2Spell spell, @NotNull Location from, @NotNull Location to) {
        super(player);

        this.spell = spell;
        this.from = from;
        this.to = to;
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
        if (cancel) {
            canceled = true;
            spell.kill();
        }
    }

    /**
     * Get the location where the projectile moved from
     *
     * @return the locationt the spell moved from
     */
    @NotNull
    public Location getFrom() {
        return from;
    }

    /**
     * Get the location the projectile moved to
     *
     * @return the location the projectile moved to
     */
    @NotNull
    public Location getTo() {
        return to;
    }

    /**
     * Get the spell this projectile is
     *
     * @return the spell for this projectile
     */
    @NotNull
    public O2Spell getSpell() {
        return spell;
    }
}
