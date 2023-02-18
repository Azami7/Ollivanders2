package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Spells that have a knockback effect.
 */
public abstract class Knockback extends O2Spell {
	boolean pull = false;

	/**
	 * This reduces how strong the knockback is. 1 is max strength, any number
	 * higher than one will reduce the strength.
	 */
	protected int strengthReducer = 20;

	/**
	 * The minimum velocity we can move things
	 */
	protected double minVelocity = 1;

	/**
	 * The maximum velocity we can move things
	 */
	protected double maxVelocity = 5;

	/**
	 * list of entity types this spell works on.
	 */
	protected List<EntityType> entityAllowedList = new ArrayList<>();

	/**
	 * list of entity types this spell will not work on.
	 */
	protected List<EntityType> entityBlockedList = new ArrayList<>();

	/**
	 * Default constructor for use in generating spell text. Do not use to cast the
	 * spell.
	 *
	 * @param plugin the Ollivanders2 plugin
	 */
	public Knockback(Ollivanders2 plugin) {
		super(plugin);
	}

	/**
	 * Constructor.
	 *
	 * @param plugin    a callback to the MC plugin
	 * @param player    the player who cast this spell
	 * @param rightWand which wand the player was using
	 */
	public Knockback(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
		super(plugin, player, rightWand);
	}

	/**
	 * Looks for an entity near the radius of the spell projectile and try to move
	 * it
	 */
	@Override
	protected void doCheckEffect() {
		// projectile is stopped, kill spell
		if (hasHitTarget())
			kill();

		List<Entity> entities = getCloseEntities(defaultRadius);

		if (entities.size() > 0) {
			// look for entities within radius of the projectile and knockback one of them
			for (Entity entity : entities) {
				// check to see if we can target this entity
				if (!entityHarmCheck(entity))
					continue;

				double velocity = usesModifier / strengthReducer;
				if (velocity < minVelocity)
					velocity = minVelocity;
				else if (velocity > maxVelocity)
					velocity = maxVelocity;

				if (pull)
					velocity = velocity * -1;

				entity.setVelocity(player.getLocation().getDirection().normalize().multiply(velocity));
				player.sendMessage("Successfully targeted " + entity.getName());

				kill();
				return;
			}
		}
	}

	/**
	 * Determine if this entity can be targeted for a potentially harmful spell
	 *
	 * @param entity the entity to check
	 * @return true if it can be targeted, false otherwise
	 */
	private boolean entityHarmCheck(Entity entity) {
		// first check entity allow list
		// do not knockback the caster or any block listed entity
		if ((entityAllowedList.size() > 0 && !entityAllowedList.contains(entity.getType()))
				|| entity.getUniqueId() == player.getUniqueId() || entityBlockedList.contains(entity.getType()))
			return false;

		//
		// worldguard
		//

		return entityHarmWGCheck(entity);
	}
}
