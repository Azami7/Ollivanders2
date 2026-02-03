package net.pottercraft.ollivanders2.item.enchantment;

import net.pottercraft.ollivanders2.common.MagicLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Enumeration of all available item enchantments in Ollivanders2.
 * <p>
 * Each enchantment type defines a distinct magical effect that can be applied to items. Enchantments are
 * characterized by their display name, implementation class, optional item lore, magic level (for counter-spell
 * balancing), and whether they are curses or beneficial effects.
 * </p>
 * <p>
 * Enchantments are dynamically instantiated via reflection using the {@link #className} field. The {@link #level}
 * field indicates the difficulty/power of the enchantment and is used to balance counter-spells against them.
 * Cursed enchantments ({@link #isCursed()}) represent negative effects, while non-cursed enchantments are
 * beneficial or neutral magical effects.
 * </p>
 *
 * @see Enchantment abstract base class for all enchantment implementations
 * @author Azami7
 */
public enum ItemEnchantmentType {
    /**
     * Concealment enchantment that hides text in a book.
     * <p>
     * {@link CELATUM} is a beginner-level beneficial enchantment that hides text in a book.
     * </p>
     */
    CELATUM("celatum", CELATUM.class, null, MagicLevel.BEGINNER, false),
    /**
     * Flagrante curse that burns whoever touches it.
     * <p>
     * {@link FLAGRANTE} is an expert-level curse that applies continuous burning damage to players
     * holding flagrante-cursed items. Cursed items cannot be picked up by block inventories.
     * </p>
     */
    FLAGRANTE("flagrante", FLAGRANTE.class, null, MagicLevel.EXPERT, true),
    /**
     * Duplication curse that spawns duplicates of items.
     * <p>
     * {@link GEMINO} is an expert-level curse that duplicates items when certain conditions are met,
     * potentially filling containers and overwhelming the player with copies.
     * </p>
     */
    GEMINO("gemino", GEMINO.class, null, MagicLevel.EXPERT, true),
    /**
     * Portkey enchantment that teleports the holder to a destination.
     * <p>
     * {@link PORTUS} is a NEWT-level beneficial enchantment that allows a player holding the item to
     * be teleported to a configured destination point.
     * </p>
     */
    PORTUS("portus", PORTUS.class, null, MagicLevel.NEWT, false),
    /**
     * Flight enchantment that enables broom-based flight.
     * <p>
     * {@link VOLATUS} is an expert-level beneficial enchantment applied to broomsticks that grants the
     * player flight capability while holding the broom. Broomsticks with this enchantment persist in
     * the world and cannot be picked up by hoppers.
     * </p>
     */
    VOLATUS("volatus", VOLATUS.class, "Flying vehicle used by magical folk", MagicLevel.EXPERT, false),
    ;

    /**
     * The display name of this enchantment type.
     * <p>
     * Used for identifying and displaying the enchantment to players. This value is typically lowercase
     * and corresponds to the enum constant name in lowercase form.
     * </p>
     */
    private final String name;

    /**
     * The concrete implementation class for this enchantment type.
     * <p>
     * Used with reflection to instantiate enchantment objects dynamically. The class must extend
     * {@link Enchantment} and have a constructor matching the signature:
     * {@code (Ollivanders2, int, String, String)}.
     * </p>
     */
    private final Class<?> className;

    /**
     * Optional descriptive lore to append to items with this enchantment.
     * <p>
     * If not null, this lore text is added to the enchanted item's display lore. Provides flavor text
     * or additional context for the player about the enchanted item.
     * </p>
     */
    private final String lore;

    /**
     * The magic difficulty level of this enchantment.
     * <p>
     * Indicates the power and complexity of the enchantment on the {@link MagicLevel} scale (BEGINNER,
     * NEWT, EXPERT, etc.). Used to balance counter-spells and other magical interactions against this
     * enchantment.
     * </p>
     */
    private final MagicLevel level;

    /**
     * Whether this enchantment represents a curse or beneficial effect.
     * <p>
     * True for curses (negative effects like FLAGRANTE, GEMINO), false for beneficial or neutral
     * enchantments (CELATUM, PORTUS, VOLATUS). May affect how counter-spells interact with the
     * enchanted item.
     * </p>
     */
    private final boolean cursed;

    /**
     * Private constructor for creating enchantment type enum constants.
     * <p>
     * Initializes an enchantment type with all its properties. This constructor is only called when
     * defining the enum constants above.
     * </p>
     *
     * @param displayName the display name for this enchantment (typically lowercase)
     * @param cName       the concrete implementation class (must extend Enchantment)
     * @param itemLore    optional descriptive lore to add to enchanted items, or null
     * @param level       the magic difficulty level ({@link MagicLevel})
     * @param cursed      true if this is a curse, false if a beneficial enchantment
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
     * Get the concrete implementation class for this enchantment.
     * <p>
     * The returned class is used by reflection to instantiate enchantment objects with the
     * signature {@code (Ollivanders2, int, String, String)}.
     * </p>
     *
     * @return the Class object for the enchantment implementation
     */
    @NotNull
    public Class<?> getClassName() {
        return className;
    }

    /**
     * Get the optional descriptive lore for this enchantment.
     * <p>
     * If present, this lore text is appended to the display lore of enchanted items.
     * </p>
     *
     * @return the lore text, or null if no lore is defined
     */
    @Nullable
    public String getLore() {
        return lore;
    }

    /**
     * Get the magic difficulty level of this enchantment.
     * <p>
     * Used to balance counter-spells and magical interactions against this enchantment.
     * </p>
     *
     * @return the MagicLevel (BEGINNER, NEWT, EXPERT, etc.)
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
