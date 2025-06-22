package net.pottercraft.ollivanders2.item.enchantment;

import net.pottercraft.ollivanders2.common.MagicLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Types of item enchantments
 *
 * @author Azami7
 */
public enum ItemEnchantmentType {
    /**
     * {@link FLAGRANTE}
     */
    FLAGRANTE("flagrante", FLAGRANTE.class, null, MagicLevel.EXPERT, true),
    /**
     * {@link GEMINIO}
     */
    GEMINIO("geminio", GEMINIO.class, null, MagicLevel.EXPERT, true),
    /**
     * {@link PORTUS}
     */
    PORTUS("portus", PORTUS.class, null, MagicLevel.NEWT, false),
    /**
     * {@link VOLATUS}
     */
    VOLATUS("volatus", VOLATUS.class, "Flying vehicle used by magical folk", MagicLevel.EXPERT, false),
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
    private final MagicLevel level;

    /**
     * Whether this enchantment type is a curse or not
     */
    private final boolean cursed;

    /**
     * Constructor.
     *
     * @param displayName the name for this enchantment
     * @param cName       the class for the enchantment
     * @param itemLore    the lore for the enchanted item, can be null
     * @param level       the level of this enchantment
     * @param cursed      whether this enchantment type is a curse
     */
    ItemEnchantmentType(@NotNull String displayName, @NotNull Class<?> cName, @Nullable String itemLore, @NotNull MagicLevel level, boolean cursed) {
        name = displayName;
        className = cName;
        lore = itemLore;
        this.level = level;
        this.cursed = cursed;
    }

    /**
     * Get the display name
     *
     * @return the display name
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Get the class
     *
     * @return the class
     */
    @NotNull
    public Class<?> getClassName() {
        return className;
    }

    /**
     * Get the lore
     *
     * @return the lore
     */
    @Nullable
    public String getLore() {
        return lore;
    }

    /**
     * Get the level of this enchantment
     *
     * @return the level of this enchantment
     */
    @NotNull
    public MagicLevel getLevel() {
        return level;
    }

    /**
     * Is this enchantment a curse or not
     *
     * @return true if a curse, false otherwise
     */
    public boolean isCursed() {
        return cursed;
    }
}
