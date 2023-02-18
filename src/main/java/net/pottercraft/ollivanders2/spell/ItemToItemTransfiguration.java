package net.pottercraft.ollivanders2.spell;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.item.enchantment.Enchantment;

/**
 * Super class for all item entity transfigurations
 */
public abstract class ItemToItemTransfiguration extends EntityTransfiguration {
	/**
	 * The list of changed items for non-permanent spells to revert
	 */
	protected Map<Item, Material> changedItems = new HashMap<>();

	/**
	 * If this is populated, any material type key will be changed to the value
	 */
	protected Map<Material, Material> transfigurationMap = new HashMap<>();

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
	public ItemToItemTransfiguration(Ollivanders2 plugin) {
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
	public ItemToItemTransfiguration(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
		super(plugin, player, rightWand);

		entityAllowedList.add(EntityType.DROPPED_ITEM);
	}

	/**
	 * Look for items and change them to the new type
	 */
	@Override
	void transfigure() {
		if (isTransfigured)
			// we've already transfigured something
			return;

		for (Entity entity : getCloseEntities(1.5)) {
			p.getLogger().info("checking " + entity.getName());

			if (!canTransfigure(entity)) {
				p.getLogger().info("!canTransfigure(entity)" + entity.getName());
				continue;
			}

			if (!(entity instanceof Item)) {
				p.getLogger().info("!(entity instanceof Item)" + entity.getName());
				continue;
			}

			Item item = (Item) entity;
			Material originalType = item.getItemStack().getType();

			if (!transfigurationMap.containsKey(originalType)) {
				p.getLogger().info("!transfigurationMap.containsKey(originalType)" + entity.getName());
				continue;
			}

			Material newType = transfigurationMap.get(originalType);

			int rand = Math.abs(Ollivanders2Common.random.nextInt() % 100);
			if (rand < successRate) {
				if (!permanent)
					changedItems.put(item, originalType);

				ItemStack itemStack = item.getItemStack();
				itemStack.setType(newType);
				item.setItemStack(itemStack);

				isTransfigured = true;

				player.sendMessage(Ollivanders2.chatColor + successMessage);
			} else
				player.sendMessage(Ollivanders2.chatColor + failureMessage);

			kill();
			return;
		}
	}

	/**
	 * Determine if this entity be transfigured by this spell.
	 * <p>
	 * Entity can transfigure if: 1. It is not in the bloked list 2. It is in the
	 * allowed list, if the allowed list exists 3. The entity is not already the
	 * target type 4. There are no WorldGuard permissions preventing the caster from
	 * altering this entity type 5. The item is not enchanted -or- the magic level
	 * of the enchantment is lower than this spell's magic level
	 *
	 * @param entity the entity to check
	 * @return true if it can be changed
	 */
	@Override
	protected boolean canTransfigure(@NotNull Entity entity) {
		boolean canTransfigure = super.canTransfigure(entity);

		// if all prev checks passed, now verify this item is not enchanted
		if (canTransfigure) {
			if (Ollivanders2API.getItems().enchantedItems.isEnchanted((Item) entity)) {
				if (!canTransfigureEnchantedItems)
					canTransfigure = false;
				else {
					Enchantment enchantment = Ollivanders2API.getItems().enchantedItems
							.getEnchantment(((Item) entity).getItemStack());
					if (enchantment != null
							&& enchantment.getType().getLevel().ordinal() > this.spellType.getLevel().ordinal())
						canTransfigure = false;
				}
			}
		}

		return canTransfigure;
	}

	/**
	 * Revert all items to their original types.
	 */
	@Override
	void doRevert() {
		if (permanent)
			return;

		for (Item item : changedItems.keySet()) {
			ItemStack itemStack = item.getItemStack();
			itemStack.setType(changedItems.get(item));
			item.setItemStack(itemStack);
		}
	}

	/**
	 * Transfigures entity into new EntityType.
	 *
	 * @param entity the entity to transfigure
	 * @return the transfigured entity
	 */
	@Override
	@Nullable
	protected Entity transfigureEntity(@NotNull Entity entity) {
		return entity;
	}
}
