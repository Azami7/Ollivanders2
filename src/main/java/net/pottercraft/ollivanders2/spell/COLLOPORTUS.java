package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.sk89q.worldguard.protection.flags.Flags;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;

/**
 * Locks doors and trapdoors.
 * <p>
 * Reference: https://harrypotter.fandom.com/wiki/Locking_Spell
 */
public final class COLLOPORTUS extends StationarySpell {
	/**
	 * Default constructor for use in generating spell text. Do not use to cast the
	 * spell.
	 *
	 * @param plugin the Ollivanders2 plugin
	 */
	public COLLOPORTUS(Ollivanders2 plugin) {
		super(plugin);

		spellType = O2SpellType.COLLOPORTUS;
		branch = O2MagicBranch.CHARMS;

		flavorText = new ArrayList<>() {
			{
				add("The Locking Spell.");
			}
		};

		text = "Locks doors and trapdoors so they cannot be opened. This spell does not age and can only be removed with the Unlocking Spell, Alohomora.";
	}

	/**
	 * Constructor.
	 *
	 * @param plugin    a callback to the MC plugin
	 * @param player    the player who cast this spell
	 * @param rightWand which wand the player was using
	 */
	public COLLOPORTUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
		super(plugin, player, rightWand);
		spellType = O2SpellType.COLLOPORTUS;
		branch = O2MagicBranch.CHARMS;

		// world guard flags
		if (Ollivanders2.worldGuardEnabled) {
			worldGuardFlags.add(Flags.BUILD);
		}

		minRadius = maxRadius = net.pottercraft.ollivanders2.stationaryspell.COLLOPORTUS.minRadiusConfig;
		radiusModifier = 1;
		flairSize = 10;

		initSpell();
	}

	@Override
	@Nullable
	protected O2StationarySpell createStationarySpell() {
		Block targetBlock = getTargetBlock();
		if (targetBlock == null) {
			common.printDebugMessage("COLLOPORTUS.doCheckEffect: from block is null", null, null, true);
			return null;
		}

		Material blockType = targetBlock.getType();
		if (!(Ollivanders2Common.doors.contains(blockType) || Ollivanders2Common.trapdoors.contains(blockType))) {
			common.printDebugMessage("block is not a door or trapdoor", null, null, true);
			return null;
		}

		return new net.pottercraft.ollivanders2.stationaryspell.COLLOPORTUS(p, player.getUniqueId(), location);
	}
}