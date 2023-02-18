package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.Ollivanders2;

public class SPEED_SPEEDIER extends PotionEffectSuper {
	public SPEED_SPEEDIER(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
		super(plugin, duration, pid);

		strength = 2;

		effectType = O2EffectType.SPEED_SPEEDIER;
		potionEffectType = PotionEffectType.SPEED;
		informousText = legilimensText = "is moving very fast";

		divinationText.add("will make haste");
		divinationText.add("will wear the boots of Mercury");
	}

	/**
	 * Do any cleanup related to removing this effect from the player
	 */
	@Override
	public void doRemove() {
	}
}
