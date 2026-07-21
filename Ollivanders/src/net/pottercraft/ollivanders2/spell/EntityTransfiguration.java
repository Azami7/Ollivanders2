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
 * Base class for spells that transfigure one entity into another entity type.
 * <p>
 * Transfigurations are temporary by default and reverted when the spell expires, though subclasses may make them
 * permanent. Subclasses implement {@link #transfigureEntity(Entity)} to define the transformation.
 * </p>
 *
 * @author Azami7
 * @see Transfiguration
 * @see BlockTransfiguration
 */
public abstract class EntityTransfiguration extends Transfiguration {
    /**
     * The type of entity this will transfigure an entity into.
     */
    EntityType targetType = null;

    /**
     * A list of Entity types that will not be affected by this spell.  Only used if the allow list is empty.
     */
    List<EntityType> entityBlockedList = new ArrayList<>();

    /**
     * A list of Entity types that will be affected by this spell.
     */
    List<EntityType> entityAllowedList = new ArrayList<>();

    /**
     * The original entity
     */
    Entity originalEntity = null;

    /**
     * The new entity post-transfiguration
     */
    Entity transfiguredEntity = null;

    /**
     * The radius of the target point for the spell projectile.
     */
    double radius = 1.5;

    /**
     * Can this spell target enchanted items
     */
    boolean transfigureEnchantedItems = false;

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
        entityBlockedList.add(EntityType.EXPERIENCE_BOTTLE);
        entityBlockedList.add(EntityType.UNKNOWN);

        permanent = false;
    }

    /**
     * Attempt to transfigure a nearby entity this tick, killing the spell if a target is found but its transformation
     * fails.
     */
    @Override
    void transfigure() {
        if (isTransfigured)
            // we've already transfigured something
            return;

        for (Entity entity : getNearbyEntities(radius)) {
            if (entity.getUniqueId().equals(caster.getUniqueId()))
                continue;

            if (!canTransfigure(entity)) {
                common.printDebugMessage("Cannot target entity " + entity.getName(), null, null, false);
                continue;
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
     * Check whether an entity is an eligible target: a transfigurable type that is not already transfigured. Enchanted
     * items are eligible only when {@link #transfigureEnchantedItems} is set and their enchantment level does not
     * exceed this spell's level. The result is also gated by the spell's success rate, so it may return false at
     * random even for an otherwise-valid target.
     *
     * @param entity the entity to check
     * @return true if the entity can be transfigured, false otherwise
     */
    public boolean canTransfigure(@NotNull Entity entity) {
        // first check success rate
        int rand = Math.abs(Ollivanders2Common.random.nextInt() % 100);
        if (rand >= successRate) {
            common.printDebugMessage(caster.getName() + " failed success check in canTransfigure()", null, null, false);
            return false;
        }

        // is this the right entity type?
        if (!targetTypeCheck(entity))
            return false;

        // is this an enchanted item?
        if (isEnchantedItem(entity)) {
            if (transfigureEnchantedItems) { // this spell can transfigure enchanted items if the enchantment's level is <= to this spells level
                Enchantment enchantment = Ollivanders2API.getItems().enchantedItems.getEnchantment(((Item) entity).getItemStack());
                if (enchantment != null && enchantment.getType().getLevel().ordinal() > this.spellType.getLevel().ordinal())
                    return false;
            }
            else
                return false;
        }

        // is this entity already transfigured?
        return super.canTransfigure(entity);
    }

    /**
     * @param entity the entity to check
     * @return true if the entity is a dropped item that carries an Ollivanders2 enchantment
     */
    boolean isEnchantedItem(@NotNull Entity entity) {
        // make sure it is an item
        if (!(entity instanceof Item))
            return false;

        // if all prev checks passed, now verify this item is not enchanted
        return (Ollivanders2API.getItems().enchantedItems.isEnchanted((Item) entity));
    }

    /**
     * Perform the transformation, spawning the new entity.
     *
     * @param entity the entity to transfigure
     * @return the newly spawned entity, or null if the transformation failed
     */
    @Nullable
    abstract protected Entity transfigureEntity(@NotNull Entity entity);

    /**
     * Check whether an entity's type is eligible: not already the target type, not on the blocked list, and — if the
     * allowed list is non-empty — present on it.
     *
     * @param entity the entity to check
     * @return true if the entity's type may be transfigured, false otherwise
     */
    boolean targetTypeCheck(@NotNull Entity entity) {
        // get entity type
        EntityType eType = entity.getType();

        if (targetType != null && eType == targetType) // do not change if this entity is already the target type
        {
            common.printDebugMessage("Target entity is same type as spell type.", null, null, false);
            return false;
        }
        else if (entityBlockedList.contains(eType)) // do not change if this entity is in the blocked list
        {
            common.printDebugMessage("EntityType is on the blocked list.", null, null, false);
            return false;
        }
        else if (!entityAllowedList.isEmpty()) // do not change if the allow list exists and this entity is not in it
        {
            if (!entityAllowedList.contains(eType)) {
                common.printDebugMessage("EntityType is not on the allowed list.", null, null, false);
                return false;
            }
        }

        return true;
    }

    /**
     * Remove the transfigured entity and, unless {@link #consumeOriginal} is set, restore the original entity in its
     * place. No-op for permanent spells.
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

        if (originalEntity == null)
            return;

        if (!consumeOriginal) {
            // get location to respawn
            Location loc;
            if (transfiguredEntity == null)
                loc = originalEntity.getLocation();
            else
                loc = transfiguredEntity.getLocation();

            if (originalEntity.getType() == EntityType.ITEM) {
                Item item = (Item) originalEntity;
                world.dropItemNaturally(loc, item.getItemStack());
            }
            else if (originalEntity.getType() == EntityType.FALLING_BLOCK)
                world.spawnFallingBlock(loc, ((FallingBlock) originalEntity).getBlockData().clone());
            else
                respawnEntity();
        }

        doRevert();
    }

    /**
     * Restore the original entity in place of the transfigured one, preserving its properties as closely as possible.
     */
    void respawnEntity() {
        Entity respawn = transfiguredEntity.getWorld().spawnEntity(transfiguredEntity.getLocation(), originalEntity.getType());

        // set values as close to the original as possible
        respawn.setCustomName(originalEntity.getCustomName());
        respawn.setVelocity(transfiguredEntity.getVelocity()); // velocity of the transformed entity is maintained
        respawn.setGravity(originalEntity.hasGravity());
        respawn.setGlowing(originalEntity.isGlowing());
        respawn.setInvulnerable(originalEntity.isInvulnerable());
        respawn.setTicksLived(originalEntity.getTicksLived() + transfiguredEntity.getTicksLived()); // set age to be the combined of both the original and the transfigured

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
     * Hook for spell-specific cleanup run at the end of {@link #revert()}. The default implementation does nothing.
     */
    void doRevert() {
    }

    /**
     * @param block the block to check
     * @return always false; entity transfigurations do not affect blocks
     */
    @Override
    public boolean isTransfigured(@NotNull Block block) {
        return false;
    }

    /**
     * @param entity the entity to check
     * @return true if the given entity is the one this spell transfigured
     */
    @Override
    public boolean isTransfigured(@NotNull Entity entity) {
        if (transfiguredEntity == null)
            return false;

        return transfiguredEntity.getUniqueId().equals(entity.getUniqueId());
    }

    /**
     * Hook for subclasses to customize the newly spawned entity. The default implementation does nothing.
     */
    void customizeEntity() {
    }

    /**
     * Get the original entity before transfiguration.
     *
     * @return the original entity, or null if no entity has been transfigured
     */
    public Entity getOriginalEntity() {
        return originalEntity;
    }

    /**
     * Get the entity created by the transfiguration.
     *
     * @return the transfigured entity, or null if no entity has been transfigured
     */
    public Entity getTransfiguredEntity() {
        return transfiguredEntity;
    }

    /**
     * Get the target entity type for this transfiguration spell.
     *
     * @return the target entity type, or null if not set
     */
    public EntityType getTargetType() {
        return targetType;
    }

    /**
     * @return true if this spell can transfigure enchanted items
     */
    public boolean doesTransfigureEnchantedItems() {
        return transfigureEnchantedItems;
    }
}
