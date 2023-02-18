package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.sk89q.worldguard.protection.flags.Flags;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;

/**
 * Turns a target area or object to stone.
 * <p>
 * Reference: https://harrypotter.fandom.com/wiki/Hardening_Charm
 */
public final class DURO extends BlockTransfiguration {
	private static final int minRadiusConfig = 1;
	private static final int maxRadiusConfig = 30;
	private static final int minDurationConfig = 15 * Ollivanders2Common.ticksPerSecond;
	private static final int maxDurationConfig = 10 * Ollivanders2Common.ticksPerMinute;

	/**
	 * Default constructor for use in generating spell text. Do not use to cast the
	 * spell.
	 *
	 * @param plugin the Ollivanders2 plugin
	 */
	public DURO(Ollivanders2 plugin) {
		super(plugin);

		spellType = O2SpellType.DURO;
		branch = O2MagicBranch.CHARMS;

		flavorText = new ArrayList<>() {
			{
				add("The Hardening Charm");
				add("The Hardening Charm will turn an object into solid stone. This can be surprisingly handy in a tight spot. Of course, most students only seem to use this spell to sabotage their fellow students' schoolbags or to turn a pumpkin pasty to stone just before someone bites into it. It is unwise to try this unworthy trick on any of your teachers.");
			}
		};

		text = "Duro will turn the blocks in a radius in to stone.";
	}

	/**
	 * Constructor.
	 *
	 * @param plugin    a callback to the MC plugin
	 * @param player    the player who cast this spell
	 * @param rightWand which wand the player was using
	 */
	public DURO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
		super(plugin, player, rightWand);
		spellType = O2SpellType.DURO;
		branch = O2MagicBranch.CHARMS;

		permanent = false;
		minRadius = minRadiusConfig;
		maxRadius = maxRadiusConfig;
		radiusModifier = 0.1; // 10% of usesModifier
		minDuration = minDurationConfig;
		maxDuration = maxDurationConfig;
		durationModifier = 0.5; // 50% of usesModifier

		// pass-through materials
		projectilePassThrough.remove(Material.WATER);

		// what type blocks transfigure in to for this spell
		transfigureType = Material.STONE;

		// world-guard flags
		if (Ollivanders2.worldGuardEnabled)
			worldGuardFlags.add(Flags.BUILD);

		initSpell();
	}
}