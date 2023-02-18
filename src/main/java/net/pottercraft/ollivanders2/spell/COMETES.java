package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Shoots multiple orange burst fireworks in to the air.
 */
public final class COMETES extends Pyrotechnia {
	/**
	 * Default constructor for use in generating spell text. Do not use to cast the
	 * spell.
	 *
	 * @param plugin the Ollivanders2 plugin
	 */
	public COMETES(Ollivanders2 plugin) {
		super(plugin);

		spellType = O2SpellType.COMETES;
		branch = O2MagicBranch.CHARMS;

		text = "Creates one or more orange burst fireworks.";
	}

	/**
	 * Constructor.
	 *
	 * @param plugin    a callback to the MC plugin
	 * @param player    the player who cast this spell
	 * @param rightWand which wand the player was using
	 */
	public COMETES(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
		super(plugin, player, rightWand);
		spellType = O2SpellType.COMETES;
		branch = O2MagicBranch.CHARMS;

		fireworkColors = new ArrayList<>();
		fireworkColors.add(Color.ORANGE);
		fireworkType = Type.BURST;

		maxFireworks = 10;

		initSpell();
	}

	/**
	 * Set the number of fireworks that can be cast based on the user's experience.
	 */
	@Override
	void doInitSpell() {
		setNumberOfFireworks();
	}
}
