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
 * Abstract base class for all item enchantments. An enchantment reacts to item events (pickup, drop, despawn, held) to
 * produce its effect; subclasses implement the abstract {@code do...} handlers and set {@link #enchantmentType}.
 */
public abstract class Enchantment {
    /**
     * The type of this enchantment; set by the subclass and used for NBT identification.
     */
    protected ItemEnchantmentType enchantmentType = ItemEnchantmentType.GEMINO;

    /**
     * A reference to the plugin.
     */
    protected Ollivanders2 p;

    /**
     * The power level of this enchantment; higher means a stronger or more frequent effect.
     */
    protected int magnitude;

    /**
     * Optional custom lore for the enchanted item; null for none.
     */
    protected String lore;

    /**
     * Optional enchantment-specific configuration arguments; null for none.
     */
    protected String args;

    /**
     * Common functions
     */
    protected Ollivanders2Common common;

    /**
     * Constructor
     *
     * @param plugin   the Ollivanders2 plugin instance
     * @param mag      the magnitude (power level) of this enchantment
     * @param args     optional configuration arguments for this enchantment
     * @param itemLore optional custom lore for the enchanted item
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
     * Get the magnitude (power level) of this enchantment.
     *
     * @return the magnitude value
     */
    public int getMagnitude() {
        return magnitude;
    }

    /**
     * Get the type of this enchantment.
     *
     * @return the enchantment type
     */
    @NotNull
    public ItemEnchantmentType getType() {
        return enchantmentType;
    }

    /**
     * Get the optional configuration arguments for this enchantment.
     *
     * @return the arguments string, or null if none were provided
     */
    @Nullable
    public String getArgs() {
        return args;
    }

    /**
     * Check whether the player is holding an item of this enchantment type in either hand.
     *
     * @param player the player to check
     * @return true if this enchantment is in the player's main or off hand, false otherwise
     */
    public boolean isHoldingEnchantedItem(Player player) {
        ItemStack primaryItem = player.getInventory().getItemInMainHand();
        String eTypeStr = Ollivanders2API.getItems().enchantedItems.getEnchantmentTypeKey(primaryItem);
        if (eTypeStr != null && eTypeStr.equalsIgnoreCase(enchantmentType.toString()))
            return true;

        ItemStack secondaryItem = player.getInventory().getItemInOffHand();
        eTypeStr = Ollivanders2API.getItems().enchantedItems.getEnchantmentTypeKey(secondaryItem);
        if (eTypeStr != null && eTypeStr.equalsIgnoreCase(enchantmentType.toString()))
            return true;

        return false;
    }

    /**
     * Keep an enchanted item from despawning by cancelling the despawn and giving it unlimited lifetime.
     *
     * @param event the item despawn event
     */
    public void doItemDespawn(@NotNull ItemDespawnEvent event) {
        event.setCancelled(true);
        event.getEntity().setUnlimitedLifetime(true);
    }

    /**
     * React to a non-player entity picking up an item of this enchantment.
     *
     * @param event the entity item pickup event
     */
    public abstract void doEntityPickupItem(@NotNull EntityPickupItemEvent event);

    /**
     * React to a block inventory (e.g. hopper) picking up an item of this enchantment.
     *
     * @param event the inventory item pickup event
     */
    public abstract void doInventoryPickupItem(@NotNull InventoryPickupItemEvent event);

    /**
     * React to a player dropping an item of this enchantment.
     *
     * @param event the item drop event
     */
    public abstract void doItemDrop(@NotNull PlayerDropItemEvent event);

    /**
     * React to a player switching to or from an item of this enchantment.
     *
     * @param event the item held event
     */
    public abstract void doItemHeld(@NotNull PlayerItemHeldEvent event);
}
