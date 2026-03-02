package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.item.O2Item;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.item.enchantment.ItemEnchantmentType;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for item enchantment spells.
 *
 * <p>Provides common functionality for spells that place enchantments on items, either via
 * projectile targeting (finding nearby dropped items) or held-item mode (targeting the caster's
 * off-hand item). When an item is enchanted, it is split from its stack (if applicable) and
 * registered with the enchanted items system along with spell-specific magnitude and arguments.</p>
 *
 * <p>Spell Mechanics:</p>
 *
 * <ul>
 * <li>Magnitude is calculated from caster skill: magnitude = (int)((usesModifier / 10) * strength), clamped to [minMagnitude, maxMagnitude]</li>
 * <li>Item stacks are split when enchanted: one item is enchanted, remainder (if any) is dropped separately</li>
 * <li>Wands and already-enchanted items cannot be re-enchanted</li>
 * <li>Unbreakable materials cannot be enchanted</li>
 * <li>Item type filtering via o2ItemTypeAllowList (O2ItemType) or itemTypeAllowlist (Material)</li>
 * <li>Projectile mode: targets items at the projectile endpoint</li>
 * <li>Held-item mode (noProjectile): targets the caster's off-hand item</li>
 * <li>WorldGuard integration: requires ITEM_PICKUP and ITEM_DROP flags</li>
 * </ul>
 *
 * <p>Subclasses should override {@link #createEnchantmentArgs(ItemStack)} to store spell-specific
 * data with the enchantment, and {@link #alterItem(Item)} to apply visual or functional modifications.</p>
 *
 * @see net.pottercraft.ollivanders2.item.enchantment.ItemEnchantmentType for supported enchantment types
 */
public abstract class ItemEnchant extends O2Spell {
    /**
     * The type of enchantment
     */
    protected ItemEnchantmentType enchantmentType;

    /**
     * The list of item types that this can enchant, if limited. When empty, all item types can be enchanted
     * Cannot be mixed and match with itemTypeAllowList (only use one or the other)
     */
    protected ArrayList<O2ItemType> o2ItemTypeAllowList = new ArrayList<>();

    /**
     * The list of item types that this can enchant, if limited. When empty, all item types can be enchanted.
     * Cannot be mixed and match with o2ItemTypeAllowList (only use one or the other)
     */
    protected ArrayList<Material> itemTypeAllowlist = new ArrayList<>();

    /**
     * Minimum magnitude
     */
    int minMagnitude = 1;

    /**
     * Maximum magnitude
     */
    int maxMagnitude = 10;

    /**
     * Strength multiplier for this enchantment
     */
    double strength = 1;

    /**
     * Magnitude of this enchantment - this is the final value used to determine the enchantment effect
     */
    int magnitude;

    /**
     * The optional arguments for the enchantment
     */
    String enchantmentArgs = "";

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public ItemEnchant(Ollivanders2 plugin) {
        super(plugin);
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public ItemEnchant(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        // world guard flags
        if (Ollivanders2.worldGuardEnabled) {
            worldGuardFlags.add(Flags.ITEM_PICKUP);
            worldGuardFlags.add(Flags.ITEM_DROP);
        }

        // pass-through materials
        projectilePassThrough.remove(Material.WATER);
    }

    /**
     * Calculate the enchantment magnitude based on the caster's spell experience.
     *
     * <p>Magnitude is calculated using the formula: magnitude = (int)((usesModifier / 10) * strength),
     * then clamped to the range [minMagnitude, maxMagnitude]. The magnitude determines how powerful
     * the enchantment effect will be.</p>
     */
    @Override
    void doInitSpell() {
        magnitude = (int) ((usesModifier / 10) * strength);

        if (magnitude < minMagnitude)
            magnitude = minMagnitude;
        else if (magnitude > maxMagnitude)
            magnitude = maxMagnitude;

        common.printDebugMessage("Magnitude for enchantment = " + magnitude, null, null, false);
    }

    /**
     * Check for nearby items and apply enchantment to the first valid target found.
     *
     * <p>For projectile-based spells (noProjectile == false):
     * Searches for nearby items and enchants the first one that passes validation.
     * When found, makes a final check at the projectile's endpoint before killing the spell.
     * <p>
     * For held-item spells (noProjectile == true):
     * Directly enchants the item held in the player's off-hand, if valid.</p>
     */
    @Override
    protected void doCheckEffect() {
        if (noProjectile) {
            enchantHeldItem();
            kill();
            return;
        }

        if (hasHitTarget())
            kill();

        List<Item> items = getNearbyItems(defaultRadius);
        for (Item item : items) {
            // check if this item can be enchanted
            if (!canBeEnchanted(item))
                continue;

            // enchant the item
            enchantItem(item);

            // kill the spell and return
            kill();
            return;
        }
    }

    /**
     * Enchant the item held in the player's off-hand.
     *
     * <p>Used in held-item mode (noProjectile == true) to enchant the item currently in
     * the caster's off-hand inventory slot. If the caster is not holding a valid item
     * (empty slot or item fails validation), the spell completes without enchanting anything.</p>
     */
    protected void enchantHeldItem() {
        ItemStack targetItem = caster.getInventory().getItemInOffHand();

        // make sure they are actually holding something and that it can be enchanted
        if ((targetItem.getType() == Material.AIR) || !canBeEnchanted(targetItem)) {
            common.printDebugMessage("ItemStackEnchant: no item in off hand or item cannot be enchanted", null, null, false);
        }
        else {
            // enchant the item
            enchantItem(targetItem);
        }
    }

    /**
     * Determine if an item can be enchanted by this spell.
     *
     * <p>An item can be enchanted if it passes all validation checks:</p>
     *
     * <ul>
     * <li>Not a wand (wands cannot have enchantments stacked)</li>
     * <li>Not already enchanted (cannot stack enchantments on the same item)</li>
     * <li>Not an unbreakable material</li>
     * <li>Matches the spell's item type restrictions (if any)</li>
     * </ul>
     *
     * @param itemStack the item to check
     * @return true if the item can be enchanted, false otherwise
     */
    public boolean canBeEnchanted(@NotNull ItemStack itemStack) {
        // if this is a wand or an enchanted item, skip it, we cannot stack enchantments
        if (Ollivanders2API.getItems().getWands().isWand(itemStack) || (Ollivanders2API.getItems().enchantedItems.isEnchanted(itemStack))) {
            common.printDebugMessage("ItemEnchant.canBeEnchanted: item is a wand or an enchanted item", null, null, false);
            return false;
        }

        // this is not an unbreakable material
        if (Ollivanders2Common.getUnbreakableMaterials().contains(itemStack.getType())) {
            common.printDebugMessage("ItemEnchant.canBeEnchanted: item stack is an unbreakable material type", null, null, false);
            return false;
        }

        // if this enchantment has an allow lists, check them
        if (!o2ItemTypeAllowList.isEmpty()) {
            O2ItemType itemType = O2Item.getItemType(itemStack);
            if (itemType == null || !(o2ItemTypeAllowList.contains(itemType))) {
                common.printDebugMessage("ItemEnchant.canBeEnchanted: item type " + itemType + " cannot be targeted by this spell", null, null, false);
                return false;
            }
        }

        if (!itemTypeAllowlist.isEmpty()) {
            Material material = itemStack.getType();
            if (!(itemTypeAllowlist.contains(material))) {
                common.printDebugMessage("ItemEnchant.canBeEnchanted: material type " + material + " cannot be targeted by this spell", null, null, false);
                return false;
            }
        }

        return true;
    }

    /**
     * Determine if a dropped item can be enchanted by this spell.
     *
     * @param item the Item entity to check
     * @return true if the item can be enchanted, false otherwise
     * @see #canBeEnchanted(ItemStack)
     */
    public boolean canBeEnchanted(@NotNull Item item) {
        return canBeEnchanted(item.getItemStack());
    }

    /**
     * Enchant a dropped item entity.
     *
     * <p>Enchants the item and removes the original entity from the world.
     * The enchanted and remainder items are dropped as new entities.
     * Assumes validation has already been performed via {@link #canBeEnchanted(Item)}.</p>
     *
     * @param item the Item entity to enchant
     */
    private void enchantItem(@NotNull Item item) {
        enchantItem(item.getItemStack());

        // remove the original since it was replaced by the enchanted item + a new unenchanted remainder item (if any)
        item.remove();
    }

    /**
     * Enchant an item stack.
     *
     * <p>Performs the complete enchantment process:</p>
     *
     * <ul>
     * <li>Calls {@link #createEnchantmentArgs(ItemStack)} to generate spell-specific enchantment data</li>
     * <li>Splits the stack: one item for enchanting, remainder (if any) to be dropped separately</li>
     * <li>Drops the enchanted item at the spell location</li>
     * <li>Calls {@link #alterItem(Item)} to apply any spell-specific modifications</li>
     * <li>Registers the item with the enchanted items system</li>
     * <li>Drops any remaining items from the original stack</li>
     * </ul>
     *
     * <p>Assumes validation has already been performed via {@link #canBeEnchanted(ItemStack)}.</p>
     *
     * @param itemStack the ItemStack to enchant
     */
    private void enchantItem(@NotNull ItemStack itemStack) {
        common.printDebugMessage("ItemEnchant.enchantItem: enchanting " + itemStack.getType(), null, null, false);
        common.printDebugMessage("original item stack amount = " + itemStack.getAmount(), null, null, false);

        // create the enchantment args
        createEnchantmentArgs(itemStack);

        // split off the enchanted item from the rest of the stack by cloning it and then decrementing the original by 1
        ItemStack enchantedItemStack = itemStack.clone();

        // reduce the original item stack size by 1
        itemStack.setAmount(itemStack.getAmount() - 1);
        common.printDebugMessage("original item stack amount now = " + itemStack.getAmount(), null, null, false);

        // set the amount to 1
        enchantedItemStack.setAmount(1);

        // enchant item
        Item enchantedItem = caster.getWorld().dropItem(location, enchantedItemStack);

        // do any alterations on the new item stack
        enchantedItem = alterItem(enchantedItem);

        Ollivanders2API.getItems().enchantedItems.addEnchantedItem(enchantedItem, enchantmentType, magnitude, enchantmentArgs);
        common.printDebugMessage("ItemEnchant.enchantItem: added enchantment " + enchantmentType.getName(), null, null, false);

        // drop the remainder of the original stack in world where the player is
        if (itemStack.getAmount() > 0) {
            caster.getWorld().dropItem(location, itemStack);
        }
    }

    /**
     * Generate spell-specific enchantment argument data.
     *
     * <p>Called during the enchantment process to create and store spell-specific data
     * (e.g., book content for CELATUM, destination coordinates for PORTUS) that will be
     * associated with the enchanted item. Subclasses should override this method to set
     * {@link #enchantmentArgs} with relevant data. The default implementation does nothing.</p>
     *
     * @param itemStack the item being enchanted
     */
    protected void createEnchantmentArgs(ItemStack itemStack) {
    }

    /**
     * Apply spell-specific modifications to an enchanted item.
     *
     * <p>Called after an item is registered with the enchanted items system, allowing
     * subclasses to apply visual or functional modifications (e.g., clearing book pages,
     * changing item properties). The method can return the same item or a modified replacement
     * dropped at the same location. Subclasses should override this method if modifications
     * are needed. The default implementation returns the item unchanged.</p>
     *
     * @param item the Item entity to modify
     * @return the altered item (may be a replacement if the item was removed and re-dropped)
     */
    @NotNull
    public Item alterItem(Item item) {
        return item;
    }

    /**
     * Get the enchantment type of this spell.
     *
     * @return the ItemEnchantmentType
     */
    public ItemEnchantmentType getEnchantmentType() {
        return enchantmentType;
    }

    /**
     * Get the minimum magnitude for this enchantment.
     *
     * @return the minimum magnitude value
     */
    public int getMinMagnitude() {
        return minMagnitude;
    }

    /**
     * Get the maximum magnitude for this enchantment.
     *
     * @return the maximum magnitude value
     */
    public int getMaxMagnitude() {
        return maxMagnitude;
    }

    /**
     * Get the calculated magnitude for this enchantment.
     *
     * @return the magnitude value
     */
    public int getMagnitude() {
        return magnitude;
    }

    /**
     * Get the strength multiplier for this enchantment.
     *
     * @return the strength multiplier
     */
    public double getStrength() {
        return strength;
    }

    /**
     * Get the generated enchantment arguments.
     *
     * @return the enchantment arguments string
     */
    public String getEnchantmentArgs() {
        return enchantmentArgs;
    }

    /**
     * Get the list of O2ItemType restrictions for this spell.
     *
     * @return the O2ItemType allow list (empty if no restrictions)
     */
    public List<O2ItemType> getO2ItemTypeAllowList() {
        return o2ItemTypeAllowList;
    }
}
