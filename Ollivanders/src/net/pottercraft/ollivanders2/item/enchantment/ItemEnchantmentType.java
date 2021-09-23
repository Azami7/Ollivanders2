package net.pottercraft.ollivanders2.item.enchantment;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Types of item enchantments
 *
 * @author Azami7
 */
public enum ItemEnchantmentType
{
    FLAGRANTE ("flagrante", FLAGRANTE.class, null, Ollivanders2Common.MagicLevel.EXPERT),
    GEMINIO ("geminio", GEMINIO.class, null, Ollivanders2Common.MagicLevel.EXPERT),
    VOLATUS ("volatus", VOLATUS.class, "Flying vehicle used by magical folk", Ollivanders2Common.MagicLevel.EXPERT),
    ;

    /**
     * The name of this enchantment for display purposes
     */
    private final String name;

    /**
     * The class, for reflection
     */
    private final Class<?> className;

    /**
     * Optional lore to add to the enchanted item
     */
    private final String lore;

    /**
     * The level of this enchantment, for use with counter-spells
     */
    private final Ollivanders2Common.MagicLevel level;

    /**
     * Constructor.
     *
     * @param displayName the name for this enchantment
     * @param cName the class for the enchantment
     * @param itemLore the lore for the enchanted item, can be null
     * @param level the level of this enchantment
     */
    ItemEnchantmentType(@NotNull String displayName, @NotNull Class<?> cName, @Nullable String itemLore, @NotNull Ollivanders2Common.MagicLevel level)
    {
        name = displayName;
        className = cName;
        lore = itemLore;
        this.level = level;
    }

    /**
     * Get the display name
     *
     * @return the display name
     */
    @NotNull
    public String getName()
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
    public String getLore()
    {
        return lore;
    }

    /**
     * Get the level of this enchantment
     *
     * @return the level of this enchantment
     */
    @NotNull
    public Ollivanders2Common.MagicLevel getLevel()
    {
        return level;
    }
}
