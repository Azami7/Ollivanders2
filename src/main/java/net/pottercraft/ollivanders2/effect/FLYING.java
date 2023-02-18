package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Grants player flight
 */
public class FLYING extends O2Effect {
	/**
	 * Whether to do the smoke effect when player is flying
	 */
	boolean doSmokeEffect = false;

	/**
	 * Constructor
	 *
	 * @param plugin   a callback to the MC plugin
	 * @param duration the duration of the effect
	 * @param pid      the ID of the player this effect acts on
	 */
	public FLYING(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
		super(plugin, duration, pid);

		effectType = O2EffectType.FLYING;
	}

	/**
	 * Allow player to fly until the effect ends.
	 */
	@Override
	public void checkEffect() {
		Player target = p.getServer().getPlayer(targetID);

		if (target != null) {
			age(1);
			if (duration > 1) {
				target.setAllowFlight(true);
				if (doSmokeEffect)
					target.getWorld().playEffect(target.getLocation(), org.bukkit.Effect.SMOKE, 4);
			} else {
				common.printDebugMessage("Adding flight for " + target.getDisplayName(), null, null, false);
				target.setAllowFlight(false);
			}
		} else
			kill();
	}

	/**
	 * Do any cleanup related to removing this effect from the player
	 */
	@Override
	public void doRemove() {
		Player player = p.getServer().getPlayer(targetID);
		if (player != null) {
			// if the player is not an admin, remove flight ability
			if (!player.hasPermission("Ollivanders2.admin")) {
				common.printDebugMessage("Removing flight for " + player.getDisplayName(), null, null, false);
				player.setAllowFlight(false);
				player.setFlying(false);
			}
		}
	}
}