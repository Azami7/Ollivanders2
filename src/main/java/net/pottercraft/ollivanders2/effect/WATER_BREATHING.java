package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Gives a player water breathing
 *
 * @author Azami7
 * @since 2.2.9
 */
public class WATER_BREATHING extends PotionEffectSuper {
	public WATER_BREATHING(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
		super(plugin, duration, pid);

		strength = 1;

		effectType = O2EffectType.WATER_BREATHING;
		potionEffectType = PotionEffectType.WATER_BREATHING;
		informousText = legilimensText = "can breath in water";

		divinationText.add("will swim with the mermaids");
		divinationText.add("will feel fishy");
		divinationText.add("will no longer fear water");
	}

	/**
	 * Do any cleanup related to removing this effect from the player
	 */
	@Override
	public void doRemove() {
	}
}
