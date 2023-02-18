package net.pottercraft.ollivanders2.house.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class OllivandersPlayerSortedEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();

	/**
	 * Constructor
	 *
	 * @param player the player who found their wand
	 */
	public OllivandersPlayerSortedEvent(@NotNull Player player) {
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
}
