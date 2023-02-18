package net.pottercraft.ollivanders2.book.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.spell.O2SpellType;

public class OllivandersBookLearningSpellEvent extends PlayerEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private O2SpellType spellType;
	boolean canceled = false;

	/**
	 * Constructor
	 *
	 * @param player the player who found their wand
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
