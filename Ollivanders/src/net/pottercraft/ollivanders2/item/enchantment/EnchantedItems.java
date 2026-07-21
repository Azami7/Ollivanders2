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
 * Manager for enchanted items. Applies enchantments to items, listens for the item events enchantments react to
 * (pickup, drop, despawn, held), and routes each event to the responsible {@link Enchantment}.
 * <p>
 * Enchantment metadata (type, magnitude, ID, args) is stored as NBT tags on the item so it persists across server
 * restarts and item operations. Live {@link Enchantment} objects are cached by ID and rebuilt from NBT the first time
 * an enchanted item is seen.
 * </p>
 *
 * @see Enchantment
 * @see ItemEnchantmentType
 */
public class EnchantedItems implements Listener {
    /**
     * A reference to the plugin.
     */
    private final Ollivanders2 p;

    /**
     * Common functions.
     */
    private final Ollivanders2Common common;

    /**
     * NBT key for the enchantment type, stored as the {@link ItemEnchantmentType} enum name (e.g. "GEMINO").
     */
    static public NamespacedKey enchantmentType;

    /**
     * NBT key for the enchantment magnitude, stored as an integer (1 or greater).
     */
    static public NamespacedKey enchantmentMagnitude;

    /**
     * NBT key for the enchantment ID, the unique key used to look up the enchantment in {@link #enchantedItems}.
     */
    static public NamespacedKey enchantmentID;

    /**
     * NBT key for optional enchantment-specific arguments (e.g. PORTUS destination coordinates).
     */
    static public NamespacedKey enchantmentArgs;

    /**
     * Live enchantment objects indexed by ID, so event handling need not rebuild them on every item interaction.
     */
    private final HashMap<String, Enchantment> enchantedItems = new HashMap<>();

    /**
     * Whether broomstick items are enabled in the plugin configuration.
     */
    private static boolean enableBrooms;

    /**
     * Constructor. Creates the {@link NamespacedKey}s used to store enchantment metadata under the plugin's namespace.
     *
     * @param plugin a callback to the plugin
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
     * Load enchantment configuration (such as whether brooms are enabled) from the plugin config. Called once at
     * plugin startup.
     */
    public void onEnable() {
        enableBrooms = p.getConfig().getBoolean("enableBrooms");
        if (enableBrooms)
            p.getLogger().info("Enabling brooms.");
    }

    /**
     * Enchant a dropped item entity, using the entity's unique ID as the enchantment ID.
     *
     * @param item      the item entity to enchant
     * @param eType     the type of enchantment to apply
     * @param magnitude the power level of the enchantment; 1 or greater
     * @param args      optional enchantment-specific arguments
     */
    public void addEnchantedItem(@NotNull Item item, @NotNull ItemEnchantmentType eType, int magnitude, @Nullable String args) {
        ItemMeta itemMeta = item.getItemStack().getItemMeta();
        if (itemMeta == null)
            return;

        String eid = item.getUniqueId().toString();

        addEnchantedItem(item.getItemStack(), eType, magnitude, eid, args);
    }

    /**
     * Enchant an item stack: writes the enchantment NBT tags, caches a live {@link Enchantment} by ID, and appends the
     * enchantment's lore to the item if it has any. Mutates the given item stack's metadata.
     *
     * @param itemStack the item stack to enchant
     * @param eType     the type of enchantment to apply
     * @param magnitude the power level of the enchantment; 1 or greater
     * @param eid       the unique ID for this enchantment instance, used as the cache key
     * @param args      optional enchantment-specific arguments
     */
    public void addEnchantedItem(@NotNull ItemStack itemStack, @NotNull ItemEnchantmentType eType, int magnitude, @NotNull String eid, @Nullable String args) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return;

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

        // append the enchantment's lore, if any, such as for a broom or floo powder
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

        enchantedItems.put(eid, enchantment);

