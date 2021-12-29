package net.pottercraft.ollivanders2.book.events;

import net.pottercraft.ollivanders2.potion.O2PotionType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class OllivandersBookLearningPotionEvent extends PlayerEvent implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private O2PotionType potionType;
    boolean canceled = false;

    /**
     * Constructor
     *
     * @param player the player who found their wand
     */
    public OllivandersBookLearningPotionEvent(@NotNull Player player, @NotNull O2PotionType spell)
    {
        super(player);

        potionType = spell;
    }

    @NotNull
    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    @NotNull
    public O2PotionType getPotionType()
    {
        return potionType;
    }

    @Override
    public boolean isCancelled()
    {
        return canceled;
    }

    @Override
    public void setCancelled (boolean cancel)
    {
        canceled = cancel;
    }
}
