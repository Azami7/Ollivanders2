package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Minor antidote to the awake o2effect
 *
 * @author Azami7
 */
public class AWAKE_ANTIDOTE_LESSER extends O2EffectAntidoteSuper {
	/**
	 * Constructor
	 *
	 * @param plugin   a reference to the plugin for logging
	 * @param duration the duration of this effect - not used for this effect type
	 * @param pid      the target player
	 */
	public AWAKE_ANTIDOTE_LESSER(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
		super(plugin, duration, pid);

		effectType = O2EffectType.AWAKE_ANTIDOTE_LESSER;
		o2EffectType = O2EffectType.AWAKE;
		strength = 0.25;
	}

	/**
	 * Do any cleanup related to removing this effect from the player
	 */
	@Override
	public void doRemove() {
	}
}