        common.printDebugMessage("Added enchanted item " + itemStack.getType() + " of type " + eType.getName(), null, null, false);
    }

    /**
     * Check whether an item entity has an enchantment applied.
     *
     * @param item the item entity to check
     * @return true if the item has enchantment NBT tags
     */
    public boolean isEnchanted(@NotNull Item item) {
        return isEnchanted(item.getItemStack());
    }

    /**
     * Check whether an item stack has an enchantment applied. Reads NBT only; does not build an {@link Enchantment}.
     *
     * @param itemStack the item stack to check
     * @return true if the item stack has enchantment NBT tags
     */
    public boolean isEnchanted(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return false;

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        return container.has(enchantmentType, PersistentDataType.STRING);
    }

    /**
     * Check whether an item entity carries a curse enchantment.
     *
     * @param item the item entity to check
     * @return true if the item is cursed
     */
    public boolean isCursed(@NotNull Item item) {
        return isCursed(item.getItemStack());
    }

    /**
     * Check whether an item stack carries a curse enchantment, such as GEMINO (duplication) or FLAGRANTE (burning).
     *
     * @param itemStack the item stack to check
     * @return true if the item stack is cursed
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
     * Check whether an item is cursed and the curse is weak enough for the given magic level to detect it. A
     * detector one level below the curse still detects it; anything lower does not.
     *
     * @param item  the item to check for curses
     * @param level the magic level attempting to detect the curse
     * @return true if the item is cursed and its curse level is at most one above the given level
     */
    public boolean isCursedLevelBased(@NotNull Item item, @NotNull MagicLevel level) {
        if (!isCursed(item)) {
            common.printDebugMessage("EnchantedItems.isCursedLevelBased: item is not cursed", null, null, false);
            return false;
        }

        common.printDebugMessage("EnchantedItems.isCursedLevelBased: item is cursed", null, null, false);

        ItemEnchantmentType enchantmentType = getEnchantmentType(item.getItemStack());
        if (enchantmentType == null) // this should never happen because isCursed would have been false, this is to make the linter happy
            return false;

        common.printDebugMessage("curse level is " + enchantmentType.getLevel() + ", detection level is " + level, null, null, false);
        if (enchantmentType.getLevel().ordinal() <= (level.ordinal() + 1)) {
            common.printDebugMessage("EnchantedItems.isCursedLevelBased: curse can be detected", null, null, false);
            return true;
        }
        common.printDebugMessage("EnchantedItems.isCursedLevelBased: curse cannot be detected", null, null, false);
        return false;
    }

    /**
     * Get the raw enchantment type name from an item stack's NBT, without parsing it to an enum.
     *
     * @param itemStack the item stack to read
     * @return the enchantment type name (e.g. "GEMINO"), or null if the item is not enchanted
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
     * Get the enchantment type from an item stack's NBT, parsed to its enum value.
     *
     * @param itemStack the item stack to read
     * @return the enchantment type, or null if the item is not enchanted or the stored name is invalid
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
     * Get the unique enchantment ID from an item entity's NBT.
     *
     * @param item the item entity to read
     * @return the enchantment ID, or null if the item is not enchanted
     */
    @Nullable
    public String getEnchantmentID(@NotNull Item item) {
        return getEnchantmentID(item.getItemStack());
    }

    /**
     * Get the unique enchantment ID from an item stack's NBT. This is the key used to look up the cached
     * {@link Enchantment}.
     *
     * @param itemStack the item stack to read
     * @return the enchantment ID, or null if the item is not enchanted
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
     * Get the enchantment magnitude (power level, 1 or greater) from an item stack's NBT.
     *
     * @param itemStack the item stack to read
     * @return the magnitude, or null if the item is not enchanted
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
     * Get the optional enchantment arguments from an item stack's NBT (e.g. PORTUS destination coordinates).
     *
     * @param itemStack the item stack to read
     * @return the arguments string, or null if the item is not enchanted or has no arguments
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
     * Get the live {@link Enchantment} for an item stack, from the cache if present or rebuilt from NBT and cached
     * otherwise. Rebuilding covers items first seen after a restart (e.g. loaded from a player's inventory on login).
     *
     * @param itemStack the item stack to read
     * @return the enchantment, or null if the item is not enchanted or its NBT is invalid
     */
    @Nullable
    public Enchantment getEnchantment(@NotNull ItemStack itemStack) {
        if (!isEnchanted(itemStack))
            return null;

        String eid = getEnchantmentID(itemStack);

        if (eid == null)
            return null;

        if (enchantedItems.containsKey(eid))
            return enchantedItems.get(eid);

        // not cached: rebuild from NBT, as happens the first time an item is loaded into the game
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
     * Create an {@link Enchantment}, reflectively instantiating the subclass for the given type name.
     *
     * @param eType     the enchantment type name (e.g. "GEMINO")
     * @param magnitude the power level of the enchantment; must be 1 or greater or null is returned
     * @param args      optional enchantment-specific arguments
     * @return the new enchantment, or null if the magnitude is below 1, the type name is invalid, or instantiation fails
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
     * Fully disenchant an item entity: drops it from the cache and strips its NBT tags.
     *
     * @param enchantedItem the enchanted item entity
     * @return the disenchanted item stack
     */
    @NotNull
    public ItemStack removeEnchantment(@NotNull Item enchantedItem) {
        return removeEnchantment(enchantedItem.getItemStack());
    }

    /**
     * Fully disenchant an item stack: drops the {@link Enchantment} from the cache and strips its NBT tags. To strip
     * NBT only, leaving the cache intact, use {@link #removeEnchantmentNBT(ItemStack)}.
     *
     * @param enchantedItemStack the item stack to disenchant
     * @return the disenchanted item stack
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

        return removeEnchantmentNBT(enchantedItemStack);
    }

    /**
     * Strip enchantment NBT tags from a copy of the given item stack, leaving the cache untouched. Used to make
     * unenchanted copies, such as GEMINO duplicates that must not themselves duplicate. For full removal (cache and
     * NBT), use {@link #removeEnchantment(ItemStack)}.
     *
     * @param enchantedItemStack the enchanted item stack; not mutated
     * @return a new, disenchanted copy of the item stack, or the original if it has no metadata
     */
    public static ItemStack removeEnchantmentNBT(ItemStack enchantedItemStack) {
        Ollivanders2API.common.printDebugMessage("EnchantedItem.removeEnchantmentNBT: cleaning NBT tags", null, null, false);
        ItemStack disenchantedItemStack = enchantedItemStack.clone();

        ItemMeta itemMeta = disenchantedItemStack.getItemMeta();
        if (itemMeta == null) {
            return enchantedItemStack;
        }

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        container.remove(enchantmentType);
        container.remove(enchantmentMagnitude);
        container.remove(enchantmentID);
        container.remove(enchantmentArgs);

        disenchantedItemStack.setItemMeta(itemMeta);

        return disenchantedItemStack;
    }

    /**
     * Route an item despawn to the item's {@link Enchantment}; ignores unenchanted items.
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
     * Route an entity item pickup to the item's {@link Enchantment}; ignores unenchanted items.
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
     * Route a block-inventory (e.g. hopper) item pickup to the item's {@link Enchantment}; ignores unenchanted items.
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
     * Route a player item drop to the item's {@link Enchantment}; ignores unenchanted items.
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
     * Route a hotbar slot change to the enchantment on the newly or previously held item (e.g. to toggle broom
     * flight); ignores unenchanted items.
     *
     * @param event the player item held event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemHeld(@NotNull PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        ItemStack heldItem = player.getInventory().getItem(event.getNewSlot());

        if (heldItem == null || !isEnchanted(heldItem)) {
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
     * Check whether broomstick (VOLATUS) enchantments are enabled in the plugin configuration.
     *
     * @return true if brooms are enabled
     */
    public static boolean areBroomsEnabled() {
        return enableBrooms;
    }
}
