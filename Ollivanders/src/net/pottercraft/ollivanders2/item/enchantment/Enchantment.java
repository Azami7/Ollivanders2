package net.pottercraft.ollivanders2.item.enchantment;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Item enchantment
 */
public abstract class Enchantment {
    /**
     * The type of enchantment
     */
    ItemEnchantmentType enchantmentType = ItemEnchantmentType.GEMINIO;

    /**
     * A callback to the plugin
     */
    Ollivanders2 p;

    /**
     * The magnitude of this enchantment
     */
    int magnitude;

    /**
     * Optional lore to add to the item for this enchantment, such as for a broom
     */
    String lore;

    /**
     * Optional arguments for this enchantment
     */
    String args;

    /**
     * Common functions
     */
    Ollivanders2Common common;

    /**
     * Constructor
     *
     * @param plugin   a callback to the plugin
     * @param mag      the magnitude of this enchantment
     * @param args     optional arguments for this enchantment
     * @param itemLore the optional lore for this enchantment
     */
    public Enchantment(@NotNull Ollivanders2 plugin, int mag, @Nullable String args, @Nullable String itemLore) {
        p = plugin;
        magnitude = mag;
        lore = itemLore;
        this.args = args;

        common = new Ollivanders2Common(p);
    }

    /**
     * Get the name of this enchantment.
     *
     * @return the enchantment name
     */
    @NotNull
    public String getName() {
        return enchantmentType.getName();
    }

    /**
     * Get the magnitude for this enchantment
     *
     * @return the magnitude
     */
    public int getMagnitude() {
        return magnitude;
    }

    /**
     * Get the type of enchantment
     *
     * @return the enchantment type
     */
    @NotNull
    public ItemEnchantmentType getType() {
        return enchantmentType;
    }

    /**
     * Get the optional arguments for this enchantment
     *
     * @return the args string
     */
    @Nullable
    public String getArgs() {
        return args;
    }

    /**
     * Is the player holding an item enchanted with this enchantment type?
     *
     * @param player the player to check
     * @return true if they are holding an item with this enchantment type, false otherwise
     */
    boolean isHoldingEnchantedItem(Player player) {
        // first check NBT tag of item in primary hand
        ItemStack primaryItem = player.getInventory().getItemInMainHand();
        String eTypeStr = Ollivanders2API.getItems().enchantedItems.getEnchantmentTypeKey(primaryItem);
        if (eTypeStr != null)
            return eTypeStr.equalsIgnoreCase(enchantmentType.toString());

        // check NBT tag in item in player's off-hand
        ItemStack secondaryItem = player.getInventory().getItemInOffHand();
        eTypeStr = Ollivanders2API.getItems().enchantedItems.getEnchantmentTypeKey(secondaryItem);
        if (eTypeStr != null)
            return eTypeStr.equalsIgnoreCase(enchantmentType.toString());

        // no NBT tag, this is not enchanted, return false
        return false;
    }

    /**
     * Handle item despawn events
     *
     * @param event the item despawn event
     */
    abstract public void doItemDespawn(@NotNull ItemDespawnEvent event);

    /**
     * Handle item pickup events
     *
     * @param event the item pickup event
     */
    abstract public void doEntityPickupItem(@NotNull EntityPickupItemEvent event);

    /**
     * Handle item pickup events
     *
     * @param event the item pickup event
     */
    abstract public void doInventoryPickupItem(@NotNull InventoryPickupItemEvent event);

    /**
     * Handle item drop events
     *
     * @param event the item drop event
     */
    abstract public void doItemDrop(@NotNull PlayerDropItemEvent event);

    /**
     * Handle item held events
     *
     * @param event the item drop event
     */
    abstract public void doItemHeld(@NotNull PlayerItemHeldEvent event);
}
