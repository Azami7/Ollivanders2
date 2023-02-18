package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.flags.Flags;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;

/**
 * Creates a glowstone at the reticule that goes away after a time.
 * <p>
 * Reference: https://harrypotter.fandom.com/wiki/Lumos_Maxima
 */
public final class LUMOS_MAXIMA extends O2Spell {
	/**
	 * The length of the line of glowstone
	 */
	int lineLength;

	/**
	 * How long the glowstone will remain
	 */
	int duration;

	static int maxLineLength = 20;
	static int maxDuration = Ollivanders2Common.ticksPerSecond * 600; // 10 minutes
	static int minDuration = Ollivanders2Common.ticksPerSecond * 30; // 30 seconds

	/**
	 * A map of the affected blocks and their original types for use with revert()
	 */
	final HashMap<Block, Material> affectedBlocks = new HashMap<>();

	/**
	 * Default constructor for use in generating spell text. Do not use to cast the
	 * spell.
	 *
	 * @param plugin the Ollivanders2 plugin
	 */
	public LUMOS_MAXIMA(Ollivanders2 plugin) {
		super(plugin);

		spellType = O2SpellType.LUMOS_MAXIMA;
		branch = O2MagicBranch.CHARMS;

		flavorText = new ArrayList<>() {
			{
				add("\"Light your wands, can’t you? And hurry, we have little time!\" -Griphook");
			}
		};

		text = "Lumos Maxima will spawn a glowstone at the impact site.";
	}

	/**
	 * Constructor.
	 *
	 * @param plugin    a callback to the MC plugin
	 * @param player    the player who cast this spell
	 * @param rightWand which wand the player was using
	 */
	public LUMOS_MAXIMA(Ollivanders2 plugin, Player player, Double rightWand) {
		super(plugin, player, rightWand);

		spellType = O2SpellType.LUMOS_MAXIMA;
		branch = O2MagicBranch.CHARMS;

		// base line length on experience
		lineLength = 1 + (int) usesModifier / 10;
		if (lineLength > maxLineLength)
			lineLength = maxLineLength;

		// set duration based on experience, min 30 seconds
		duration = Ollivanders2Common.ticksPerSecond * (int) usesModifier;
		if (duration < minDuration)
			duration = minDuration;
		else if (duration > maxDuration)
			duration = maxDuration;

		// remove all pass-through materials, this can only go through air and water
		projectilePassThrough.clear();
		projectilePassThrough.add(Material.AIR);
		projectilePassThrough.add(Material.CAVE_AIR);
		projectilePassThrough.add(Material.WATER);

		// world guard flags
		if (Ollivanders2.worldGuardEnabled)
			worldGuardFlags.add(Flags.BUILD);

		initSpell();
	}

	/**
	 * Create a line of glowstone from the caster's wand then age the effect and
	 * revert the blocks
	 */
	@Override
	protected void doCheckEffect() {
		if (!hasHitTarget()) {
			// if we have not hit a solid target and still have more line to draw, add a
			// glowstone
			if (lineLength > 0) {
				Block curBlock = location.getBlock();
				affectedBlocks.put(curBlock, curBlock.getType());

				curBlock.setType(Material.GLOWSTONE);

				World world = location.getWorld();
				if (world == null)
					common.printDebugMessage("LUMOS_MAXIMA.doCheckEffect: world is null", null, null, true);
				else
					world.playEffect(location, Effect.MOBSPAWNER_FLAMES, 0);
			}
			// if we have not hit a solid target but don't have more line left, stop the
			// projectile
			else
				stopProjectile();
		} else {
			duration = duration - 1;

			if (duration <= 0)
				kill();
		}
	}

	/**
	 * Revert all the blocks changed by this spell
	 */
	@Override
	public void revert() {
		for (Block block : affectedBlocks.keySet()) {
			block.setType(affectedBlocks.get(block));
		}
	}
}