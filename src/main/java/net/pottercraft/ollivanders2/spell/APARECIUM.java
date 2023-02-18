package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;

/**
 * The Revealing Charm. Causes any stationary spell objects to flair with an
 * intensity equal to your level.
 * <p>
 * Reference: https://harrypotter.fandom.com/wiki/Revealing_Charm
 */
public final class APARECIUM extends O2Spell {
	/**
	 * Default constructor for use in generating spell text. Do not use to cast the
	 * spell.
	 *
	 * @param plugin the Ollivanders2 plugin
	 */
	public APARECIUM(Ollivanders2 plugin) {
		super(plugin);

		spellType = O2SpellType.APARECIUM;
		branch = O2MagicBranch.CHARMS;

		flavorText = new ArrayList<>() {
			{
				add("The Revealing Charm will reveal invisible ink and messages hidden by magical means. Simply tap a book or parchment with your wand and any hidden message will be revealed. This spell is more than sufficient to overcome the basic concealing charms and so is a favourite of parents and teachers alike.");
				add("The Revealing Charm");
			}
		};

		text = "Causes any area spells to reveal their borders.";
	}

	/**
	 * Constructor.
	 *
	 * @param plugin    a callback to the MC plugin
	 * @param player    the player who cast this spell
	 * @param rightWand which wand the player was using
	 */
	public APARECIUM(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
		super(plugin, player, rightWand);
		spellType = O2SpellType.APARECIUM;
		branch = O2MagicBranch.CHARMS;

		initSpell();
	}

	/**
	 * If any stationary spells are at the location of the spell projectile, make
	 * them flair.
	 */
	@Override
	protected void doCheckEffect() {
		List<O2StationarySpell> stationaries = Ollivanders2API.getStationarySpells()
				.getStationarySpellsAtLocation(location);

		if (stationaries.size() > 0) {
			common.printDebugMessage("Found " + stationaries.size() + " stationary spells", null, null, false);
			stationaries.get(0).flair(10);

			kill();
			return;
		}

		// if the spell has hit a solid block, the projectile is stopped and wont go
		// further so kill the spell
		if (hasHitTarget())
			kill();
	}
}