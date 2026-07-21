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
 * Base class for spells that place an {@link ItemEnchantmentType} enchantment on an item — a dropped item at the
 * projectile endpoint, or the caster's off-hand item in held-item mode (noProjectile). The enchanted item is split
 * from its stack and registered with the enchanted-items system; the enchantment magnitude scales with caster skill.
 * <p>
 * Subclasses override {@link #createEnchantmentArgs(ItemStack)} to store spell-specific data and
 * {@link #alterItem(Item)} to modify the enchanted item.
 * </p>
 *
 * @see ItemEnchantmentType
 */
public abstract class ItemEnchant extends O2Spell {
    /**
     * The type of enchantment this spell applies.
     */
    protected ItemEnchantmentType enchantmentType;

    /**
     * Restricts which {@link O2ItemType}s this spell can enchant; empty means no restriction. Use this or
     * {@link #itemTypeAllowlist}, not both.
     */
    protected ArrayList<O2ItemType> o2ItemTypeAllowList = new ArrayList<>();

    /**
     * Restricts which materials this spell can enchant; empty means no restriction. Use this or
     * {@link #o2ItemTypeAllowList}, not both.
     */
    protected ArrayList<Material> itemTypeAllowlist = new ArrayList<>();

    /**
     * Lower limit for {@link #magnitude}.
     */
    int minMagnitude = 1;

    /**
     * Upper limit for {@link #magnitude}.
     */
    int maxMagnitude = 10;

    /**
     * Scales the enchantment magnitude with caster skill; see {@link #doInitSpell()} for the formula.
     */
    double strength = 1;

    /**
     * The enchantment's magnitude, set by {@link #doInitSpell()}.
     */
    int magnitude;

    /**
     * Optional arguments stored with the enchantment; set by {@link #createEnchantmentArgs(ItemStack)}.
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
     * Set {@link #magnitude} to {@code (usesModifier / 10) * strength}, limited to [{@link #minMagnitude},
     * {@link #maxMagnitude}].
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
     * Enchant a valid target and end the spell: the caster's off-hand item in held-item mode, otherwise the first
     * valid dropped item near the projectile.
     */
    @Override
    protected void doCheckEffect() {
        if (noProjectile) {
            enchantHeldItem();
            kill();
            return;
        }

        if (hasHitBlock())
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
     * Enchant the caster's off-hand item if it is present and valid; otherwise do nothing.
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
     * Check whether an item can be enchanted: not a wand or an already-enchanted item, not an unbreakable material,
     * and matching this spell's item-type restrictions if any are set.
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
     * Enchant a dropped item, replacing the original entity with the enchanted item and a remainder stack if any.
     * Assumes {@link #canBeEnchanted(Item)} has passed.
     *
     * @param item the Item entity to enchant
     */
    private void enchantItem(@NotNull Item item) {
        enchantItem(item.getItemStack());

        // remove the original since it was replaced by the enchanted item + a new unenchanted remainder item (if any)
        item.remove();
    }

    /**
     * Enchant one item split from the given stack: register it with the enchanted-items system, apply
     * {@link #alterItem(Item)}, and drop the enchanted item plus any remainder at the spell location. Assumes
     * {@link #canBeEnchanted(ItemStack)} has passed.
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
     * Hook for subclasses to store spell-specific data in {@link #enchantmentArgs} before the item is enchanted (e.g.
     * book content for CELATUM, destination coordinates for PORTUS). The default implementation does nothing.
     *
     * @param itemStack the item being enchanted
     */
    protected void createEnchantmentArgs(ItemStack itemStack) {
    }

    /**
     * Hook for subclasses to modify the freshly enchanted item; may return a replacement dropped at the same location.
     * The default implementation returns the item unchanged.
     *
     * @param item the Item entity to modify
     * @return the altered item, or a replacement if it was re-dropped
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
