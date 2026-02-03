package net.pottercraft.ollivanders2.item.enchantment;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.MagicLevel;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Manager for enchanted items and their magical properties.
 * <p>
 * Enchantments are magical effects applied to items using NBT (Named Binary Tag) data stored in item metadata.
 * This class handles:
 * <ul>
 * <li>Creating and tracking enchanted items with unique identifiers</li>
 * <li>Storing enchantment metadata (type, magnitude, arguments) as NBT tags</li>
 * <li>Retrieving enchantment objects from items</li>
 * <li>Handling enchantment event listeners (pickup, drop, despawn, item held)</li>
 * <li>Managing cursed item detection with level-based awareness</li>
 * </ul>
 * </p>
 * <p>
 * Enchantments are stored as NBT tags on ItemStack metadata, allowing them to persist across server
 * restarts and item operations. Each enchantment is cached by its unique ID for efficient event handling.
 * </p>
 * <p>
 * Examples: broom flight (VOLATUS), port keys (PORTUS), item duplication (GEMINO), concealment (CELATUM).
 * </p>
 *
 * @see Enchantment the abstract base class for all enchantment implementations
 * @see ItemEnchantmentType the enumeration of available enchantment types
 */
public class EnchantedItems implements Listener {
    /**
     * Reference to the Ollivanders2 plugin instance.
     */
    private Ollivanders2 p;

    /**
     * Utility functions for common operations across the plugin.
     */
    private final Ollivanders2Common common;

    /**
     * Namespace key for storing enchantment type in item NBT tags.
     * <p>
     * Stores the ItemEnchantmentType enum name as a string (e.g., "GEMINO", "VOLATUS", "PORTUS").
     * </p>
     */
    static public NamespacedKey enchantmentType;

    /**
     * Namespace key for storing enchantment magnitude in item NBT tags.
     * <p>
     * Stores the power level of the enchantment as an integer (1 or greater).
     * </p>
     */
    static public NamespacedKey enchantmentMagnitude;

    /**
     * Namespace key for storing enchantment ID in item NBT tags.
     * <p>
     * Stores the unique identifier (typically a UUID string) used to look up the enchantment in the cache.
     * </p>
     */
    static public NamespacedKey enchantmentID;

    /**
     * Namespace key for storing enchantment arguments in item NBT tags.
     * <p>
     * Stores optional enchantment-specific configuration arguments as a string (e.g., PORTUS destination coordinates).
     * </p>
     */
    static public NamespacedKey enchantmentArgs;

    /**
     * Cache of enchantment objects indexed by their unique ID.
     * <p>
     * Maps enchantment IDs to their corresponding Enchantment instances for fast lookup during event handling.
     * This avoids recreating Enchantment objects on every item interaction.
     * </p>
     */
    private final HashMap<String, Enchantment> enchantedItems = new HashMap<>();

    /**
     * Whether broomstick items are enabled in the plugin configuration
     */
    private static boolean enableBrooms;

    /**
     * Constructor for creating a new EnchantedItems manager.
     * <p>
     * Initializes the enchantment system and sets up the NamespacedKey instances used to store
     * enchantment metadata in item NBT tags. The keys use the plugin as their namespace to avoid
     * conflicts with other plugins.
     * </p>
     *
     * @param plugin the Ollivanders2 plugin instance providing access to configuration and logging
     */
    public EnchantedItems(@NotNull Ollivanders2 plugin) {
        p = plugin;
        common = new Ollivanders2Common(p);

        enchantmentType = new NamespacedKey(p, "o2enchantment_name");
        enchantmentMagnitude = new NamespacedKey(p, "o2enchantment_magnitude");
        enchantmentID = new NamespacedKey(p, "o2enchantment_id");
        enchantmentArgs = new NamespacedKey(p, "o2enchantment_args");
    }

    /**
     * Initialize enchantment configuration when the plugin is enabled.
     * <p>
     * Loads configuration settings from the plugin's config file, such as whether broomstick
     * items should be enabled. This method is called once during plugin startup.
     * </p>
     */
    public void onEnable() {
        enableBrooms = p.getConfig().getBoolean("enableBrooms");
        if (enableBrooms)
            p.getLogger().info("Enabling brooms.");
    }

