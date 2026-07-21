package net.pottercraft.ollivanders2.item.enchantment;

import net.pottercraft.ollivanders2.common.MagicLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The available item enchantments. Each type carries its display name, implementation class (instantiated via
 * reflection), optional item lore, {@link MagicLevel} (for counter-spell balancing), and whether it is a curse.
 *
 * @see Enchantment
 * @author Azami7
 */
public enum ItemEnchantmentType {
    /**
     * Concealment enchantment that hides text in a book.
     */
    CELATUM("celatum", CELATUM.class, null, MagicLevel.BEGINNER, false),
    /**
     * Curse that continuously burns a player holding the item.
     */
    FLAGRANTE("flagrante", FLAGRANTE.class, null, MagicLevel.NEWT, true),
    /**
     * Curse that duplicates the item, potentially overwhelming the player with copies.
     */
    GEMINO("gemino", GEMINO.class, null, MagicLevel.EXPERT, true),
    /**
     * Portkey enchantment that teleports the holder to a configured destination.
     */
    PORTUS("portus", PORTUS.class, null, MagicLevel.NEWT, false),
    /**
     * Broomstick enchantment that grants flight while the broom is held.
     */
    VOLATUS("volatus", VOLATUS.class, "Flying vehicle used by magical folk", MagicLevel.EXPERT, false),
    ;

    /**
     * The display name of this enchantment type, typically the lowercase constant name.
     */
    private final String name;

    /**
     * The {@link Enchantment} implementation class, instantiated via reflection with signature
     * {@code (Ollivanders2, int, String, String)}.
     */
    private final Class<?> className;

    /**
     * Optional lore appended to items with this enchantment; null for none.
     */
    private final String lore;

    /**
     * The magic level of this enchantment, used to balance counter-spells against it.
     */
    private final MagicLevel level;

    /**
     * Whether this enchantment is a curse (true) or a beneficial/neutral effect (false).
     */
    private final boolean cursed;

    /**
     * Constructor
     *
     * @param displayName the display name for this enchantment
     * @param cName       the {@link Enchantment} implementation class
     * @param itemLore    optional lore to add to enchanted items, or null
     * @param level       the magic level of this enchantment
     * @param cursed      true if this is a curse, false if beneficial
     */
    ItemEnchantmentType(@NotNull String displayName, @NotNull Class<?> cName, @Nullable String itemLore, @NotNull MagicLevel level, boolean cursed) {
        name = displayName;
        className = cName;
        lore = itemLore;
        this.level = level;
        this.cursed = cursed;
    }

    /**
     * Get the display name of this enchantment type.
     *
     * @return the display name (e.g., "volatus", "flagrante")
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Get the {@link Enchantment} implementation class, used to instantiate this enchantment via reflection.
     *
     * @return the implementation class
     */
    @NotNull
    public Class<?> getClassName() {
        return className;
    }

    /**
     * Get the optional lore appended to items with this enchantment.
     *
     * @return the lore text, or null if none is defined
     */
    @Nullable
    public String getLore() {
        return lore;
    }

    /**
     * Get the magic level of this enchantment.
     *
     * @return the magic level
     */
    @NotNull
    public MagicLevel getLevel() {
        return level;
    }

    /**
     * Check whether this enchantment is a curse or a beneficial effect.
     *
     * @return true if this is a curse, false if a beneficial or neutral enchantment
     */
    public boolean isCursed() {
        return cursed;
    }
}
