package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Give a player a health boost
 *
 * @author Azami7
 * @since 2.2.9
 */
public class HEALTH_BOOST extends PotionEffectSuper {
	public HEALTH_BOOST(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
		super(plugin, duration, pid);

		strength = 1;

		effectType = O2EffectType.HEALTH_BOOST;
		potionEffectType = PotionEffectType.HEALTH_BOOST;
		informousText = legilimensText = "feels stronger";

		divinationText.add("will become stonger");
		divinationText.add("will be blessed by fortune");
		divinationText.add("shall be blessed");
		divinationText.add("will rise to become more powerful");
	}

	/**
	 * Do any cleanup related to removing this effect from the player
	 */
	@Override
	public void doRemove() {
	}
}