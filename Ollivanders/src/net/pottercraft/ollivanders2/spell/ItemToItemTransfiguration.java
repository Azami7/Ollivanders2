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
 * Base class for spells that permanently change the material type of a dropped item — into a single catch-all
 * material or a per-source-material mapping. The number of items changed in a stack scales with the caster's skill.
 *
 * @author Azami7
 * @see Transfiguration
 * @see EntityTransfiguration
 */
public abstract class ItemToItemTransfiguration extends Transfiguration {
    /**
     * Per-source-material target overrides. An item whose type is a key is changed to the mapped value; any other
     * item is changed to {@link #targetType}. Empty by default.
     */
    Map<Material, Material> transfigurationMap = new HashMap<>();

    /**
     * The default material items are changed into when not overridden by {@link #transfigurationMap}.
     */
    Material targetType = Material.BOWL;

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
     * Find a targetable dropped item near the projectile and permanently convert up to
     * {@link #calculateNumberOfItems()} items of its stack into the new material. Does nothing until a target is found.
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
     * Check whether an entity is an eligible target: a dropped item of a breakable material that this spell can turn
     * into something (present in {@link #transfigurationMap} when that map is used). Enchanted items are eligible only
     * when {@link #transfigureEnchanted} is set and their enchantment level does not exceed this spell's level. The
     * result is also gated by the spell's success rate, so it may return false at random even for a valid item.
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
