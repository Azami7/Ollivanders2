package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import com.sk89q.worldguard.protection.flags.Flags;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;

/**
 * Drops random items from a player's inventory. Also cuts down trees.
 * <p>
 * Reference: https://harrypotter.fandom.com/wiki/Severing_Charm
 */
public final class DIFFINDO extends O2Spell {
	private static final int maxRadius = 20;

	/**
	 * Default constructor for use in generating spell text. Do not use to cast the
	 * spell.
	 *
	 * @param plugin the Ollivanders2 plugin
	 */
	public DIFFINDO(Ollivanders2 plugin) {
		super(plugin);

		spellType = O2SpellType.DIFFINDO;
		branch = O2MagicBranch.CHARMS;

		flavorText = new ArrayList<>() {
			{
				add("The Severing Charm");
				add("With the Severing Charm, cutting or tearing objects is a simple matter of wand control.");
				add("The spell can be quite precise in skilled hands, and the Severing Charm is widely used in a variety of wizarding trades.");
			}
		};

		text = "Breaks logs in a radius or drops items from a player’s inventory.";
	}

	/**
	 * Constructor.
	 *
	 * @param plugin    a callback to the MC plugin
	 * @param player    the player who cast this spell
	 * @param rightWand which wand the player was using
	 */
	public DIFFINDO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
		super(plugin, player, rightWand);
		spellType = O2SpellType.DIFFINDO;
		branch = O2MagicBranch.CHARMS;

		// world guard flags
		if (Ollivanders2.worldGuardEnabled) {
			worldGuardFlags.add(Flags.PVP);
			worldGuardFlags.add(Flags.BUILD);
			worldGuardFlags.add(Flags.ITEM_DROP);
		}

		initSpell();
	}

	/**
	 * Split a log or a player's inventory
	 */
	@Override
	protected void doCheckEffect() {
		// first check for players
		splitBackpack();

		// next check for log
		if (hasHitTarget() && !isKilled()) {
			splitLogs();
			kill();
		}
	}

	/**
	 * Split a player's inventory open and make items fall out
	 */
	private void splitBackpack() {
		List<Player> players = getNearbyPlayers(defaultRadius);

		for (Player plyr : players) {
			if (plyr.getUniqueId() == player.getUniqueId())
				continue;

			PlayerInventory inv = plyr.getInventory();
			ArrayList<ItemStack> remStack = new ArrayList<>();
			for (ItemStack stack : inv.getContents()) {
				// 0-99% chance of this item being dropped, based on usesModifier where 200
				// casts gives 99% chance
				if ((usesModifier / 2) > ((Math.abs(Ollivanders2Common.random.nextInt()) % 100) + 1))
					remStack.add(stack);
			}
			for (ItemStack rem : remStack) {
				inv.remove(rem);
				plyr.getWorld().dropItemNaturally(plyr.getLocation(), rem);
			}

			kill();
			return;
		}
	}

	/**
	 * Split logs within a radius of the spell target location
	 */
	private void splitLogs() {
		double radius = usesModifier / 4;
		if (radius < 1)
			radius = 1;
		else if (radius > maxRadius)
			radius = maxRadius;

		Block target = getTargetBlock();
		if (target == null) {
			common.printDebugMessage("DIFFINDO.doCheckEffect: target block is null", null, null, false);
			kill();
			return;
		}

		if (Ollivanders2Common.isNaturalLog(target)) {
			for (Block nearbyBlock : Ollivanders2Common.getBlocksInRadius(location, radius)) {
				if (Ollivanders2Common.isNaturalLog(nearbyBlock)) {
					nearbyBlock.breakNaturally();
				}
			}
		}
	}
}