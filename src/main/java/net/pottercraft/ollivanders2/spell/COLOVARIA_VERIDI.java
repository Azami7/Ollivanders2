package net.pottercraft.ollivanders2.spell;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.O2Color;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Target sheep or colored block turns green.
 *
 * @author Azami7
 */
public final class COLOVARIA_VERIDI extends ColoroSuper {
	/**
	 * Default constructor for use in generating spell text. Do not use to cast the
	 * spell.
	 *
	 * @param plugin the Ollivanders2 plugin
	 */
	public COLOVARIA_VERIDI(Ollivanders2 plugin) {
		super(plugin);

		spellType = O2SpellType.COLOVARIA_VERIDI;
		branch = O2MagicBranch.CHARMS;

		text = "Turns target colorable entity or block green.";
	}

	/**
	 * Constructor.
	 *
	 * @param plugin    a callback to the MC plugin
	 * @param player    the player who cast this spell
	 * @param rightWand which wand the player was using
	 */
	public COLOVARIA_VERIDI(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
		super(plugin, player, rightWand);
		spellType = O2SpellType.COLOVARIA_VERIDI;
		branch = O2MagicBranch.CHARMS;

		color = O2Color.GREEN;

		initSpell();
	}
}