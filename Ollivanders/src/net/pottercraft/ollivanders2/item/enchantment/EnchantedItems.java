package net.pottercraft.ollivanders2.item.enchantment;

import net.pottercraft.ollivanders2.Ollivanders2;
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
 * Enchantment effect on items.
 *
 * <p>Enchantments are used to give items magical properties like broom flight, port keys, and cursed items.</p>
 *
 *  <p>Reference: https://minecraft.fandom.com/wiki/Tutorials/Command_NBT_tags</p>
 */
public class EnchantedItems implements Listener {
    /**
     * Reference to the plugin
     */
    Ollivanders2 p;

    /**
     * Common functions
     */
    private final Ollivanders2Common common;

    /**
     * Namespace keys for NBT tags
     */
    static NamespacedKey enchantmentType;

    /**
     * Namespace keys for NBT tags
     */
    static NamespacedKey enchantmentMagnitude;

    /**
     * Namespace keys for NBT tags
     */
    static NamespacedKey enchantmentID;

    /**
     * Namespace keys for NBT tags
     */
    static NamespacedKey enchantmentArgs;

    /**
     * Lookup of known enchanted items to speed up handlers
     */
    HashMap<String, Enchantment> enchantedItems = new HashMap<>();

    /**
     * Enchantment related config
     */
    public static boolean enableBrooms;

    /**
     * Constructor
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
     * Initialization steps to run when the plugin is enabled
     */
    public void onEnable() {
        //
        // brooms
        //
        enableBrooms = p.getConfig().getBoolean("enableBrooms");
        if (enableBrooms)
            p.getLogger().info("Enabling brooms.");
    }

    /**
     * Add enchanted item
     *
     * @param item      the enchanted item
     * @param eType     the type of enchantment
     * @param magnitude the magnitude of enchantment
     * @param args      optional arguments for this enchantment
     */
    public void addEnchantedItem(@NotNull Item item, @NotNull ItemEnchantmentType eType, int magnitude, @NotNull String args) {
        ItemMeta itemMeta = item.getItemStack().getItemMeta();
        if (itemMeta == null)
            return;

        String eid = item.getUniqueId().toString();

        addEnchantedItem(item.getItemStack(), eType, magnitude, eid, args);
    }

    /**
     * Add enchanted item
     *
     * @param itemStack the enchanted item
     * @param eType     the type of enchantment
     * @param magnitude the magnitude of enchantment
     * @param eid       the uniqueID for this enchantment
     * @param args      optional arguments for this enchantment
     */
    public void addEnchantedItem(@NotNull ItemStack itemStack, @NotNull ItemEnchantmentType eType, int magnitude, @NotNull String eid, @NotNull String args) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return;

        // add NBT tags
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        container.set(enchantmentID, PersistentDataType.STRING, eid);
        container.set(enchantmentMagnitude, PersistentDataType.INTEGER, magnitude);
        container.set(enchantmentType, PersistentDataType.STRING, eType.toString());
        container.set(enchantmentArgs, PersistentDataType.STRING, args);

        itemStack.setItemMeta(itemMeta);

        Enchantment enchantment = getEnchantment(itemStack);
        if (enchantment == null)
            return;

        // optional lore for the item such as for a broom or floo powder
        if (enchantment.lore != null) {
            List<String> lore = itemMeta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            lore.add(enchantment.lore);
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
        }

        // store these in a hashmap for faster access later
        enchantedItems.put(eid, enchantment);

