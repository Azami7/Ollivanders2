package net.pottercraft.ollivanders2.spell.events;

import net.pottercraft.ollivanders2.spell.O2Spell;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Fired each time a spell projectile moves from one location to the next.
 */
public class OllivandersSpellProjectileMoveEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    boolean canceled = false;

    O2Spell spell;

    Location from;

    Location to;

    /**
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

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    /**
     * Cancelling the move kills the spell. Passing false is a no-op; a cancelled move cannot be reinstated.
     *
     * @param cancel true to cancel the move and kill the spell
     */
    @Override
    public void setCancelled(boolean cancel) {
        if (cancel) {
            canceled = true;
            spell.kill();
        }
    }

    /**
     * @return the location the projectile moved from
     */
    @NotNull
    public Location getFrom() {
        return from;
    }

    /**
     * @return the location the projectile moved to
     */
    @NotNull
    public Location getTo() {
        return to;
    }

    /**
     * @return the spell this projectile belongs to
     */
    @NotNull
    public O2Spell getSpell() {
        return spell;
    }
}
