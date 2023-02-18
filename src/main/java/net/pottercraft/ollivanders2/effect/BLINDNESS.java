package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Make a player blind
 *
 * @author Azami7
 * @since 2.2.9
 */
public class BLINDNESS extends PotionEffectSuper {
	public BLINDNESS(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
		super(plugin, duration, pid);

		effectType = O2EffectType.BLINDNESS;
		potionEffectType = PotionEffectType.BLINDNESS;
		informousText = legilimensText = "cannot see";

		strength = 1;

		divinationText.add("shall be cursed");
		divinationText.add("shall be afflicted in the mind");
		divinationText.add("will become unable to see");
		divinationText.add("will be struck by a terrible affliction");
	}

	/**
	 * Do any cleanup related to removing this effect from the player
	 */
	@Override
	public void doRemove() {
	}
}