package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;

/**
 * Protego totalum is a stationary spell which will prevent any entities from
 * crossing its boundary.
 * <p>
 * https://harrypotter.fandom.com/wiki/Protego_totalum
 *
 * {@link net.pottercraft.ollivanders2.stationaryspell.ShieldSpell}
 */
public final class PROTEGO_TOTALUM extends StationarySpell {
	/**
	 * Default constructor for use in generating spell text. Do not use to cast the
	 * spell.
	 *
	 * @param plugin the Ollivanders2 plugin
	 */
	public PROTEGO_TOTALUM(Ollivanders2 plugin) {
		super(plugin);

		spellType = O2SpellType.PROTEGO_TOTALUM;
		branch = O2MagicBranch.CHARMS;

		flavorText = new ArrayList<>() {
			{
				add("Raising her wand, she began to walk in a wide circle around Harry and Ron, murmuring incantations as she went. Harry saw little disturbances in the surrounding air: it was as if Hermione had cast a heat haze across their clearing.");
			}
		};

		text = "Protego totalum is a stationary spell which will prevent any entities from crossing its boundary.";
	}

	/**
	 * Constructor.
	 *
	 * @param plugin    a callback to the MC plugin
	 * @param player    the player who cast this spell
	 * @param rightWand which wand the player was using
	 */
	public PROTEGO_TOTALUM(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
		super(plugin, player, rightWand);

		spellType = O2SpellType.PROTEGO_TOTALUM;
		branch = O2MagicBranch.CHARMS;

		durationModifierInSeconds = 10;
		radiusModifier = 1;
		flairSize = 10;
		centerOnCaster = true;
		minRadius = net.pottercraft.ollivanders2.stationaryspell.PROTEGO_TOTALUM.minRadiusConfig;
		maxRadius = net.pottercraft.ollivanders2.stationaryspell.PROTEGO_TOTALUM.maxRadiusConfig;
		minDuration = net.pottercraft.ollivanders2.stationaryspell.PROTEGO_TOTALUM.minDurationConfig;
		maxDuration = net.pottercraft.ollivanders2.stationaryspell.PROTEGO_TOTALUM.maxDurationConfig;

		initSpell();
	}

	@Override
	protected O2StationarySpell createStationarySpell() {
		return new net.pottercraft.ollivanders2.stationaryspell.PROTEGO_TOTALUM(p, player.getUniqueId(), location,
				radius, duration);
	}
}