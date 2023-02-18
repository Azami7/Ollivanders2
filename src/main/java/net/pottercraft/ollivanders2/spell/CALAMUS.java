package net.pottercraft.ollivanders2.spell;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.sk89q.worldguard.protection.flags.Flags;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Turns sticks in to arrows.
 */
public final class CALAMUS extends ItemToItemTransfiguration {
	/**
	 * Default constructor for use in generating spell text. Do not use to cast the
	 * spell.
	 *
	 * @param plugin the Ollivanders2 plugin
	 */
	public CALAMUS(Ollivanders2 plugin) {
		super(plugin);

		spellType = O2SpellType.CALAMUS;
		branch = O2MagicBranch.TRANSFIGURATION;

		text = "Turns sticks in to arrows.";
	}

	/**
	 * Constructor.
	 *
	 * @param plugin    a callback to the MC plugin
	 * @param player    the player who cast this spell
	 * @param rightWand which wand the player was using
	 */
	public CALAMUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
		super(plugin, player, rightWand);
		spellType = O2SpellType.CALAMUS;
		branch = O2MagicBranch.TRANSFIGURATION;

		// world guard
		if (Ollivanders2.worldGuardEnabled)
			worldGuardFlags.add(Flags.ITEM_DROP);

		transfigurationMap.put(Material.STICK, Material.ARROW);
		successMessage = "You changed a stick in to an arrow.";
		failureMessage = "Nothing happens.";
		permanent = true;

		initSpell();

		// do this after init spell to ensure success rate is always 100
		successRate = 100;
	}
}