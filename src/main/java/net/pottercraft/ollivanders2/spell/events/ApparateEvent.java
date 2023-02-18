package net.pottercraft.ollivanders2.spell.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public abstract class ApparateEvent extends PlayerEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	boolean canceled = false;

	/**
	 * Constructor
	 *
	 * @param player the player apparating
	 */
	public ApparateEvent(@NotNull Player player) {
		super(player);
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

	@Override
	public void setCancelled(boolean cancel) {
		canceled = cancel;
	}
}