        common.printDebugMessage("Added enchanted item " + itemMeta.getDisplayName() + " of type " + eType.getName(), null, null, false);
    }

    /**
     * Determine if an item stack is enchanted.
     *
     * @param item the item to check
     * @return true if the item stack has the NBT tag, false otherwise
     */
    public boolean isEnchanted(@NotNull Item item) {
        return isEnchanted(item.getItemStack());
    }

    /**
     * Determine if an item stack is enchanted.
     *
     * @param itemStack the item to check
     * @return true if the item stack has the NBT tag, false otherwise
     */
    public boolean isEnchanted(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return false;

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        return container.has(enchantmentType, PersistentDataType.STRING);
    }

    /**
     * Is this item cursed
     *
     * @param item the item to check
     * @return true if it is cursed, false otherwise
     */
    public boolean isCursed(@NotNull Item item) {
        return isCursed(item.getItemStack());
    }

    /**
     * Is this item stack cursed
     *
     * @param itemStack the item stack to check
     * @return true if cursed, false otherwise
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
     * Is this item cursed - level based check. If the curse is too high a level, this check will return false even if
     * there is a curse.
     *
     * @param item  the item to check
     * @param level the level of the spell or person doing the checking
     * @return true if it is cursed and the level of the enchantment is <= level + 1, false otherwise
     */
    public boolean isCursedLevelBased(@NotNull Item item, @NotNull MagicLevel level) {
        boolean cursed = isCursed(item);

        if (cursed) {
            // it is cursed, but can they tell based on the level of the spell checking for it?
            ItemEnchantmentType enchantmentType = getEnchantmentType(item.getItemStack());

            if (enchantmentType == null)
                return false;

            // ignore npe warning, if it is cursed then enchantmentType cannot be null
            if (enchantmentType.getLevel().ordinal() <= (level.ordinal() + 1)) {
                return false;
            }
            else
                return true;
        }

        return false;
    }

    /**
     * Get the enchantment key for this item
     *
     * @param itemStack the item stack to get the key from
     * @return the key if present, null otherwise
     */
    @Nullable
    public String getEnchantmentTypeKey(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return null;

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (container.has(enchantmentType, PersistentDataType.STRING)) {
            return container.get(enchantmentType, PersistentDataType.STRING);
        }
        else
            return null;
    }

    /**
     * Get the enchantment type for this item
     *
     * @param itemStack the item stack to get the key from
     * @return the enchantment type if present, null otherwise
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
     * Get the enchantment id for this item
     *
     * @param itemStack the item stack to get the key from
     * @return the id if present, null otherwise
     */
    @Nullable
    public String getEnchantmentID(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return null;

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (container.has(enchantmentID, PersistentDataType.STRING))
            return container.get(enchantmentID, PersistentDataType.STRING);
        else
            return null;
    }

    /**
     * Get the enchantment magnitude for this item
     *
     * @param itemStack the item stack to get the key from
     * @return the id if present, -1 otherwise
     */
    public Integer getEnchantmentMagnitude(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return -1;

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (container.has(enchantmentMagnitude, PersistentDataType.INTEGER))
            return container.get(enchantmentMagnitude, PersistentDataType.INTEGER);
        else
            return -1;
    }

    /**
     * Get the enchantment args for this item
     *
     * @param itemStack the item stack to get the key from
     * @return the args if present, null otherwise
     */
    @Nullable
    public String getEnchantmentArgs(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return null;

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (container.has(enchantmentArgs, PersistentDataType.STRING)) {
            return container.get(enchantmentArgs, PersistentDataType.STRING);
        }
        else
            return null;
    }

    /**
     * Get the enchantment object for this enchanted item - this will either get the existing or create one
     *
     * @param itemStack the enchanted item
     * @return the Enchantment if enchantment NBT tags are present, null otherwise
     */
    @Nullable
    public Enchantment getEnchantment(@NotNull ItemStack itemStack) {
        if (!isEnchanted(itemStack))
            return null;

        String eid = getEnchantmentID(itemStack);

        if (eid == null)
            return null;

        // have we already loaded this one?
        if (enchantedItems.containsKey(eid))
            return enchantedItems.get(eid);

        Integer magnitude = getEnchantmentMagnitude(itemStack);
        String eType = getEnchantmentTypeKey(itemStack);
        String args = getEnchantmentArgs(itemStack);

        if (magnitude == null || eType == null) {
            common.printDebugMessage("Null enchantment NBT tags", null, null, false);
            return null;
        }

        return createEnchantment(eType, magnitude, args);
    }

    /**
     * Create the enchantment object for an enchanted item
     *
     * @param eType     the type of enchantment
     * @param magnitude the magnitude of the enchantment
     * @param args      optional arguments for the enchantment
     * @return the enchantment if type and magnitude were valid, null otherwise
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
     * Remove the enchantment from an item
     *
     * @param item the item to remove the enchantment from
     */
    public void removeEnchantment(@NotNull Item item) {
        if (!isEnchanted(item))
            return;

        ItemStack itemStack = item.getItemStack();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return;

        String eid = getEnchantmentID(itemStack);
        if (eid != null)
            enchantedItems.remove(eid);

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        container.remove(enchantmentType);
        container.remove(enchantmentMagnitude);
        container.remove(enchantmentID);
        container.remove(enchantmentArgs);
    }

    /**
     * Handle enchanted items despawning.
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
     * Handle when an enchanted item is picked up.
     *
     * @param event the item pickup event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemPickup(@NotNull EntityPickupItemEvent event) {
        Item item = event.getItem();
        ItemStack itemStack = item.getItemStack();

        if (!isEnchanted(itemStack))
            return;

        Enchantment enchantment = getEnchantment(itemStack);
        if (enchantment == null)
            return;

        enchantment.doItemPickup(event);
    }

    /**
     * Handle when an enchanted item is dropped.
     *
     * @param event the item drop event
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
     * Handle when an enchanted item is held.
     *
     * @param event the player item held event
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
}