    /**
     * Apply an enchantment to an Item entity in the world.
     * <p>
     * This convenience method applies an enchantment to a dropped Item entity by delegating to
     * {@link #addEnchantedItem(ItemStack, ItemEnchantmentType, int, String, String)} using the
     * item's unique ID as the enchantment ID.
     * </p>
     *
     * @param item      the item entity to enchant
     * @param eType     the type of enchantment to apply
     * @param magnitude the power level of the enchantment (1+)
     * @param args      optional enchantment-specific arguments (null if not needed)
     */
    public void addEnchantedItem(@NotNull Item item, @NotNull ItemEnchantmentType eType, int magnitude, @Nullable String args) {
        ItemMeta itemMeta = item.getItemStack().getItemMeta();
        if (itemMeta == null)
            return;

        String eid = item.getUniqueId().toString();

        addEnchantedItem(item.getItemStack(), eType, magnitude, eid, args);
    }

    /**
     * Apply an enchantment to an ItemStack and register it in the enchantment cache.
     * <p>
     * This method:
     * <ul>
     * <li>Stores enchantment metadata (type, magnitude, ID, args) as NBT tags in the item's metadata</li>
     * <li>Creates an Enchantment object from the enchantment type and caches it by ID</li>
     * <li>Adds custom lore to the item if the enchantment has associated text</li>
     * </ul>
     * </p>
     * <p>
     * The enchantment ID uniquely identifies this enchanted item instance and is used for efficient
     * lookup during event handling. It should typically be a UUID or unique identifier.
     * </p>
     *
     * @param itemStack the ItemStack to enchant
     * @param eType     the type of enchantment to apply
     * @param magnitude the power level of the enchantment (1+)
     * @param eid       the unique identifier for this enchantment instance
     * @param args      optional enchantment-specific arguments (null if not needed)
     */
    public void addEnchantedItem(@NotNull ItemStack itemStack, @NotNull ItemEnchantmentType eType, int magnitude, @NotNull String eid, @Nullable String args) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return;

        // add NBT tags
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        container.set(enchantmentID, PersistentDataType.STRING, eid);
        container.set(enchantmentMagnitude, PersistentDataType.INTEGER, magnitude);
        container.set(enchantmentType, PersistentDataType.STRING, eType.toString());
        if (args != null)
            container.set(enchantmentArgs, PersistentDataType.STRING, args);

        itemStack.setItemMeta(itemMeta);

        Enchantment enchantment = getEnchantment(itemStack);
        if (enchantment == null)
            return;

