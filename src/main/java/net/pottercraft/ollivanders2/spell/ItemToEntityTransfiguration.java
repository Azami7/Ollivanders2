package net.pottercraft.ollivanders2.spell;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.sk89q.worldguard.protection.flags.Flags;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.item.enchantment.Enchantment;

/**
 * Transform items to entities. This spell always consumes the item (ie. it
 * doesn't come back on revert)
 */
public class ItemToEntityTransfiguration extends EntityTransfiguration implements Listener {
	/**
	 * If this is populated, any material type key will be changed to the value
	 */
	protected Map<Material, EntityType> transfigurationMap = new HashMap<>();

	/**
	 * Optional custom name for the transfigured entity
	 */
	String entityCustomName = null;

	/**
	 * Can this spell target enchanted items
	 */
	boolean canTransfigureEnchantedItems = false;

	/**
	 * Default constructor for use in generating spell text. Do not use to cast the
	 * spell.
	 *
	 * @param plugin the Ollivanders2 plugin
	 */
	public ItemToEntityTransfiguration(Ollivanders2 plugin) {
		super(plugin);

		branch = O2MagicBranch.TRANSFIGURATION;
	}

	/**
	 * Constructor.
	 *
	 * @param plugin    a callback to the MC plugin
	 * @param player    the player who cast this spell
	 * @param rightWand which wand the player was using
	 */
	public ItemToEntityTransfiguration(@NotNull Ollivanders2 plugin, @NotNull Player player,
			@NotNull Double rightWand) {
		super(plugin, player, rightWand);

		entityAllowedList.add(EntityType.DROPPED_ITEM);

		// world guard flags
		if (Ollivanders2.worldGuardEnabled)
			worldGuardFlags.add(Flags.MOB_SPAWNING);
	}

	/**
	 * Transfigures item into EntityType.
	 *
	 * @param entity the item to transfigure
	 * @return the transfigured entity if successful, null otherwise
	 */
	@Override
	@Nullable
	protected Entity transfigureEntity(@NotNull Entity entity) {
		if (!(entity instanceof Item)) {
			common.printDebugMessage("Entity is not type Item in " + spellType.toString(), null, null, true);
			return null;
		}

		Item item = (Item) entity;

		if (!transfigurationMap.isEmpty() && transfigurationMap.containsKey(item.getItemStack().getType()))
			targetType = transfigurationMap.get(item.getItemStack().getType());

		// spawn the new entity
		Entity newEntity = item.getWorld().spawnEntity(item.getLocation(), targetType);
		if (entityCustomName != null && entityCustomName.length() > 0)
			newEntity.setCustomName(entityCustomName);

		// remove the item
		item.remove();

		// register listeners once the transfiguration has been successful
		p.getServer().getPluginManager().registerEvents(this, p);

		return newEntity;
	}

	/**
	 * Determine if this entity be transfigured by this spell.
	 * <p>
	 * Entity can transfigure if:<br>
	 * 1. It is not in the blocked list<br>
	 * 2. It is in the allowed list, if the allowed list exists<br>
	 * 3. The entity is not already the target type<br>
	 * 4. There are no WorldGuard permissions preventing the caster from altering
	 * this entity type<br>
	 * 5. The entity is an Item 6. The item type is in the transfiguration map, if
	 * it is populated 7. The item is not enchanted -or- the magic level of the
	 * enchantment is lower than this spell's magic level<br>
	 *
	 * @param entity the entity to check
	 * @return true if it can be changed
	 */
	@Override
	protected boolean canTransfigure(@NotNull Entity entity) {
		// make sure it is an item
		if (!super.canTransfigure(entity) || !(entity instanceof Item))
			return false;

		// make sure it is a type we can transfigure
		Material itemType = ((Item) entity).getItemStack().getType();
		if (!transfigurationMap.isEmpty() && !transfigurationMap.containsKey(itemType))
			return false;

		// if all prev checks passed, now verify this item is not enchanted
		if (Ollivanders2API.getItems().enchantedItems.isEnchanted((Item) entity)) {
			if (!canTransfigureEnchantedItems)
				return false;
			else {
				Enchantment enchantment = Ollivanders2API.getItems().enchantedItems
						.getEnchantment(((Item) entity).getItemStack());
				if (enchantment != null
						&& enchantment.getType().getLevel().ordinal() > this.spellType.getLevel().ordinal())
					return false;
			}
		}

		return true;
	}

	/**
	 * Handle when the entity is killed.
	 *
	 * @param event the entity death event
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDeath(EntityDeathEvent event) {
		if (transfiguredEntity == null)
			return;

		Entity entity = event.getEntity();
		if (entity.getUniqueId() == transfiguredEntity.getUniqueId())
			// the entity was killed, kill this spell
			kill();
	}
}
