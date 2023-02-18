package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Make a player unlucky
 *
 * @author Azami7
 * @since 2.2.9
 */
public class UNLUCK extends PotionEffectSuper {
	public UNLUCK(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
		super(plugin, duration, pid);

		strength = 1;

		effectType = O2EffectType.UNLUCK;
		potionEffectType = PotionEffectType.UNLUCK;
		informousText = legilimensText = "feels unlucky";

		divinationText.add("will be cursed by misfortune");
		divinationText.add("shall be cursed");
		divinationText.add("will find nothing but misfortune");
	}

	/**
	 * Do any cleanup related to removing this effect from the player
	 */
	@Override
	public void doRemove() {
	}
}
