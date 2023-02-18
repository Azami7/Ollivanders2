package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.Ollivanders2;

public class SPEED_SPEEDIEST extends PotionEffectSuper {
	public SPEED_SPEEDIEST(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
		super(plugin, duration, pid);

		strength = 4;

		effectType = O2EffectType.SPEED_SPEEDIEST;
		potionEffectType = PotionEffectType.SPEED;
		informousText = legilimensText = "is moving extremely fast";

		divinationText.add("will wear the boots of Mercury");
		divinationText.add("will move with the power of the gods");
	}

	/**
	 * Do any cleanup related to removing this effect from the player
	 */
	@Override
	public void doRemove() {
	}
}