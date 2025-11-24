package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;

import net.pottercraft.ollivanders2.item.enchantment.Enchantment;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for entity transfiguration spells.
 *
 * <p>Manages the transfiguration of living entities (mobs, animals) into other entity types.
 * Provides core transfiguration logic including entity targeting, type validation, and reversion.
 * Entity transfigurations are always temporary (non-permanent) and will be reverted when the
 * spell duration expires or the spell is explicitly reverted.</p>
 *
 * <p>Subclasses must implement {@link #transfigureEntity(Entity)} to define the actual transformation logic.</p>
 *
 * @author Azami7
 * @see TransfigurationBase for base transfiguration mechanics
 * @see BlockTransfiguration for block transfiguration alternative
 */
public abstract class EntityTransfiguration extends TransfigurationBase {
    /**
     * The type of entity this will transfigure.
     */
    EntityType targetType = EntityType.SHEEP;

    /**
     * A list of Entity types that will not be affected by this spell.  Only used if the allow list is empty.
     */
    List<EntityType> entityBlockedList = new ArrayList<>();

    /**
     * The original entity
     */
    Entity originalEntity = null;

    /**
     * The new entity post-transfiguration
     */
    Entity transfiguredEntity = null;

    /**
     * A list of Entity types that will be affected by this spell.
     */
    List<EntityType> entityAllowedList = new ArrayList<>();

    /**
     * The radius of the target point for the spell projectile.
     */
    double radius = 1.5;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public EntityTransfiguration(Ollivanders2 plugin) {
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
    public EntityTransfiguration(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        branch = O2MagicBranch.TRANSFIGURATION;

        entityBlockedList.add(EntityType.AREA_EFFECT_CLOUD);
        entityBlockedList.add(EntityType.EXPERIENCE_ORB);
        entityBlockedList.add(EntityType.FALLING_BLOCK);
        entityBlockedList.add(EntityType.EXPERIENCE_ORB);
        entityBlockedList.add(EntityType.EXPERIENCE_BOTTLE);
        entityBlockedList.add(EntityType.UNKNOWN);

        permanent = false;
    }

    /**
     * Searches for and transfigures an entity at the current spell projectile location.
     *
     * <p>This method is called each tick while the spell is active. It scans nearby entities within
     * the spell's radius to find a valid target. Skips entities that are already transfigured by other
     * active spells and applies success rate checks during the transfiguration attempt.</p>
     *
     * <p>If the projectile has hit a block (stopped) but no entity was transfigured, the spell fails.
     * Once an entity is successfully transfigured, subsequent calls return immediately.</p>
     *
     * <p>Validation checks include:
     * <ul>
     * <li>Entity type must match the spell's target type (unless allow list is populated)</li>
     * <li>Entity cannot be on the blocked list</li>
     * <li>Entity cannot already be transfigured by another spell</li>
     * <li>Success rate check must pass (based on player skill)</li>
     * </ul></p>
     */
    @Override
    void transfigure() {
        if (hasHitTarget() && !isTransfigured) {
            // we've hit a block and the projectile is stopped but we didn't find anything to transfigure
            common.printDebugMessage("Failed to transfigure an entity before projectile stopped", null, null, false);
            sendFailureMessage();

            kill();
            return;
        }

        if (isTransfigured)
            // we've already transfigured something
            return;

        for (Entity entity : getCloseEntities(radius)) {
            if (entity.getUniqueId().equals(player.getUniqueId()))
                continue;

            if (isTransfigured) {
                common.printDebugMessage(entity.getName() + " is already transfigured by this spell", null, null, false);
                return;
            }


            if (!canTransfigure(entity)) {
                common.printDebugMessage("Cannot target entity " + entity.getName(), null, null, false);
                return;
            }

            originalEntity = entity;
            transfiguredEntity = transfigureEntity(entity);

            if (transfiguredEntity == null) {
                kill();
                common.printDebugMessage("Transfiguration failed in " + spellType.toString(), null, null, true);
            }
            else {
                customizeEntity();
                isTransfigured = true;
                return;
            }
        }
    }

    /**
     * Determine if this entity can be transfigured by this spell.
     *
     * @param entity the entity to check
     * @return true if the entity can be transfigured, false otherwise.
     */
    protected boolean canTransfigure(@NotNull Entity entity) {
        // first check success rate
        int rand = Math.abs(Ollivanders2Common.random.nextInt() % 100);
        if (rand >= successRate) {
            common.printDebugMessage(player.getName() + " failed success check in canTransfigure()", null, null, false);
            return false;
        }

        // is this the right entity type?
        if (!targetTypeCheck(entity))
            return false;

        // is this entity already transfigured?
        for (O2Spell spell : Ollivanders2API.getSpells().getActiveSpells()) {
            if (spell instanceof TransfigurationBase) {
                if (((TransfigurationBase) spell).isEntityTransfigured(entity)) {
                    common.printDebugMessage(entity.getName() + " is already transfigured", null, null, false);
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Transfigures entity into new EntityType.
     *
     * @param entity the entity to transfigure
     * @return the transfigured entity
     */
    @Nullable
    abstract protected Entity transfigureEntity(@NotNull Entity entity);

    /**
     * Determines if this entity can be changed by this Transfiguration spell.
     *
     * @param entity the entity to check
     * @return true if the entity can be changed, false otherwise.
     */
    boolean targetTypeCheck(@NotNull Entity entity) {
        // get entity type
        EntityType eType = entity.getType();

        boolean check = true;

        if (eType == targetType) // do not change if this entity is already the target type
        {
            common.printDebugMessage("Target entity is same type as spell type.", null, null, false);
            check = false;
        }
        else if (entityBlockedList.contains(eType)) // do not change if this entity is in the blocked list
        {
            common.printDebugMessage("EntityType is on the blocked list.", null, null, false);
            check = false;
        }
        else if (!entityAllowedList.isEmpty()) // do not change if the allow list exists and this entity is not in it
        {
            if (!entityAllowedList.contains(eType)) {
                common.printDebugMessage("EntityType is not on the allowed list.", null, null, false);
                check = false;
            }
        }

        return check;
    }

    /**
     * Revert the transfigured entity back to its original state.
     *
     * <p>Removes the transfigured entity from the world and restores the original entity if the
     * spell was not set to consume the original. For regular entities, respawns the original entity
     * with preserved properties. For Items and FallingBlocks, drops or respawns them appropriately.
     * If the transfigured entity has inventory (is an InventoryHolder), drops all inventory contents
     * before removal.</p>
     *
     * <p>This method also calls {@link #doRevert()} to allow subclasses to perform spell-specific
     * cleanup beyond basic reversion.</p>
     */
    @Override
    protected void revert() {
        if (permanent)
            return;

        // remove the new entity
        if (transfiguredEntity != null) {
            if (transfiguredEntity instanceof InventoryHolder) {
                for (ItemStack stack : ((InventoryHolder) transfiguredEntity).getInventory().getContents()) {
                    if (stack == null)
                        continue;

                    transfiguredEntity.getWorld().dropItemNaturally(transfiguredEntity.getLocation(), stack);
                }
            }

            transfiguredEntity.remove();
        }
        isTransfigured = false;

        if (originalEntity == null)
            return;

        if (!consumeOriginal) {
            // get location to respawn
            Location loc;
            if (transfiguredEntity == null)
                loc = originalEntity.getLocation();
            else
                loc = transfiguredEntity.getLocation();

            if (loc.getWorld() == null) {
                common.printDebugMessage("location has a null world in " + spellType.toString(), null, null, true);
                return;
            }

            if (originalEntity.getType() == EntityType.ITEM) {
                Item item = (Item) originalEntity;
                loc.getWorld().dropItemNaturally(loc, item.getItemStack());
            }
            else if (originalEntity.getType() == EntityType.FALLING_BLOCK)
                loc.getWorld().spawnFallingBlock(loc, ((FallingBlock) originalEntity).getBlockData().clone());
            else
                respawnEntity();
        }

        doRevert();
    }

    /**
     * Respawn the original entity with attributes as close to the original as possible.
     *
     * <p>Called during reversion to restore the original entity in place of the transfigured entity.
     * Attempts to preserve entity properties including custom name, velocity, gravity, invulnerability,
     * and age (ticks lived). For Items and Enchanted Items, restores item meta and enchantments.</p>
     *
     * <p>The respawned entity is positioned at the transfigured entity's current location.</p>
     */
    void respawnEntity() {
        Entity respawn = transfiguredEntity.getWorld().spawnEntity(transfiguredEntity.getLocation(), originalEntity.getType());

        // set values as close to the original as possible
        respawn.setCustomName(originalEntity.getCustomName());
        respawn.setVelocity(transfiguredEntity.getVelocity());
        respawn.setGravity(originalEntity.hasGravity());
        respawn.setGlowing(originalEntity.isGlowing());
        respawn.setInvulnerable(originalEntity.isInvulnerable());
        // set age to be the combined of both the original and the transfigured
        respawn.setTicksLived(originalEntity.getTicksLived() + transfiguredEntity.getTicksLived());

        // restore item meta if it is an item
        if (originalEntity instanceof Item && transfiguredEntity instanceof Item) {
            ItemMeta itemMeta = ((Item) originalEntity).getItemStack().getItemMeta();
            if (itemMeta != null)
                ((Item) transfiguredEntity).getItemStack().setItemMeta(itemMeta);

            Enchantment enchantment = Ollivanders2API.getItems().enchantedItems.getEnchantment(((Item) originalEntity).getItemStack());

            if (enchantment != null) {
                Ollivanders2API.getItems().enchantedItems.addEnchantedItem((Item) transfiguredEntity, enchantment.getType(), enchantment.getMagnitude(), enchantment.getArgs());
                Ollivanders2API.getItems().enchantedItems.removeEnchantment((Item) originalEntity);
            }
        }
    }

    /**
     * Performs spell-specific revert functions beyond basic entity respawning.
     *
     * <p>Called at the end of the revert process after the original entity has been restored.
     * Override this method in subclasses to perform any additional cleanup or restoration logic
     * specific to the spell (e.g., removing applied effects, adjusting entity properties, etc.).</p>
     *
     * <p>Default implementation does nothing. Subclasses should override if needed.</p>
     */
    void doRevert() {
    }

    /**
     * Is this block transfigured by this spell
     *
     * @param block the block to check
     * @return true if transfigured, false otherwise
     */
    @Override
    public boolean isBlockTransfigured(@NotNull Block block) {
        return false;
    }

    /**
     * Is this entity transfigured by this spell
     *
     * @param entity the entity to check
     * @return true if transfigured, false otherwise
     */
    @Override
    public boolean isEntityTransfigured(@NotNull Entity entity) {
        if (permanent)
            return false;

        if (transfiguredEntity == null)
            return false;

        return transfiguredEntity.getUniqueId().equals(entity.getUniqueId());
    }

    /**
     * Let child spells optionally customize the spawned entity. This must be overridden by the child classes.
     */
    void customizeEntity() {
    }
}
