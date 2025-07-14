package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.item.O2Item;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.item.enchantment.ItemEnchantmentType;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Place an enchantment on an item.
 */
public abstract class ItemEnchant extends O2Spell {
    /**
     * The type of enchantment
     */
    protected ItemEnchantmentType enchantmentType;

    /**
     * The list of item types that this can enchant, if limited. When empty, all item types can be enchanted
     * Cannot be mixed and match with o2ItemTypeAllowList (only use one or the other)
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
    int maxMagnitude = 100;

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
    String args = "";

    /**
     * Does this spell enchant a held item or cast a projectile to affect an Item in world
     */
    boolean enchantsHeldItem = false;

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
     * Set enchantment magnitude based on caster's skill
     */
    @Override
    void doInitSpell() {
        magnitude = (int) ((usesModifier / 4) * strength);

        if (magnitude < minMagnitude)
            magnitude = minMagnitude;
        else if (magnitude > maxMagnitude)
            magnitude = maxMagnitude;

        common.printDebugMessage("Magnitude for enchantment = " + magnitude, null, null, false);
    }

    /**
     * Override checkEffect to handle the enchantments that target an item in the player's inventory.
     */
    @Override
    public void checkEffect() {
        if (enchantsHeldItem) {
            enchantHeldItem();
            stopProjectile();
            kill();
        }
        else
            super.checkEffect();
    }

    /**
     * Enchants the item held in the player's off-hand
     */
    protected void enchantHeldItem () {
        ItemStack targetItem = player.getInventory().getItemInOffHand();

        // make sure they are actually holding something and that it can be enchanted
        if ((targetItem.getType() == Material.AIR) || !canBeEnchanted(targetItem)) {
            common.printDebugMessage("ItemStackEnchant: no item in off hand or item cannot be enchanted", null, null, false);
            return;
        }

        // enchant the item
        enchantItem(targetItem);
    }

    /**
     * Add the enchantment to an item stack in the projectile's location
     */
    @Override
    protected void doCheckEffect() {
        if (hasHitTarget()) {
            kill();
            return;
        }

        List<Item> items = getItems(1.5);
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
     * Can this item be enchanted by this spell?
     *
     * @param itemStack the item to check
     * @return true if it can be enchanted, false otherwise
     */
    protected boolean canBeEnchanted(@NotNull ItemStack itemStack) {
        // if this is a wand or an enchanted item, skip it, we cannot stack enchantments
        if (Ollivanders2API.getItems().getWands().isWand(itemStack) || (Ollivanders2API.getItems().enchantedItems.isEnchanted(itemStack))) {
            common.printDebugMessage("ItemEnchant.canBeEnchanted: item is a want or an enchanted item", null, null, false);
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
     * Can this item be enchanted by this spell?
     *
     * @param item the item to check
     * @return true if it can be enchanted, false otherwise
     */
    protected boolean canBeEnchanted(@NotNull Item item) {
        return canBeEnchanted(item.getItemStack());
    }

    /**
     * Enchant the item, assumes the check to verify this item can be enchanted has been done.
     *
     * @param item the item to enchant
     */
    private void enchantItem(@NotNull Item item) {
        enchantItem(item.getItemStack());
    }

    /**
     * Enchant the item stack, assumes the check to verify this item can be enchanted has been done.
     *
     * @param itemStack the itemStack to enchant
     */
    private void enchantItem(@NotNull ItemStack itemStack) {
        // init the enchantment args
        initEnchantmentArgs(itemStack);

        // split off the enchanted item from the rest of the stack by cloning it and then decrementing the original by 1
        ItemStack enchantedItemStack = itemStack.clone();
        // reduce the original item stack size by 1
        itemStack.setAmount(itemStack.getAmount() - 1);
        // set the amount to 1
        enchantedItemStack.setAmount(1);

        // do any alterations on the new item stack
        enchantedItemStack = alterItem(enchantedItemStack);

        // enchant item
        Item enchantedItem = player.getWorld().dropItem(location, enchantedItemStack);
        Ollivanders2API.getItems().enchantedItems.addEnchantedItem(enchantedItem, enchantmentType, magnitude, args);

        // drop the remainder of the original stack in world where the player is
        if (itemStack.getAmount() > 0) {
            player.getWorld().dropItem(location, itemStack);
        }
    }

    /**
     * Set the enchantment arg string. This needs to be overridden by the classes that need it.
     *
     * @param itemStack the item being enchanted
     */
    protected void initEnchantmentArgs(ItemStack itemStack) { }

    /**
     * Do any after effects on the item. This needs to be overridden by the classes that need it.
     *
     * @param item the item to affect
     */
    @Nullable
    protected Item alterItem(Item item) { return item; }

    /**
     * Do any after effects on the item. This needs to be overridden by the classes that need it.
     *
     * @param itemStack the item to affect
     */
    protected ItemStack alterItem(ItemStack itemStack) { return itemStack; }
}
