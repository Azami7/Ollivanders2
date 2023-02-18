package net.pottercraft.ollivanders2.stationaryspell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.AVADA_KEDAVRA;
import net.pottercraft.ollivanders2.spell.O2Spell;

/**
 * Destroys spell projectiles crossing the boundary.
 * <p>
 * https://harrypotter.fandom.com/wiki/Protego_horribilis
 * <p>
 * {@link net.pottercraft.ollivanders2.spell.PROTEGO_HORRIBILIS}
 */
public class PROTEGO_HORRIBILIS extends ShieldSpell {
	public static final int minRadiusConfig = 5;
	public static final int maxRadiusConfig = 30;
	public static final int minDurationConfig = Ollivanders2Common.ticksPerSecond * 30;
	public static final int maxDurationConfig = Ollivanders2Common.ticksPerMinute * 30;

	/**
	 * Simple constructor used for deserializing saved stationary spells at server
	 * start. Do not use to cast spell.
	 *
	 * @param plugin a callback to the MC plugin
	 */
	public PROTEGO_HORRIBILIS(@NotNull Ollivanders2 plugin) {
		super(plugin);

		spellType = O2StationarySpellType.PROTEGO_HORRIBILIS;
	}

	/**
	 * Constructor
	 *
	 * @param plugin   a callback to the MC plugin
	 * @param pid      the player who cast the spell
	 * @param location the center location of the spell
	 * @param radius   the radius for this spell
	 * @param duration the duration of the spell
	 */
	public PROTEGO_HORRIBILIS(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius,
			int duration) {
		super(plugin);
		spellType = O2StationarySpellType.PROTEGO_HORRIBILIS;
		minRadius = minRadiusConfig;
		maxRadius = maxRadiusConfig;
		minDuration = minDurationConfig;
		maxDuration = maxDurationConfig;

		setPlayerID(pid);
		setLocation(location);
		setRadius(radius);
		setDuration(duration);

		common.printDebugMessage("Creating stationary spell type " + spellType.name(), null, null, false);
	}

	/**
	 * Age the spell by a tick and kill projectiles crossing the boundaries
	 */
	@Override
	public void checkEffect() {
		age();
		List<O2Spell> projectiles = p.getProjectiles();

		List<O2Spell> projectiles2 = new ArrayList<>(projectiles);
		for (O2Spell proj : projectiles2) {
			// https://harrypotter.fandom.com/wiki/Shield_Charm
			// "However, this shield isn't completely impenetrable, as it cannot block a
			// Killing Curse."
			if (proj instanceof AVADA_KEDAVRA)
				continue;

			if (isLocationInside(proj.location)) {
				if (location.distance(proj.location) > radius - 1)
					p.removeProjectile(proj);
			}
		}
	}

	@Override
	@NotNull
	public Map<String, String> serializeSpellData() {
		return new HashMap<>();
	}

	@Override
	public void deserializeSpellData(@NotNull Map<String, String> spellData) {
	}
}