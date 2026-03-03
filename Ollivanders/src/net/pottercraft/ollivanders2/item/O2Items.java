package net.pottercraft.ollivanders2.item;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.item.enchantment.EnchantedItems;
import net.pottercraft.ollivanders2.item.wand.O2Wands;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

/**
 * Manager for all custom items in Ollivanders2.
 *
 * <p>O2Items is responsible for managing all custom items in the Ollivanders2 plugin, including
 * basic items, enchanted items, and wands. It provides methods to retrieve items by type, name,
 * or name prefix, manage item metadata, and handle custom crafting recipes. The manager also
 * coordinates with the EnchantedItems system for item enchantment functionality.</p>
 *
 * @author Azami7
 */
public class O2Items {
    /**
     * A reference to the plugin
     */
    final private Ollivanders2 p;

    /**
     * A map of all items in the game by type
     */
    final private HashMap<O2ItemType, O2Item> O2ItemMap = new HashMap<>();

    /**
     * A list of the recipe namespacedKeys
     */
    final private HashMap<O2ItemType, NamespacedKey> recipeKeys = new HashMap<>();

    /**
     * The enchanted items manager - handles all enchantments on items
     */
    public EnchantedItems enchantedItems;

    /**
     * The wand manager - wands being complex items with special logic
     */
    static O2Wands wands;

    /**
     * Namespace key for NTB flags
     */
    public static NamespacedKey o2ItemTypeKey;

    /**
     * Constructor
     *
     * @param plugin a reference to the plugin
     */
    public O2Items(@NotNull Ollivanders2 plugin) {
        p = plugin;
        wands = new O2Wands(p);

        enchantedItems = new EnchantedItems(p);
        p.getServer().getPluginManager().registerEvents(enchantedItems, p);

        o2ItemTypeKey = new NamespacedKey(p, "o2item_type");
    }

    /**
     * Load all items and enchantments
     */
    public void onEnable() {
        Ollivanders2API.common.printLogMessage("Adding custom items", null, null, false);

        for (O2ItemType itemType : O2ItemType.values()) {
            O2Item item = new O2Item(p, itemType);
            O2ItemMap.put(item.getType(), item);
        }

        enchantedItems.onEnable();

        // add custom recipes
        addCustomRecipes();
    }

    /**
     * Add custom item recipes
     */
    private void addCustomRecipes() {
        Ollivanders2API.common.printLogMessage("Adding custom item recipes", null, null, false);

        //
        // BASIC_BROOM
        //
        ItemStack broom = O2ItemType.BASIC_BROOM.getItem(1);
        if (broom != null) {
            NamespacedKey broomKey = new NamespacedKey(p, "basicBroomRecipe_key");
            ShapedRecipe broomRecipe = new ShapedRecipe(broomKey, broom);
            broomRecipe.shape("**S", "*S*", "H**"); // shape must be set before ingredients are set
            broomRecipe.setIngredient('S', Material.STICK);
            broomRecipe.setIngredient('H', Material.HAY_BLOCK);
            p.getServer().addRecipe(broomRecipe);
            recipeKeys.put(O2ItemType.BASIC_BROOM, broomKey);
        }
        else {
            Ollivanders2API.common.printLogMessage("O2Items.addCustomRecipes: BASIC_BROOM.getItem() returned null, recipe not added", null, null, true);
        }

        //
        // FLOO_POWDER
        //
        ItemStack flooPower = O2ItemType.FLOO_POWDER.getItem(1);
        if (flooPower != null) {
            NamespacedKey flooKey = new NamespacedKey(p, "flooPowderRecipe_key");
            ShapedRecipe flooRecipe = new ShapedRecipe(flooKey, flooPower);
            flooRecipe.shape("*O*", "ORO", "*O*"); // shape must be set before ingredients are set
            flooRecipe.setIngredient('R', Material.REDSTONE);
            flooRecipe.setIngredient('O', Material.OBSIDIAN);
            p.getServer().addRecipe(flooRecipe);
            recipeKeys.put(O2ItemType.FLOO_POWDER, flooKey);
        }
        else {
            Ollivanders2API.common.printLogMessage("O2Items.addCustomRecipes: FLOO_POWDER.getItem() returned null, recipe not added", null, null, true);
        }
    }

    /**
     * Cleanup when the plugin disables.
     *
     * <p>Called when the Ollivanders2 plugin is being shut down. The O2Items manager performs
     * no cleanup on disable because item data is read-only and loaded from configuration on
     * startup. No persistent state needs to be saved.</p>
     */
    public void onDisable() {
    }

    /**
     * Get an item by type
     *
     * @param itemType the type of item to get
     * @param amount   the amount of the item to get
     * @return an item stack of the item and amount, null if item not found
     */
    @Nullable
    public ItemStack getItemByType(@NotNull O2ItemType itemType, int amount) {
        if (amount < 1) {
            return null;
        }

        O2Item o2item = O2ItemMap.get(itemType);

        return o2item.getItem(amount);
    }

