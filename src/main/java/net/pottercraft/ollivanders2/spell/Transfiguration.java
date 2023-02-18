package net.pottercraft.ollivanders2.spell;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;

/**
 * The super class for entity transfiguration projectile spells.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
@Deprecated
public abstract class Transfiguration extends O2Spell {
	private final UUID nullUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
	private EntityType fromEType;
	private ItemStack fromStack;
	private UUID toID = nullUUID;
	private boolean hasTransfigured;

	/**
	 * If this is not permanent, how long it should last. Default is 15 seconds.
	 */
	int spellDuration = Ollivanders2Common.ticksPerSecond * 15;

	/**
	 * Allows spell variants to change the duration of this spell.
	 */
	double durationModifier = 1.0;

	/**
	 * Max duration of this spell. Default is 10 minutes.
	 */
	int maxDuration = Ollivanders2Common.ticksPerSecond * 600;

	/**
	 * Default constructor for use in generating spell text. Do not use to cast the
	 * spell.
	 *
	 * @param plugin the Ollivanders2 plugin
	 */
	public Transfiguration(Ollivanders2 plugin) {
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
	public Transfiguration(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
		super(plugin, player, rightWand);

		branch = O2MagicBranch.TRANSFIGURATION;

		hasTransfigured = false;
	}

	/**
	 * Transfigures entity into new EntityType. Do not use on players. Time length
	 * is usesModier() number of minutes plus 8 seconds.
	 *
	 * @param entity  - Entity to transfigure
	 * @param toType  - Type to change entity into. If transfiguring into an item,
	 *                have this be EntityType.DROPPED_ITEM
	 * @param toStack - If transfiguring into an item, the itemstack to transfigure
	 *                into
	 * @return - The resulting entity.
	 */
	@Nullable
	public Entity transfigureEntity(@NotNull Entity entity, @Nullable EntityType toType, @Nullable ItemStack toStack) {
		fromEType = entity.getType();
		if (fromEType == EntityType.DROPPED_ITEM) {
			fromStack = ((Item) entity).getItemStack();
		}
		if (fromEType == EntityType.FALLING_BLOCK) {
			fromStack = new ItemStack(((FallingBlock) entity).getBlockData().getMaterial());
		}

		// TODO revist this logic because I am not sure I follow what it is doing. I
		// think it is trying to remove any existing transfigurations on the entity.
		for (O2Spell spell : p.getProjectiles()) {
			if (spell instanceof Transfiguration) {
				Transfiguration trans = (Transfiguration) spell;
				if (trans.getToID() == entity.getUniqueId()) {
					trans.kill();
					Entity transEntity = trans.endTransfigure();
					if (transEntity == null) {
						return entity;
					}

					return transfigureEntity(transEntity, toType, toStack);
				}
			}
		}

		entity.remove();

		hasTransfigured = true;
		Entity newEntity = null;
		Location loc = entity.getLocation();

		if (toType == EntityType.DROPPED_ITEM && toStack != null) {
			newEntity = player.getWorld().dropItemNaturally(loc, toStack);
		} else if (toType == null) {
			// do not do anything
		} else {
			newEntity = player.getWorld().spawnEntity(loc, toType);
		}

		if (newEntity == null) {
			toID = nullUUID;
		} else {
			toID = newEntity.getUniqueId();
		}

		spellDuration = (int) (spellDuration * usesModifier * durationModifier);

		if (spellDuration > maxDuration) {
			spellDuration = maxDuration;
		}

		return newEntity;
	}

	/**
	 * Ends the transfiguration. Drops items if there are any in it's inventory.
	 *
	 * @return The newly spawned Entity. Null if no entity was spawned from the
	 *         ending of the transfiguration.
	 */
	@Nullable
	public Entity endTransfigure() {
		kill();
		for (World w : p.getServer().getWorlds()) {
			for (Entity e : w.getEntities()) {
				if (e.getUniqueId() == toID) {
					e.remove();
					if (e instanceof InventoryHolder) {
						for (ItemStack stack : ((InventoryHolder) e).getInventory().getContents()) {
							e.getWorld().dropItemNaturally(e.getLocation(), stack);
						}
					}
					if (fromEType == null) {
						return null;
					}
					if (fromEType.equals(EntityType.DROPPED_ITEM)) {
						return e.getWorld().dropItemNaturally(e.getLocation(), fromStack);
					}
					if (fromEType.equals(EntityType.FALLING_BLOCK)) {
						if (fromStack.getData() != null) {
							return e.getWorld().spawnFallingBlock(e.getLocation(), fromStack.getData().clone());
						} else
							return null;
					} else {
						return e.getWorld().spawnEntity(e.getLocation(), fromEType);
					}
				}
			}
		}
		if (toID == nullUUID) {
			World world = location.getWorld();
			if (world == null) {
				return null;
			} else {
				if (fromEType.equals(EntityType.DROPPED_ITEM)) {
					return world.dropItemNaturally(location, fromStack);
				} else {
					return world.spawnEntity(location, fromEType);
				}
			}
		} else {
			return null;
		}
	}

	/**
	 * Has the transfiguration effect happened yet?
	 *
	 * @return If the transfiguration has taken place
	 */
	public boolean hasTransfigured() {
		return hasTransfigured;
	}

	/**
	 * Gets the id of the transfigured entity
	 *
	 * @return the toID
	 */
	public UUID getToID() {
		return toID;
	}
}