package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Burning effect from a flagrante-cursed item.
 */
public class FLAGRANTE_BURNING extends BURNING {
	/**
	 * Constructor
	 *
	 * @param plugin   a callback to the MC plugin
	 * @param duration the duration of the effect
	 * @param pid      the player this effect acts on
	 */
	public FLAGRANTE_BURNING(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
		super(plugin, duration, pid);

		permanent = true;
	}
}
