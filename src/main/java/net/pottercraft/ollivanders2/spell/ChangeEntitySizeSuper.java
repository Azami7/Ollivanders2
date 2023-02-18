package net.pottercraft.ollivanders2.spell;

import org.bukkit.entity.Ageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Change the size of growing entity
 */
public abstract class ChangeEntitySizeSuper extends O2Spell {
	/**
	 * Max number of entities that can be targeted
	 */
	static int maxTargets = 10;

	/**
	 * Max possible radius for the spell
	 */
	static int maxRadius = 20;

	/**
	 * The number of entities to target
	 */
	int targets;

	/**
	 * The radius to affect
	 */
	int radius;

	/**
	 * Is this spell growing or shrinking the entity?
	 */
	boolean growing;

	/**
	 * How much can this change a slimes size
	 */
	static final int maxSlimeSizeChange = 2;

	/**
	 * How much can this change a slimes size
	 */
	static final int minSlimeSize = 1;

	/**
	 * How much can this change a slimes size
	 */
	static final int maxSlimeSize = 3;

	/**
	 * Default constructor for use in generating spell text. Do not use to cast the
	 * spell.
	 *
	 * @param plugin the Ollivanders2 plugin
	 */
	public ChangeEntitySizeSuper(Ollivanders2 plugin) {
		super(plugin);
	}

	/**
	 * Constructor.
	 *
	 * @param plugin    a callback to the MC plugin
	 * @param player    the player who cast this spell
	 * @param rightWand which wand the player was using
	 */
	public ChangeEntitySizeSuper(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
		super(plugin, player, rightWand);
	}

	/**
	 * Look for entities within the projectile range and change their size, if
	 * possible
	 */
	@Override
	protected void doCheckEffect() {
		if (hasHitTarget()) {
			kill();
			return;
		}

		for (LivingEntity livingEntity : getNearbyLivingEntities(radius)) {
			if (targets < 1) {
				kill();
				return;
			}

			common.printDebugMessage("Checking " + livingEntity.getName(), null, null, false);

			if ((livingEntity.getUniqueId() == player.getUniqueId()) || !entityHarmWGCheck(livingEntity))
				continue;

			if (livingEntity instanceof Ageable) {
				if (growing)
					((Ageable) livingEntity).setAdult();
				else
					((Ageable) livingEntity).setBaby();
			} else if (livingEntity instanceof Slime)
				changeSlimeSize((Slime) livingEntity);
			else
				continue;

			targets = targets - 1;
		}
	}

	/**
	 * Make the target slime smaller.
	 *
	 * @param slime the smile to reduce the size of
	 */
	private void changeSlimeSize(@NotNull Slime slime) {
		int delta = (int) (usesModifier / 25) + 1;
		if (delta > maxSlimeSizeChange)
			delta = maxSlimeSizeChange;

		common.printDebugMessage("Changing slime size by " + delta, null, null, false);

		int newSize = slime.getSize();
		if (growing)
			newSize = newSize + delta;
		else
			newSize = newSize - delta;

		if (newSize > maxSlimeSize)
			newSize = maxSlimeSize;
		else if (newSize < minSlimeSize)
			newSize = minSlimeSize;

		slime.setSize(newSize);
	}
}
