package net.pottercraft.ollivanders2.item.enchantment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Types of item enchantments
 *
 * @author Azami7
 */
public enum ItemEnchantmentType
{
    FLAGRANTE ("flagrante", FLAGRANTE.class, null),
    GEMINIO ("geminio", GEMINIO.class, null),
    VOLATUS ("volatus", VOLATUS.class, "Flying vehicle used by magical folk"),
    ;

    /**
     * The name of this enchantment for display purposes
     */
    final String name;

    /**
     * The class, for reflection
     */
    final Class<?> className;

    /**
     * Optional lore to add to the enchanted item
     */
    final String lore;

    /**
     * Constructor.
     *
     * @param displayName the name for this enchantment
     * @param cName the class for the enchantment
     * @param itemLore the lore for the enchanted item, can be null
     */
    ItemEnchantmentType (@NotNull String displayName, @NotNull Class<?> cName, @Nullable String itemLore)
    {
        name = displayName;
        className = cName;
        lore = itemLore;
    }

    /**
     * Get the display name
     *
     * @return the display name
     */
    @NotNull
    public String getName ()
    {
        return name;
    }

    /**
     * Get the class
     *
     * @return the class
     */
    @NotNull
    public Class<?> getClassName()
    {
        return className;
    }

    /**
     * Get the lore
     *
     * @return the lore
     */
    @Nullable
    public String getLore ()
    {
        return lore;
    }
}