        // optional lore for the item such as for a broom or floo powder
        if (enchantment.lore != null) {
            itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                List<String> lore = itemMeta.getLore();
                if (lore == null) {
                    lore = new ArrayList<>();
                }
                lore.add(enchantment.lore);
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);
            }
        }

        // store these in a hashmap for faster access later
        enchantedItems.put(eid, enchantment);

        common.printDebugMessage("Added enchanted item " + itemStack.getType() + " of type " + eType.getName(), null, null, false);
    }

    /**
     * Check if an Item entity has an enchantment applied.
     *
     * @param item the item entity to check
     * @return true if the item has enchantment NBT tags, false otherwise
     */
    public boolean isEnchanted(@NotNull Item item) {
        return isEnchanted(item.getItemStack());
    }

    /**
     * Check if an ItemStack has an enchantment applied.
     * <p>
     * Examines the item's NBT metadata to determine if any enchantment has been applied.
     * This is a quick check that doesn't require creating an Enchantment object.
     * </p>
     *
     * @param itemStack the ItemStack to check
     * @return true if the ItemStack has enchantment NBT tags, false otherwise
     */
    public boolean isEnchanted(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return false;

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        return container.has(enchantmentType, PersistentDataType.STRING);
    }

    /**
     * Check if an Item entity is cursed.
     * <p>
     * A cursed item has an enchantment that is marked as a curse (harmful magical effect).
     * </p>
     *
     * @param item the item entity to check
     * @return true if the item is cursed, false otherwise
     */
    public boolean isCursed(@NotNull Item item) {
        return isCursed(item.getItemStack());
    }

    /**
     * Check if an ItemStack is cursed.
     * <p>
     * A cursed item has an enchantment that is marked as a curse (harmful magical effect).
     * Examples: GEMINO (duplication), FLAGRANTE (burning).
     * </p>
     *
     * @param itemStack the ItemStack to check
     * @return true if the ItemStack is cursed, false otherwise
     */
    public boolean isCursed(@NotNull ItemStack itemStack) {
        if (!isEnchanted(itemStack))
            return false;

        ItemEnchantmentType enchantmentType = getEnchantmentType(itemStack);

        if (enchantmentType == null)
            return false;

        return enchantmentType.isCursed();
    }

    /**
     * Check if an Item is cursed and if the curse can be detected by the given magic level.
     * <p>
     * This method determines not only if an item is cursed, but also whether the detecting spell or
     * person is powerful enough to recognize the curse. Curses that are too high-level will not be
     * detected by lower-level detection spells.
     * </p>
     * <p>
     * Detection succeeds if: curse_level &lt;= detection_level + 1
     * </p>
     *
     * @param item  the item to check for curses
     * @param level the magic level attempting to detect the curse
     * @return true if the item is cursed AND the curse level is detectable by the given level, false otherwise
     */
    public boolean isCursedLevelBased(@NotNull Item item, @NotNull MagicLevel level) {
        // if an item is not cursed, always return false regardless of level
        if (!isCursed(item))
            return false;

        ItemEnchantmentType enchantmentType = getEnchantmentType(item.getItemStack());
        if (enchantmentType == null) // this should never happen because isCursed would have been false, this is to make the linter happy
            return false;

        // Can they detect this curse? Return true if curse level is not too high
        return enchantmentType.getLevel().ordinal() <= (level.ordinal() + 1);
    }

    /**
     * Get the enchantment type string from an ItemStack's NBT tags.
     * <p>
     * Returns the string name of the enchantment type (e.g., "GEMINO", "VOLATUS", "PORTUS") without
     * parsing it into an enum. Useful for quick checks or for storing the type.
     * </p>
     *
     * @param itemStack the ItemStack to retrieve the enchantment type from
     * @return the enchantment type string if present, null if item is not enchanted
     */
    @Nullable
    public String getEnchantmentTypeKey(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return null;

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        return container.has(enchantmentType, PersistentDataType.STRING) ?
            container.get(enchantmentType, PersistentDataType.STRING) : null;
    }

    /**
     * Get the enchantment type enum from an ItemStack.
     * <p>
     * Retrieves and parses the enchantment type from the item's NBT tags, returning the corresponding
     * {@link ItemEnchantmentType} enum value. Returns null if the item is not enchanted or if the
     * stored type string is invalid.
     * </p>
     *
     * @param itemStack the ItemStack to retrieve the enchantment type from
     * @return the ItemEnchantmentType enum if present and valid, null otherwise
     */
    @Nullable
    public ItemEnchantmentType getEnchantmentType(@NotNull ItemStack itemStack) {
        String enchantmentTypeStr = getEnchantmentTypeKey(itemStack);
        if (enchantmentTypeStr == null)
            return null;

        ItemEnchantmentType enchantmentType = null;
        try {
            enchantmentType = ItemEnchantmentType.valueOf(enchantmentTypeStr);
        }
        catch (Exception e) {
            common.printDebugMessage("Invalid item enchantment " + enchantmentTypeStr, null, null, true);
        }

        return enchantmentType;
    }

    /**
     * Get the unique ID of an enchantment from an Item entity.
     * <p>
     * This convenience method retrieves the enchantment ID from a dropped Item entity by delegating to
     * {@link #getEnchantmentID(ItemStack)}.
     * </p>
     *
     * @param item the item entity to retrieve the enchantment ID from
     * @return the unique enchantment ID if present, null if item is not enchanted
     */
    @Nullable
    public String getEnchantmentID(@NotNull Item item) {
        return getEnchantmentID(item.getItemStack());
    }

    /**
     * Get the unique ID of an enchantment from an ItemStack.
     * <p>
     * Each enchanted item has a unique ID that is used to look up the cached Enchantment object.
     * This ID is typically a UUID string that uniquely identifies this specific enchantment instance.
     * </p>
     *
     * @param itemStack the ItemStack to retrieve the enchantment ID from
     * @return the unique enchantment ID if present, null if item is not enchanted
     */
    @Nullable
    public String getEnchantmentID(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return null;

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        return container.has(enchantmentID, PersistentDataType.STRING) ?
            container.get(enchantmentID, PersistentDataType.STRING) : null;
    }

    /**
     * Get the enchantment magnitude from an ItemStack.
     * <p>
     * Magnitude represents the power level of the enchantment (1+ for valid enchantments).
     * </p>
     *
     * @param itemStack the ItemStack to retrieve the enchantment magnitude from
     * @return the magnitude (1+) if the item is enchanted, null otherwise
     */
    @Nullable
    public Integer getEnchantmentMagnitude(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return null;

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (container.has(enchantmentMagnitude, PersistentDataType.INTEGER))
            return container.get(enchantmentMagnitude, PersistentDataType.INTEGER);
        else
            return null;
    }

    /**
     * Get the optional arguments for an enchantment from an ItemStack.
     * <p>
     * Enchantments can have optional configuration arguments that customize their behavior.
     * For example, PORTUS portkey enchantments use args to store the destination coordinates.
     * </p>
     *
     * @param itemStack the ItemStack to retrieve the enchantment arguments from
     * @return the enchantment arguments string if present, null if item is not enchanted or has no args
     */
    @Nullable
    public String getEnchantmentArgs(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return null;

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        return container.has(enchantmentArgs, PersistentDataType.STRING) ?
            container.get(enchantmentArgs, PersistentDataType.STRING) : null;
    }

    /**
     * Get the Enchantment object for an ItemStack.
     * <p>
     * Looks up the Enchantment in the cache by ID, or creates a new one from NBT tags if not cached.
     * This method is the primary way to retrieve an Enchantment object that can handle item events.
     * </p>
     * <p>
     * Creation from NBT is used when an enchanted item is first loaded (e.g., from a player's inventory
     * on login), allowing enchantments to be persistent across server restarts.
     * </p>
     *
     * @param itemStack the ItemStack to retrieve the enchantment from
     * @return the Enchantment object if the item is enchanted, null if not enchanted or invalid
     */
    @Nullable
    public Enchantment getEnchantment(@NotNull ItemStack itemStack) {
        if (!isEnchanted(itemStack))
            return null;

        // get the enchantmentID for this enchantment
        String eid = getEnchantmentID(itemStack);

        if (eid == null)
            return null;

        // look to see if this enchantment is in our lookup map
        if (enchantedItems.containsKey(eid))
            return enchantedItems.get(eid);

        // create an enchantment from the NBT tags - this would happen when an item is loaded in to the game the first time
        Integer magnitude = getEnchantmentMagnitude(itemStack);
        String eType = getEnchantmentTypeKey(itemStack);
        String args = getEnchantmentArgs(itemStack);

        if (magnitude == null || eType == null) {
            common.printDebugMessage("Null enchantment NBT tags", null, null, false);
            return null;
        }

        Enchantment enchantment = createEnchantment(eType, magnitude, args);
        enchantedItems.put(eid, enchantment);

        return enchantment;
    }

    /**
     * Create an Enchantment object from type, magnitude, and arguments.
     * <p>
     * Uses reflection to instantiate the correct Enchantment subclass for the given type.
     * Returns null if the type is invalid, magnitude is less than 1, or the subclass cannot
     * be instantiated.
     * </p>
     * <p>
     * This is typically called when an enchanted item is loaded from NBT data for the first time,
     * or when creating enchantments on-the-fly.
     * </p>
     *
     * @param eType     the enchantment type string (e.g., "GEMINO", "VOLATUS")
     * @param magnitude the power level of the enchantment (must be 1 or greater)
     * @param args      optional enchantment-specific configuration arguments
     * @return the created Enchantment object if valid, null if type is invalid or instantiation fails
     */
    @Nullable
    public Enchantment createEnchantment(@NotNull String eType, @NotNull Integer magnitude, @Nullable String args) {
        if (magnitude < 1)
            return null;

        ItemEnchantmentType enchantmentType;

        try {
            enchantmentType = ItemEnchantmentType.valueOf(eType);
        }
        catch (Exception e) {
            common.printDebugMessage("Malformed enchantment type", null, null, false);
            return null;
        }

        Enchantment enchantment = null;
        Class<?> enchantmentClass = enchantmentType.getClassName();

        try {
            enchantment = (Enchantment) enchantmentClass.getConstructor(Ollivanders2.class, int.class, String.class, String.class).newInstance(p, magnitude, args, enchantmentType.getLore());
        }
        catch (Exception e) {
            common.printDebugMessage("Failed to create enchantment", e, null, true);
        }

        return enchantment;
    }

    /**
     * Remove an enchantment from an Item entity.
     * <p>
     * This convenience method removes the enchantment from an Item entity by delegating to
     * {@link #removeEnchantment(ItemStack)}. Also removes the enchantment from the cache.
     * </p>
     *
     * @param enchantedItem the enchanted item entity to remove the enchantment from
     * @return the ItemStack with the enchantment removed
     */
    @NotNull
    public ItemStack removeEnchantment(@NotNull Item enchantedItem) {
        return removeEnchantment(enchantedItem.getItemStack());
    }

    /**
     * Remove an enchantment from an ItemStack completely.
     * <p>
     * This method:
     * <ul>
     * <li>Removes the Enchantment object from the cache</li>
     * <li>Removes all enchantment NBT tags from the item's metadata</li>
     * </ul>
     * </p>
     * <p>
     * Use this to completely disenchant an item. For simply removing NBT tags without touching the
     * cache, use {@link #removeEnchantmentNBT(ItemStack)} instead.
     * </p>
     *
     * @param enchantedItemStack the ItemStack to remove the enchantment from
     * @return the ItemStack with the enchantment removed
     */
    @NotNull
    public ItemStack removeEnchantment(@NotNull ItemStack enchantedItemStack) {
        if (!isEnchanted(enchantedItemStack)) {
            common.printDebugMessage("EnchantedItems.removeEnchantment: itemStack not enchanted", null, null, false);
            return enchantedItemStack;
        }

        String eid = getEnchantmentID(enchantedItemStack);
        if (eid != null)
            enchantedItems.remove(eid);
        else {
            common.printDebugMessage("EnchantedItems.removeEnchantment: eid is null", null, null, true);
        }

        // return the disenchanted enchantedItemStack
        return removeEnchantmentNBT(enchantedItemStack);
    }

    /**
     * Remove enchantment NBT tags from an ItemStack without affecting the cache.
     * <p>
     * This method strips all enchantment metadata from an item without removing the Enchantment object
     * from the cache. Use this for creating unenchanted copies of enchanted items, such as GEMINO
     * duplicates that should not trigger further duplication.
     * </p>
     * <p>
     * For complete enchantment removal (both cache and NBT), use {@link #removeEnchantment(ItemStack)} instead.
     * </p>
     *
     * @param enchantedItemStack the ItemStack with enchantment NBT tags to remove
     * @return a new ItemStack with all enchantment NBT tags removed
     */
    public static ItemStack removeEnchantmentNBT(ItemStack enchantedItemStack) {
        Ollivanders2API.common.printDebugMessage("EnchantedItem.removeEnchantmentNBT: cleaning NBT tags", null, null, false);
        ItemStack disenchantedItemStack = enchantedItemStack.clone();

        // remove the enchantment NBT tags
        ItemMeta itemMeta = disenchantedItemStack.getItemMeta();
        if (itemMeta == null) {
            return enchantedItemStack;
        }

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        container.remove(enchantmentType);
        container.remove(enchantmentMagnitude);
        container.remove(enchantmentID);
        container.remove(enchantmentArgs);

        // set the enchantedItemStack meta on the disenchanted enchantedItemStack
        disenchantedItemStack.setItemMeta(itemMeta);

        // return the disenchanted enchantedItemStack
        return disenchantedItemStack;
    }

    /**
     * Handle item despawn events for enchanted items.
     * <p>
     * When an enchanted item is about to despawn (due to inactivity timeout), this handler
     * prevents the despawn by cancelling the event and setting unlimited lifetime.
     * Enchanted items are too valuable to be lost to despawning.
     * </p>
     *
     * @param event the item despawn event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemDespawn(@NotNull ItemDespawnEvent event) {
        Item item = event.getEntity();

        ItemStack itemStack = item.getItemStack();

        if (!isEnchanted(itemStack))
            return;

        Enchantment enchantment = getEnchantment(itemStack);
        if (enchantment == null)
            return;

        enchantment.doItemDespawn(event);
    }

    /**
     * Handle entity pickup events for enchanted items.
     * <p>
     * When a player or entity picks up an enchanted item, this handler retrieves the corresponding
     * Enchantment object and delegates to its {@link Enchantment#doEntityPickupItem(EntityPickupItemEvent)}
     * method to trigger the enchantment's pickup effect.
     * </p>
     *
     * @param event the entity item pickup event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityPickupItem(@NotNull EntityPickupItemEvent event) {
        Item item = event.getItem();
        ItemStack itemStack = item.getItemStack();

        if (!isEnchanted(itemStack))
            return;

        Enchantment enchantment = getEnchantment(itemStack);
        if (enchantment == null)
            return;

        enchantment.doEntityPickupItem(event);
    }

    /**
     * Handle inventory pickup events for enchanted items.
     * <p>
     * When a hopper or other block-based inventory attempts to pick up an enchanted item, this handler
     * retrieves the Enchantment and delegates to its {@link Enchantment#doInventoryPickupItem(InventoryPickupItemEvent)}
     * method. Most enchantments prevent hoppers from picking up items to protect them from automated systems.
     * </p>
     *
     * @param event the inventory item pickup event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryItemPickup(@NotNull InventoryPickupItemEvent event) {
        Item item = event.getItem();
        ItemStack itemStack = item.getItemStack();

        if (!isEnchanted(itemStack))
            return;

        Enchantment enchantment = getEnchantment(itemStack);
        if (enchantment == null)
            return;

        enchantment.doInventoryPickupItem(event);
    }

    /**
     * Handle item drop events for enchanted items.
     * <p>
     * When a player drops an enchanted item, this handler retrieves the Enchantment and delegates to
     * its {@link Enchantment#doItemDrop(PlayerDropItemEvent)} method to trigger any drop-related effects.
     * </p>
     *
     * @param event the player item drop event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemDrop(@NotNull PlayerDropItemEvent event) {
        Item item = event.getItemDrop();
        ItemStack itemStack = item.getItemStack();

        if (!isEnchanted(itemStack))
            return;

        Enchantment enchantment = getEnchantment(itemStack);
        if (enchantment == null)
            return;

        enchantment.doItemDrop(event);
    }

    /**
     * Handle item held events (hotbar slot changes) for enchanted items.
     * <p>
     * When a player changes which item slot is active in their hotbar, this handler checks both
     * the newly held item and the previously held item. If either contains an enchantment, the
     * Enchantment's {@link Enchantment#doItemHeld(PlayerItemHeldEvent)} method is called to trigger
     * any hold-related effects (e.g., toggling broom flight).
     * </p>
     *
     * @param event the player item held event (slot change event)
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemHeld(@NotNull PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        // check newly held item
        ItemStack heldItem = player.getInventory().getItem(event.getNewSlot());

        if (heldItem == null || !isEnchanted(heldItem)) {
            // check previously held item
            heldItem = player.getInventory().getItem(event.getPreviousSlot());

            if (heldItem == null || !isEnchanted(heldItem))
                return;
        }

        Enchantment enchantment = getEnchantment(heldItem);
        if (enchantment == null)
            return;

        enchantment.doItemHeld(event);
    }

    /**
     * Check if broomstick enchantments are enabled in the plugin configuration.
     * <p>
     * Broomstick items (using VOLATUS enchantments) are optional and can be disabled via the
     * plugin config. This method provides a quick way to check if they are enabled before
     * triggering broom-related effects.
     * </p>
     *
     * @return true if broomstick enchantments are enabled, false otherwise
     */
    public static boolean areBroomsEnabled() {
        return enableBrooms;
    }
}
