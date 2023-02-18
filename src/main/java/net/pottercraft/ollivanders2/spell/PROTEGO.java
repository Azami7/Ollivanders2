package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;

/**
 * The basic protection spell
 * <p>
 * https://harrypotter.fandom.com/wiki/Shield_Charm
 * <p>
 * {@link net.pottercraft.ollivanders2.stationaryspell.ShieldSpell}
 */
public final class PROTEGO extends StationarySpell {
	/**
	 * Default constructor for use in generating spell text. Do not use to cast the
	 * spell.
	 *
	 * @param plugin the Ollivanders2 plugin
	 */
	public PROTEGO(Ollivanders2 plugin) {
		super(plugin);

		spellType = O2SpellType.PROTEGO;
		branch = O2MagicBranch.CHARMS;

		flavorText = new ArrayList<>() {
			{
				add("\"I don't remember telling you to use a Shield Charm... but there is no doubt that it was effective...\" -Severus Snape");
				add("The Shield Charm");
			}
		};

		text = "Protego is a shield spell which, while you are crouching, will cause spells cast at it to bounce off.";
	}

	/**
	 * Constructor.
	 *
	 * @param plugin    a callback to the MC plugin
	 * @param player    the player who cast this spell
	 * @param rightWand which wand the player was using
	 */
	public PROTEGO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
		super(plugin, player, rightWand);

		spellType = O2SpellType.PROTEGO;
		branch = O2MagicBranch.CHARMS;

		durationModifierInSeconds = 1;
		radiusModifier = 1;
		flairSize = 10;
		centerOnCaster = true;
		minRadius = net.pottercraft.ollivanders2.stationaryspell.PROTEGO.minRadiusConfig;
		maxRadius = net.pottercraft.ollivanders2.stationaryspell.PROTEGO.maxRadiusConfig;
		minDuration = net.pottercraft.ollivanders2.stationaryspell.PROTEGO.minDurationConfig;
		maxDuration = net.pottercraft.ollivanders2.stationaryspell.PROTEGO.maxDurationConfig;

		initSpell();
	}

	@Override
	protected O2StationarySpell createStationarySpell() {
		return new net.pottercraft.ollivanders2.stationaryspell.PROTEGO(p, player.getUniqueId(), location, radius,
				duration);
	}
}