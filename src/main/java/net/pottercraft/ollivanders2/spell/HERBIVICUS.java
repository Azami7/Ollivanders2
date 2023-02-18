package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;

/**
 * Herbivicus causes crops in a radius to grow.
 * <p>
 * Reference: https://harrypotter.fandom.com/wiki/Herbivicus_Charm
 */
public final class HERBIVICUS extends O2Spell {
	private final static int maxRadius = 15;
	private final static int maxGrowth = 7;

	/**
	 * The radius of crops that can be targeted
	 */
	private int radius;

	/**
	 * The amount the crops will grow
	 */
	private int growth;

	/**
	 * Default constructor for use in generating spell text. Do not use to cast the
	 * spell.
	 *
	 * @param plugin the Ollivanders2 plugin
	 */
	public HERBIVICUS(Ollivanders2 plugin) {
		super(plugin);

		spellType = O2SpellType.HERBIVICUS;
		branch = O2MagicBranch.HERBOLOGY;

		flavorText = new ArrayList<>() {
			{
				add("The Plant-Growing Charm");
			}
		};

		text = "Herbivicus causes crops within a radius to grow.";
	}

	/**
	 * Constructor.
	 *
	 * @param plugin    a callback to the MC plugin
	 * @param player    the player who cast this spell
	 * @param rightWand which wand the player was using
	 */
	public HERBIVICUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
		super(plugin, player, rightWand);

		spellType = O2SpellType.HERBIVICUS;
		branch = O2MagicBranch.HERBOLOGY;

		// pass-through materials
		projectilePassThrough.add(Material.WATER);

		initSpell();
	}

	/**
	 * Determine radius and growth amount by caster's level and year
	 */
	@Override
	void doInitSpell() {
		radius = (int) ((usesModifier / 4));

		if (radius < 1)
			radius = 1;
		else if (radius > maxRadius)
			radius = maxRadius;

		growth = (int) ((usesModifier / 25));
		if (growth < 1)
			growth = 1;
		else if (growth > maxGrowth)
			growth = maxGrowth;
	}

	/**
	 * Grow all the crops in a radius of the spell target
	 */
	@Override
	protected void doCheckEffect() {
		if (!hasHitTarget())
			return;

		for (Block block : Ollivanders2Common.getBlocksInRadius(location, radius)) {
			BlockData blockData = block.getBlockData();

			if (blockData instanceof Ageable) {
				Ageable cropData = (Ageable) blockData;

				if (cropData.getAge() == cropData.getMaximumAge()) {
					successMessage = "Those plants are already fully grown.";
				} else {
					int toGrow = cropData.getAge() + growth;
					if (toGrow > cropData.getMaximumAge())
						toGrow = cropData.getMaximumAge();

					cropData.setAge(toGrow);
					block.setBlockData(cropData);

					successMessage = "The plants grow faster.";
				}
			}
		}

		kill();
	}
}