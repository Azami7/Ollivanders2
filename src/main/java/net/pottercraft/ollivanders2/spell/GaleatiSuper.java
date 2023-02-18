package net.pottercraft.ollivanders2.spell;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Sets a player's helmet to a specific block type.
 * <p>
 * In HP, these would be Transfiguration spells but for code purposes they
 * behave like charm projectiles, so we extend Charms then override the spell
 * type.
 */
public abstract class GaleatiSuper extends O2Spell {
	Material materialType = Material.AIR;

	/**
	 * Default constructor for use in generating spell text. Do not use to cast the
	 * spell.
	 *
	 * @param plugin the Ollivanders2 plugin
	 */
	public GaleatiSuper(Ollivanders2 plugin) {
		super(plugin);
	}

	/**
	 * Constructor.
	 *
	 * @param plugin    a callback to the MC plugin
	 * @param player    the player who cast this spell
	 * @param rightWand which wand the player was using
	 */
	public GaleatiSuper(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
		super(plugin, player, rightWand);
	}

	/**
	 * Targets a player in radius of the projectile and changes their helmet.
	 */
	@Override
	protected void doCheckEffect() {
		// projectile has stopped, kill the spell
		if (hasHitTarget())
			kill();

		List<Player> livingEntities = getNearbyPlayers(defaultRadius);

		for (Player target : livingEntities) {
			if (target.getUniqueId() == player.getUniqueId())
				continue;

			EntityEquipment entityEquipment = target.getEquipment();
			if (entityEquipment == null) {
				// they have no equipment
				kill();
				return;
			}

			ItemStack helmet = entityEquipment.getHelmet();
			if (helmet != null) {
				if (helmet.getType() != Material.AIR)
					target.getWorld().dropItem(target.getEyeLocation(), helmet);
			}
			entityEquipment.setHelmet(new ItemStack(materialType, 1));
			kill();
			return;
		}
	}
}
