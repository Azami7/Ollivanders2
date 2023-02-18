package net.pottercraft.ollivanders2.spell;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.sk89q.worldguard.protection.flags.Flags;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;

/**
 * Breaks glass.
 * <p>
 * Reference: https://harrypotter.fandom.com/wiki/Finestra_spell
 */
public final class FINESTRA extends O2Spell {
	private static final int maxRadius = 20;

	/**
	 * Default constructor for use in generating spell text. Do not use to cast the
	 * spell.
	 *
	 * @param plugin the Ollivanders2 plugin
	 */
	public FINESTRA(Ollivanders2 plugin) {
		super(plugin);

		spellType = O2SpellType.FINESTRA;
		branch = O2MagicBranch.CHARMS;

		text = "Breaks glass in a radius.";
	}

	/**
	 * Constructor.
	 *
	 * @param plugin    a callback to the MC plugin
	 * @param player    the player who cast this spell
	 * @param rightWand which wand the player was using
	 */
	public FINESTRA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
		super(plugin, player, rightWand);
		spellType = O2SpellType.FINESTRA;
		branch = O2MagicBranch.CHARMS;

		// world guard flags
		if (Ollivanders2.worldGuardEnabled)
			worldGuardFlags.add(Flags.BUILD);

		initSpell();
	}

	/**
	 * Break glass blocks in a radius
	 */
	@Override
	protected void doCheckEffect() {
		if (!hasHitTarget())
			return;

		Block target = getTargetBlock();
		if (target == null) {
			common.printDebugMessage("FINESTRA.doCheckEffect: target block is null", null, null, false);
			kill();
			return;
		}

		double radius = usesModifier / 4;
		if (radius < 1)
			radius = 1;
		else if (radius > maxRadius)
			radius = maxRadius;

		if (isGlass(getTargetBlock())) {
			for (Block nearbyBlock : Ollivanders2Common.getBlocksInRadius(location, radius)) {
				if (isGlass(nearbyBlock))
					nearbyBlock.breakNaturally();
			}
		}

		kill();
	}

	/**
	 * Determine if a block is glass.
	 *
	 * @param block the block to check
	 * @return true if the block is glass, false otherwise
	 */
	private boolean isGlass(Block block) {
		Material blockType = block.getType();

		return (blockType == Material.GLASS || blockType == Material.GLASS_PANE
				|| blockType == Material.BLACK_STAINED_GLASS || blockType == Material.BLACK_STAINED_GLASS_PANE
				|| blockType == Material.GRAY_STAINED_GLASS || blockType == Material.GRAY_STAINED_GLASS_PANE
				|| blockType == Material.BLUE_STAINED_GLASS || blockType == Material.BLUE_STAINED_GLASS_PANE
				|| blockType == Material.BROWN_STAINED_GLASS || blockType == Material.BROWN_STAINED_GLASS_PANE
				|| blockType == Material.CYAN_STAINED_GLASS || blockType == Material.CYAN_STAINED_GLASS_PANE
				|| blockType == Material.GREEN_STAINED_GLASS || blockType == Material.GREEN_STAINED_GLASS_PANE
				|| blockType == Material.LIGHT_BLUE_STAINED_GLASS || blockType == Material.LIGHT_BLUE_STAINED_GLASS_PANE
				|| blockType == Material.RED_STAINED_GLASS || blockType == Material.RED_STAINED_GLASS_PANE
				|| blockType == Material.YELLOW_STAINED_GLASS || blockType == Material.YELLOW_STAINED_GLASS_PANE
				|| blockType == Material.LIME_STAINED_GLASS || blockType == Material.LIME_STAINED_GLASS_PANE
				|| blockType == Material.ORANGE_STAINED_GLASS || blockType == Material.ORANGE_STAINED_GLASS_PANE
				|| blockType == Material.LIGHT_GRAY_STAINED_GLASS || blockType == Material.LIGHT_GRAY_STAINED_GLASS_PANE
				|| blockType == Material.MAGENTA_STAINED_GLASS || blockType == Material.MAGENTA_STAINED_GLASS_PANE
				|| blockType == Material.PURPLE_STAINED_GLASS || blockType == Material.PURPLE_STAINED_GLASS_PANE
				|| blockType == Material.WHITE_STAINED_GLASS || blockType == Material.WHITE_STAINED_GLASS_PANE
				|| blockType == Material.PINK_STAINED_GLASS || blockType == Material.PINK_STAINED_GLASS_PANE);
	}
}