    /**
     * Get an item by name
     *
     * @param name   the name of the item
     * @param amount the amount of the item to get
     * @return an item stack of the item and amount, null if item not found
     */
    @Nullable
    public ItemStack getItemByName(@NotNull String name, int amount) {
        O2ItemType itemType = getItemTypeByName(name);
        if (itemType == null)
            return null;

        return getItemByType(itemType, amount);
    }

    /**
     * Get an item that name starts with a substring
     *
     * @param name   the substring of the name starts with
     * @param amount the amount of the item to get
     * @return an item stack of the item and amount, null if item not found
     */
    @Nullable
    public ItemStack getItemByNameStartsWith(@NotNull String name, int amount) {
        O2ItemType itemType = getItemTypeByNameStartsWith(name);

        if (itemType == null)
            return null;

        return getItemByType(itemType, amount);
    }

    /**
     * Get item type by display name - set in the metadata of the item
     *
     * @param name the display name for the item
     * @return the item type if found, null otherwise
     */
    @Nullable
    public O2ItemType getItemTypeByName(@NotNull String name) {
        O2ItemType itemType = null;

        for (O2Item item : O2ItemMap.values()) {
            if (item.getName().equalsIgnoreCase(name)) {
                itemType = item.getType();
                break;
            }
        }

        return itemType;
    }

    /**
     * Get the name of an item by type.
     *
     * @param itemType the item type
     * @return the name of this item or null if not found
     */
    @Nullable
    public String getItemNameByType(@NotNull O2ItemType itemType) {
        return O2ItemMap.get(itemType).getName();
    }

    /**
     * Get an item where the name starts with this string
     *
     * @param name the substring of the name starts with
     * @return the item type if found, null otherwise
     */
    @Nullable
    public O2ItemType getItemTypeByNameStartsWith(@NotNull String name) {
        for (O2Item item : O2ItemMap.values()) {
            if (item.getName().toLowerCase().startsWith(name.trim().toLowerCase()))
                return item.getType();
        }

        return null;
    }

    /**
     * Get the material type for an item type
     *
     * @param itemType the item type to get the material for
     * @return the material type if found, null otherwise
     */
    @Nullable
    public Material getItemMaterialByType(@NotNull O2ItemType itemType) {
        return O2ItemMap.get(itemType).getMaterialType();
    }

    /**
     * Get an O2Item name from the NBT o2ItemTypeKey tag in the item metadata
     *
     * @param itemStack the item stack to check
     * @return the O2Item name if found, null otherwise
     */
    @Nullable
    public String getO2ItemNameFromItemStack(@NotNull ItemStack itemStack) {
        String name = null;
        ItemMeta meta = itemStack.getItemMeta();

        if (meta != null) {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            if (container.has(O2Items.o2ItemTypeKey, PersistentDataType.STRING))
                name = container.get(O2Items.o2ItemTypeKey, PersistentDataType.STRING);
        }

        return name;
    }

    /**
     * Get the names of all items.
     *
     * @return an array of the item names
     */
    @NotNull
    public ArrayList<String> getAllItemNames() {
        Set<O2ItemType> itemKeys = O2ItemMap.keySet();

        ArrayList<String> itemNames = new ArrayList<>();

        for (O2ItemType itemType : itemKeys) {
            itemNames.add(itemType.getName());
        }

        Collections.sort(itemNames);
        return itemNames;
    }

    /**
     * Get the O2ItemType of an Item
     *
     * @param item the item to check
     * @return the O2ItemType if it is an O2Item, null otherwise
     */
    @Nullable
    public O2ItemType getItemTypeByItem(@NotNull Item item) {
        return getItemTypeByItemStack(item.getItemStack());
    }

    /**
     * Get the O2ItemType of an Item
     *
     * @param itemStack the ItemStack to check
     * @return the O2ItemType if it is an O2Item, null otherwise
     */
    @Nullable
    public O2ItemType getItemTypeByItemStack(@NotNull ItemStack itemStack) {
        String itemName = getO2ItemNameFromItemStack(itemStack);
        if (itemName == null)
            return null;

        return O2ItemType.getTypeByName(itemName);
    }

    /**
     * Get wands management class
     *
     * @return wands
     */
    public O2Wands getWands() {
        return wands;
    }

    /**
     * Get a copy of the recipe keys map.
     *
     * <p>Returns a new HashMap containing all custom item recipe NamespacedKeys indexed by
     * their corresponding O2ItemType. The returned map is a copy, so modifications do not
     * affect the internal state.</p>
     *
     * @return a copy of the item type to recipe key mapping
     */
    public HashMap<O2ItemType, NamespacedKey> getRecipeKeys() {
        HashMap<O2ItemType, NamespacedKey> keys = new HashMap<>();
        keys.putAll(recipeKeys);

        return keys;
    }
}
