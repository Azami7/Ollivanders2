package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import com.sk89q.worldguard.protection.flags.Flags;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.stationaryspell.COLLOPORTUS;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;

/**
 * Moves a group of blocks.
 * <p>
 * https://harrypotter.fandom.com/wiki/Levitation_Charm
 * <p>
 * https://github.com/Azami7/Ollivanders2/issues/282 - this code needs to be
 * totally redone (yick)
 */
public final class WINGARDIUM_LEVIOSA extends O2Spell {
	/**
	 * The map of block data for the selected blocks
	 */
	Map<Location, BlockData> blockDataMap = new HashMap<>();

	/**
	 * A list of the selected blocks
	 */
	List<Block> blockList = new ArrayList<>();

	/**
	 * The location of the selected blocks
	 */
	List<Location> locList = new ArrayList<>();

	/**
	 * Are we actively moving blocks?
	 */
	boolean moving = true;

	/**
	 * How far we move the blocks (I think...)
	 */
	double length = 0;

	/**
	 * The maximum radius of blocks that can be selected
	 */
	int maxRadius = 5;

	/**
	 * The radius of blocks selected
	 */
	int radius = 1;

	/**
	 * If the blocks should be converted to fallingBlocks after the end of the
	 * spell.
	 */
	boolean dropBlocks = true;

	/**
	 * Default constructor for use in generating spell text. Do not use to cast the
	 * spell.
	 *
	 * @param plugin the Ollivanders2 plugin
	 */
	public WINGARDIUM_LEVIOSA(Ollivanders2 plugin) {
		super(plugin);

		branch = O2MagicBranch.CHARMS;
		spellType = O2SpellType.WINGARDIUM_LEVIOSA;

		flavorText = new ArrayList<>() {
			{
				add("The Levitation Charm");
				add("You're saying it wrong ...It's Wing-gar-dium Levi-o-sa, make the 'gar' nice and long.\" -Hermione Granger");
				add("The Levitation Charm is one of the first spells learnt by any young witch or wizard.  With the charm a witch or wizard can make things fly with the flick of a wand.");
			}
		};

		text = "Levitates and lets you move blocks while crouching.";
	}

	/**
	 * Constructor
	 *
	 * @param plugin    a callback to the MC plugin
	 * @param player    the player who cast this spell
	 * @param rightWand which wand the player was using
	 */
	public WINGARDIUM_LEVIOSA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
		super(plugin, player, rightWand);

		spellType = O2SpellType.WINGARDIUM_LEVIOSA;
		branch = O2MagicBranch.CHARMS;

		materialBlockedList.add(Material.LAVA);
		materialBlockedList.add(Material.SAND);
		materialBlockedList.add(Material.GRAVEL);

		// world guard flags
		if (Ollivanders2.worldGuardEnabled) {
			worldGuardFlags.add(Flags.BUILD);
			worldGuardFlags.add(Flags.ITEM_PICKUP);
			worldGuardFlags.add(Flags.ITEM_DROP);
		}

		initSpell();
	}

	/**
	 * Set the radius based on the caster's skill
	 */
	@Override
	void doInitSpell() {
		radius = (int) usesModifier / 20;
		if (radius > maxRadius)
			radius = maxRadius;
		else if (radius < 1)
			radius = 1;
	}

	/**
	 * Select and move a group of blocks
	 */
	@Override
	public void checkEffect() {
		if (!isSpellAllowed()) {
			kill();
			return;
		}

		if (moving) {
			move();

			Material type = location.getBlock().getType();
			if (type != Material.AIR && type != Material.WATER && type != Material.LAVA) {
				moving = false;

				ArrayList<COLLOPORTUS> collos = new ArrayList<>();
				for (O2StationarySpell stat : Ollivanders2API.getStationarySpells().getActiveStationarySpells()) {
					if (stat instanceof COLLOPORTUS) {
						collos.add((COLLOPORTUS) stat);
					}
				}

				for (Block block : Ollivanders2Common.getBlocksInRadius(location, radius)) {
					boolean insideCollo = false;
					for (COLLOPORTUS collo : collos) {
						if (collo.isLocationInside(block.getLocation())) {
							insideCollo = true;
						}
					}

					if (!insideCollo) {
						type = block.getType();
						if (!materialBlockedList.contains(type) && type.isSolid()) {
							Location loc = centerOfBlock(block).subtract(location);
							blockDataMap.put(loc, block.getBlockData());
							locList.add(loc);
							blockList.add(block);
						}
					}
				}
				length = player.getEyeLocation().distance(location);
			}
		} else {
			if (player.isSneaking()) {
				List<Location> locList2 = new ArrayList<>(locList);
				for (Block block : blockList) {
					if (block.getType() == Material.AIR) {
						locList2.remove(locList.get(blockList.indexOf(block)));
					}
				}
				locList = locList2;
				for (Block block : blockList) {
					block.setType(Material.AIR);
				}
				blockList.clear();
				for (Location loc : locList) {
					Vector direction = player.getEyeLocation().getDirection().multiply(length);
					Location center = player.getEyeLocation().add(direction);
					location = center;
					Location toLoc = center.clone().add(loc);
					if (toLoc.getBlock().getType() == Material.AIR) {
						toLoc.getBlock().setType(blockDataMap.get(loc).getMaterial());
						blockList.add(toLoc.getBlock());
					}
				}
			} else if (dropBlocks) {
				for (Block block : blockList) {
					block.setType(Material.AIR);
				}
				Vector direction = player.getEyeLocation().getDirection().multiply(length);
				Location center = player.getEyeLocation().add(direction);
				Vector moveVec = center.toVector().subtract(location.toVector());
				for (Location loc : locList) {
					Location toLoc = center.clone().add(loc);
					BlockData blockData = blockDataMap.get(loc);

					World world = loc.getWorld();
					if (world == null) {
						kill();
						return;
					}

					FallingBlock fall = loc.getWorld().spawnFallingBlock(toLoc, blockData);
					fall.setVelocity(moveVec);
				}
				kill();
			} else {
				kill();
			}
		}
	}

	/**
	 * Returns the location at the center of the block, instead of the corner.
	 *
	 * @param block - Block to get the center location of.
	 * @return Location at the center of the block.
	 */
	@NotNull
	private Location centerOfBlock(@NotNull Block block) {
		Location newLoc = block.getLocation().clone();
		newLoc.setX(newLoc.getX() + 0.5);
		newLoc.setY(newLoc.getY() + 0.5);
		newLoc.setZ(newLoc.getZ() + 0.5);
		return newLoc;
	}

	@Override
	protected void doCheckEffect() {
	}
}