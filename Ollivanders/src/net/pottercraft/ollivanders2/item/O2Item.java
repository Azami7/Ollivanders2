package net.pottercraft.ollivanders2.item;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Items for Ollivanders
 *
 * @author Azami7
 */
public class O2Item {
    /**
     * Reference to the plugin
     */
    final protected Ollivanders2 p;

    /**
     * The type this item is
     */
    final private O2ItemType itemType;

    /**
     * Constructor
     *
     * @param plugin reference to the plugin
     * @param type   the type this item is
     */
    public O2Item(@NotNull Ollivanders2 plugin, @NotNull O2ItemType type) {
        p = plugin;
        itemType = type;
    }

    /**
     * Get an ItemStack of this item type
     *
     * @param amount the amount of items in the stack
     * @return an ItemStack of this item
     */
    @Nullable
    public ItemStack getItem(int amount) {
        Ollivanders2Common common = new Ollivanders2Common(p);
        common.printDebugMessage("Getting item " + itemType.getName(), null, null, false);

        return itemType.getItem(amount);
    }

    /**
     * @return the O2ItemType
     */
    @NotNull
    public O2ItemType getType() {
        return itemType;
    }

    /**
     * @return the item name
     */
    @NotNull
    public String getName() {
        return itemType.getName();
    }

    /**
     * @return the material type
     */
    @NotNull
    public Material getMaterialType() {
        return itemType.getMaterial();
    }

    /**
     * Get the item type for this item stack
     *
     * @param itemStack the item stack to check
     * @return the O2ItemType, if it is one, or null otherwise
     */
    @Nullable
    static public O2ItemType getItemType(@NotNull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            return null;

        return O2ItemType.getTypeByName(meta.getDisplayName());
    }
}
