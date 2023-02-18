package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.Ollivanders2;

public class MUCUS extends O2Effect {
	/**
	 * Constructor
	 *
	 * @param plugin   a callback to the MC plugin
	 * @param duration the duration of the effect
	 * @param pid      the ID of the player this effect acts on
	 */
	public MUCUS(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
		super(plugin, duration, pid);

		effectType = O2EffectType.MUCUS;
		informousText = "is unnaturally congested";
	}

	/**
	 * Spawn a slime entity on the player's head once per 15 seconds.
	 */
	@Override
	public void checkEffect() {
		age(1);
		if (duration % 300 == 0) {
			Player target = p.getServer().getPlayer(targetID);

			if (target != null) {
				World world = target.getWorld();
				Slime slime = (Slime) world.spawnEntity(target.getEyeLocation(), EntityType.SLIME);
				slime.setSize(1);
			} else
				kill();
		}
	}

	/**
	 * Do any cleanup related to removing this effect from the player
	 */
	@Override
	public void doRemove() {
	}
}