package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Make a player confused
 *
 * @author Azami7
 * @since 2.2.9
 */
public class CONFUSION extends PotionEffectSuper {
	public CONFUSION(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
		super(plugin, duration, pid);

		effectType = O2EffectType.CONFUSION;
		potionEffectType = PotionEffectType.CONFUSION;
		informousText = legilimensText = "feels confused";

		strength = 1;

		divinationText.add("shall be cursed");
		divinationText.add("will be afflicted in the mind");
		divinationText.add("will be struck by a terrible affliction");
		divinationText.add("will suffer a mental breakdown");
	}

	/**
	 * Do any cleanup related to removing this effect from the player
	 */
	@Override
	public void doRemove() {
	}
}