package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.Ollivanders2;

public class LYCANTHROPY_RELIEF extends O2Effect {
	/**
	 * Constructor
	 *
	 * @param plugin   a callback to the MC plugin
	 * @param duration the duration of the effect
	 * @param pid      the ID of the player this effect acts on
	 */
	public LYCANTHROPY_RELIEF(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
		super(plugin, duration, pid);

		effectType = O2EffectType.LYCANTHROPY_RELIEF;
		informousText = "looks unwell";
	}

	/**
	 * Age this effect each game tick.
	 */
	@Override
	public void checkEffect() {
		age(1);
	}

	/**
	 * Do any cleanup related to removing this effect from the player
	 */
	@Override
	public void doRemove() {
	}
}