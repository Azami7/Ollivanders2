package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.item.enchantment.ItemEnchantmentType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Abstract base class for item-to-item material type transfiguration spells.
 *
 * <p>Manages the transfiguration of dropped item entities by changing their material type. These
 * transfigurations are always permanent. The number of items changed in a stack scales with
 * the caster's skill level via {@link #calculateNumberOfItems()}.</p>
 *
 * <p>Subclasses configure the transformation by either populating {@link #transfigurationMap}
 * for specific material-to-material mappings, or setting {@link #targetType} for a catch-all
 * target material.</p>
 *
 * @author Azami7
 * @see Transfiguration for base transfiguration mechanics
 * @see EntityTransfiguration for entity transfiguration alternative
 */
public abstract class ItemToItemTransfiguration extends Transfiguration {
    /**
     * If this is populated, any material type key will be changed to the value
     */
    Map<Material, Material> transfigurationMap = new HashMap<>();

    /**
     * If the transfigurationMap is not populated, any item will be changed to this type
     */
    Material targetType = Material.BOWL;

    /**
     * Can this spell transfigure enchanted items?
     */
    boolean transfigureEnchanted = false;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
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

        // world guard
        if (Ollivanders2.worldGuardEnabled) {
            worldGuardFlags.add(Flags.ITEM_PICKUP);
            worldGuardFlags.add(Flags.ITEM_DROP);
        }

        permanent = true;
    }

    /**
     * Look for a targetable item and change its material type.
     *
     * <p>Finds the first valid item within range, determines the new material type from
     * the {@link #transfigurationMap} or {@link #targetType}, and replaces up to
     * {@link #calculateNumberOfItems()} items in the stack. If the entire stack is consumed,
     * the original item is removed; otherwise the original stack is reduced and a new item
     * with the transfigured material is dropped.</p>
     */
    @Override
    protected void transfigure() {
        if (isTransfigured) // we've already transfigured something
            return;

        Item originalItem = getTargetableItem(defaultRadius, null);
        if (originalItem == null) // keep searching next tick
            return;

        // get the original item stack
        ItemStack originalStack = originalItem.getItemStack();

        // determine the new material type
        Material transType = targetType;
        if (transfigurationMap.containsKey(originalStack.getType()))
            transType = transfigurationMap.get(originalStack.getType());

        // determine how much of the stack will be changed
        int transAmount = calculateNumberOfItems();
        int originalAmount = originalStack.getAmount();
        if (transAmount > originalAmount)
            transAmount = originalAmount;

        // create the new item stack
        ItemStack transStack = new ItemStack(transType, transAmount);
        transStack.setItemMeta(originalStack.getItemMeta());

        // update the original item stack to remove the transfigured amount
        if (transAmount >= originalAmount)
            originalItem.remove();
        else {
            originalStack.setAmount(originalAmount - transAmount);
            originalItem.setItemStack(originalStack);
        }

        // drop the new item stack
        world.dropItem(location, transStack);
        isTransfigured = true;
    }

    /**
     * Find the first item within the given radius that can be transfigured.
     *
     * @param radius         the search radius around the spell's current location
     * @param restrictedUUID optional UUID of an item to skip (used by subclasses to exclude a previously targeted item)
     * @return the first targetable item, or null if none found
     */
    @Nullable
    Item getTargetableItem(double radius, @Nullable UUID restrictedUUID) {
        for (Item item : EntityCommon.getItemsInRadius(location, radius)) {
            if (item.getUniqueId().equals(restrictedUUID))
                continue;

            if (!canTransfigure(item)) {
                common.printDebugMessage("ItemToItemTransfiguration.transfigure: item cannot be transfigured", null, null, false);
                continue;
            }

            return item;
        }

        return null;
    }

    /**
     * Determine if this item can be transfigured by this spell.
     *
     * <p>Checks the following conditions in order:</p>
     *
     * <ul>
     * <li>Entity must be an Item</li>
     * <li>Item material must not be unbreakable</li>
     * <li>Item must not be enchanted (unless {@link #transfigureEnchanted} is true and the enchantment level is within range)</li>
     * <li>If {@link #transfigurationMap} is populated, the item material must be a key in the map</li>
     * <li>Item must not already be the target of an active non-permanent transfiguration</li>
     * </ul>
     *
     * @param entity the entity to check
     * @return true if the entity can be transfigured, false otherwise
     */
    @Override
    public boolean canTransfigure(Entity entity) {
        if (!(entity instanceof Item))
            return false;

        Item item = (Item) entity;

        // is this item an unbreakable material type?
        if (Ollivanders2Common.getUnbreakableMaterials().contains(item.getItemStack().getType()))
            return false;

        // is the item enchanted?
        if (Ollivanders2API.getItems().enchantedItems.isEnchanted(item)) {
            if (transfigureEnchanted) {
                ItemEnchantmentType enchantmentType = Ollivanders2API.getItems().enchantedItems.getEnchantmentType(item.getItemStack());
                if (enchantmentType != null && (enchantmentType.getLevel().ordinal() > spellType.getLevel().ordinal()))
                    return false;
            }
            else
                return false;
        }

        // is there a transfiguration map and the item type is not in it
        if (!transfigurationMap.isEmpty() && !transfigurationMap.containsKey(item.getItemStack().getType()))
            return false;

        // is the item already transfigured
        return super.canTransfigure(item);
    }

    /**
     * How many items in the item stack can this spell change - scales with caster level.
     *
     * @return the number of items to change
     */
    public int calculateNumberOfItems() {
        return (int)Math.floor(usesModifier / 10);
    }

    /**
     * Item-to-item transfigurations do not track block changes.
     *
     * @param block the block to check
     * @return always false
     */
    @Override
    public boolean isTransfigured(@NotNull Block block) {
        return false;
    }

    /**
     * Item-to-item transfigurations are always permanent and do not track affected entities.
     *
     * @param entity the entity to check
     * @return always false
     */
    @Override
    public boolean isTransfigured(@NotNull Entity entity) {
        return false;
    }

    /**
     * No-op since item-to-item transfigurations are always permanent.
     */
    protected void revert() {
    }

    /**
     * Whether this spell can transfigure enchanted items.
     *
     * @return true if this spell can transfigure enchanted items, false otherwise
     */
    public boolean doesTransfigureEnchanted() {
        return transfigureEnchanted;
    }
}
