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

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    public O2SpellType getSpellType() {
        return spellType;
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
