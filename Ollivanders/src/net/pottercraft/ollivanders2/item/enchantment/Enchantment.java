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
 * Abstract base class for all item enchantments.
 * <p>
 * Provides a template for enchantment behavior using event handlers for item interactions
 * (pickup, drop, despawn, item held). Subclasses must implement all abstract event handler methods
 * and set the {@link #enchantmentType} field to identify the specific enchantment type.
 * </p>
 * <p>
 * Enchantments are defined by their type ({@link ItemEnchantmentType}), magnitude (power level),
 * optional item lore, and optional arguments for custom behavior.
 * </p>
 */
public abstract class Enchantment {
    /**
     * The type of enchantment.
     * <p>
     * Defaults to GEMINIO but should be set by subclasses to the appropriate {@link ItemEnchantmentType}.
     * Determines the enchantment's identity and is used for NBT tag identification and lookup.
     * </p>
     */
    protected ItemEnchantmentType enchantmentType = ItemEnchantmentType.GEMINO;

    /**
     * A reference to the plugin.
     * <p>
     * Provides access to the Ollivanders2 plugin instance for server and logging operations.
     * </p>
     */
    protected Ollivanders2 p;

    /**
     * The power level of this enchantment.
     * <p>
     * Represents the magnitude or intensity of the enchantment effect. Higher values typically indicate
     * stronger or more frequent enchantment behavior.
     * </p>
     */
    protected int magnitude;

    /**
     * Optional custom lore to display on the enchanted item.
     * <p>
     * Can be used to add descriptive text to the item's lore, such as descriptions for enchanted
     * broomsticks. Null if no custom lore is needed.
     * </p>
     */
    protected String lore;

    /**
     * Optional configuration arguments for enchantment behavior.
     * <p>
     * Allows enchantments to be configured with custom parameters without requiring subclass changes.
     * Format and interpretation are enchantment-specific. Null if no arguments are provided.
     * </p>
     */
    protected String args;

    /**
     * Utility functions for common operations.
     * <p>
     * Provides access to shared functionality such as debug logging and random number generation.
     * </p>
     */
    protected Ollivanders2Common common;

    /**
     * Constructor for creating a new enchantment instance.
     * <p>
     * Initializes the enchantment with a plugin reference, magnitude, and optional configuration.
     * Subclasses should call this constructor and set {@link #enchantmentType} to their specific type.
     * </p>
     *
     * @param plugin   the Ollivanders2 plugin instance
     * @param mag      the magnitude (power level) of this enchantment instance
     * @param args     optional configuration arguments specific to this enchantment instance
     * @param itemLore optional custom lore to display on the enchanted item
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
     * <p>
     * Retrieves the display name from the {@link #enchantmentType}, which identifies this enchantment
     * for UI and logging purposes.
     * </p>
     *
     * @return the enchantment name, never null
     */
    @NotNull
    public String getName() {
        return enchantmentType.getName();
    }

    /**
     * Get the magnitude (power level) of this enchantment.
     * <p>
     * The magnitude determines the intensity or frequency of the enchantment effect.
     * </p>
     *
     * @return the magnitude value
     */
    public int getMagnitude() {
        return magnitude;
    }

    /**
     * Get the type of this enchantment.
     * <p>
     * The enchantment type is used for NBT tag identification and determines which enchantment
     * behaviors are triggered.
     * </p>
     *
     * @return the enchantment type, never null
     */
    @NotNull
    public ItemEnchantmentType getType() {
        return enchantmentType;
    }

    /**
     * Get the optional configuration arguments for this enchantment.
     * <p>
     * Returns the custom arguments passed during enchantment creation, allowing enchantments
     * to be configured with behavior parameters. The format is enchantment-specific.
     * </p>
     *
     * @return the arguments string, or null if no arguments were provided
     */
    @Nullable
    public String getArgs() {
        return args;
    }

    /**
     * Check if the player is holding an item enchanted with this enchantment type.
     * <p>
     * Examines both the main hand and off-hand inventory slots to determine if either contains
     * an item matching this enchantment type.
     * </p>
     *
     * @param player the player to check
     * @return true if the player is holding an item with this enchantment type in either hand, false otherwise
     */
    public boolean isHoldingEnchantedItem(Player player) {
        // check NBT tag of item in primary hand
        ItemStack primaryItem = player.getInventory().getItemInMainHand();
        String eTypeStr = Ollivanders2API.getItems().enchantedItems.getEnchantmentTypeKey(primaryItem);
        if (eTypeStr != null && eTypeStr.equalsIgnoreCase(enchantmentType.toString()))
            return true;

        // check NBT tag in item in player's off-hand
        ItemStack secondaryItem = player.getInventory().getItemInOffHand();
        eTypeStr = Ollivanders2API.getItems().enchantedItems.getEnchantmentTypeKey(secondaryItem);
        if (eTypeStr != null && eTypeStr.equalsIgnoreCase(enchantmentType.toString()))
            return true;

        return false;
    }

    /**
     * Handle item despawn events.
     * <p>
     * Called when an enchanted item despawns from the world. Prevent enchanted items from despawning.
     * </p>
     *
     * @param event the item despawn event
     */
    public void doItemDespawn(@NotNull ItemDespawnEvent event) {
        // cancel the event
        event.setCancelled(true);

        // give the item unlimited lifetime so it will not try to despawn again
        event.getEntity().setUnlimitedLifetime(true);
    }

    /**
     * Handle entity item pickup events.
     * <p>
     * Called when a living entity (not a player) picks up an enchanted item. Implement to trigger
     * enchantment effects specific to non-player entities.
     * </p>
     *
     * @param event the entity item pickup event
     */
    public abstract void doEntityPickupItem(@NotNull EntityPickupItemEvent event);

    /**
     * Handle inventory item pickup events.
     * <p>
     * Called when a hopper or other block inventory picks up an enchanted item. Implement to trigger
     * enchantment effects related to block-based inventory interactions.
     * </p>
     *
     * @param event the inventory item pickup event
     */
    public abstract void doInventoryPickupItem(@NotNull InventoryPickupItemEvent event);

    /**
     * Handle item drop events.
     * <p>
     * Called when a player drops an enchanted item. Implement to trigger enchantment effects
     * related to item dropping.
     * </p>
     *
     * @param event the item drop event
     */
    public abstract void doItemDrop(@NotNull PlayerDropItemEvent event);

    /**
     * Handle item held events.
     * <p>
     * Called when a player changes which item is held (switches slots in hotbar). Implement to trigger
     * enchantment effects related to item selection.
     * </p>
     *
     * @param event the item held event
     */
    public abstract void doItemHeld(@NotNull PlayerItemHeldEvent event);
}
