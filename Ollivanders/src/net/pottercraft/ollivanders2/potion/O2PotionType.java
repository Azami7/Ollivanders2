package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.common.MagicLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Enumeration of all potion types available in Ollivanders2.
 * <p>
 * Each potion type defines a distinct magical brew with its display name, implementation class,
 * and magic difficulty level. Potions are brewed by players using cauldrons and specific ingredients,
 * then consumed for various magical effects.
 * </p>
 * <p>
 * Magic levels range from BEGINNER (simple potions like Cure for Boils) to NEWT (advanced potions
 * like Draught of Living Death).
 * </p>
 *
 * @author Azami7
 * @see O2Potion the abstract base class for all potion implementations
 */
public enum O2PotionType {
    /**
     * {@link ANIMAGUS_POTION}
     */
    ANIMAGUS_POTION(ANIMAGUS_POTION.class, "Animagus Potion", MagicLevel.NEWT),
    /**
     * {@link BABBLING_BEVERAGE}
     */
    BABBLING_BEVERAGE(BABBLING_BEVERAGE.class, "Babbling Beverage", MagicLevel.OWL),
    /**
     * {@link BARUFFIOS_BRAIN_ELIXIR}
     */
    BARUFFIOS_BRAIN_ELIXIR(BARUFFIOS_BRAIN_ELIXIR.class, "Baruffio's Brain Elixir", MagicLevel.EXPERT),
    /**
     * {@link COMMON_ANTIDOTE_POTION}
     */
    COMMON_ANTIDOTE_POTION(COMMON_ANTIDOTE_POTION.class, "Antidote to Common Poisons", MagicLevel.BEGINNER),
    /**
     * {@link CURE_FOR_BOILS}
     */
    CURE_FOR_BOILS(CURE_FOR_BOILS.class, "Cure for Boils", MagicLevel.BEGINNER),
    /**
     * {@link DRAUGHT_OF_LIVING_DEATH}
     */
    DRAUGHT_OF_LIVING_DEATH(DRAUGHT_OF_LIVING_DEATH.class, "Draught of Living Death", MagicLevel.NEWT),
    /**
     * {@link FORGETFULNESS_POTION}
     */
    FORGETFULNESS_POTION(FORGETFULNESS_POTION.class, "Forgetfulness Potion", MagicLevel.OWL),
    /**
     * {@link HERBICIDE_POTION}
     */
    HERBICIDE_POTION(HERBICIDE_POTION.class, "Herbicide Potion", MagicLevel.BEGINNER),
    /**
     * {@link HUNGER_POTION}
     */
    HUNGER_POTION(HUNGER_POTION.class, "Hunger Potion", MagicLevel.BEGINNER),
    /**
     * {@link ICE_POTION}
     */
    ICE_POTION(ICE_POTION.class, "Ice Potion", MagicLevel.OWL),
    /**
     * {@link MEMORY_POTION}
     */
    MEMORY_POTION(MEMORY_POTION.class, "Memory Potion", MagicLevel.NEWT),
    /**
     * {@link OCULUS_FELIS}
     */
    OCULUS_FELIS(OCULUS_FELIS.class, "Oculus Felis", MagicLevel.BEGINNER),
    /**
     * {@link REGENERATION_POTION}
     */
    REGENERATION_POTION(REGENERATION_POTION.class, "Regeneration Potion", MagicLevel.NEWT),
    /**
     * {@link SATIATION_POTION}
     */
    SATIATION_POTION(SATIATION_POTION.class, "Satiation Potion", MagicLevel.BEGINNER),
    /**
     * {@link SHRINKING_SOLUTION}
     */
    SHRINKING_SOLUTION(SHRINKING_SOLUTION.class, "Shrinking Solution", MagicLevel.OWL),
    /**
     * {@link SLEEPING_DRAUGHT}
     */
    SLEEPING_DRAUGHT(SLEEPING_DRAUGHT.class, "Sleeping Draught", MagicLevel.BEGINNER),
    /**
     * {@link STRENGTHENING_SOLUTION}
     */
    STRENGTHENING_SOLUTION(STRENGTHENING_SOLUTION.class, "Strengthening Solution", MagicLevel.OWL),
    /**
     * {@link SWELLING_SOLUTION}
     */
    SWELLING_SOLUTION(SWELLING_SOLUTION.class, "Swelling Solution", MagicLevel.OWL),
    /**
     * {@link WEAKNESS_POTION}
     */
    WEAKNESS_POTION(WEAKNESS_POTION.class, "Weakness Potion", MagicLevel.BEGINNER),
    /**
     * {@link WIDEYE_POTION}
     */
    WIDEYE_POTION(WIDEYE_POTION.class, "Wideye Potion", MagicLevel.BEGINNER),
    /**
     * {@link WIGGENWELD_POTION}
     */
    WIGGENWELD_POTION(WIGGENWELD_POTION.class, "Wiggenweld Potion", MagicLevel.OWL),
    /**
     * {@link WIT_SHARPENING_POTION}
     */
    WIT_SHARPENING_POTION(WIT_SHARPENING_POTION.class, "Wit-Sharpening Potion", MagicLevel.OWL),
    /**
     * {@link WOLFSBANE_POTION}
     */
    WOLFSBANE_POTION(WOLFSBANE_POTION.class, "Wolfsbane Potion", MagicLevel.EXPERT);

    /**
     * The implementation class for this potion type.
     * <p>
     * Used with reflection to instantiate potion objects. Must extend O2Potion.
     * </p>
     */
    private final Class<?> className;

    /**
     * The magic difficulty level required to brew this potion.
     */
    private final MagicLevel level;

    /**
     * The human-readable display name of this potion (e.g., "Baruffio's Brain Elixir").
     */
    private final String name;

    /**
     * Constructor for creating a potion type enum constant.
     *
     * @param className the implementation class for this potion type (must extend O2Potion)
     * @param name      the display name of the potion (e.g., "Baruffio's Brain Elixir")
     * @param level     the magic difficulty level required to brew this potion
     */
    O2PotionType(@NotNull Class<?> className, @NotNull String name, @NotNull MagicLevel level) {
        this.className = className;
        this.name = name;
        this.level = level;
    }

    /**
     * Get the implementation class for this potion type.
     *
     * @return the Class object for this potion's implementation (extends O2Potion)
     */
    @NotNull
    public Class<?> getClassName() {
        return className;
    }

    /**
     * Get the magic level of this potion
     *
     * @return the magic level
     */
    @NotNull
    public MagicLevel getLevel() {
        return level;
    }

    /**
     * Get the display name for this potion type.
     *
     * @return the human-readable potion name (e.g., "Baruffio's Brain Elixir")
     */
    @NotNull
    public String getPotionName() {
        return name;
    }

    /**
     * Get an O2PotionType enum from its enum name string.
     * <p>
     * This should be used as the opposite of toString() on the enum. This is distinctly different from
     * {@link #getPotionTypeByName(String)} which gets the potion type by the display name, which may
     * not match enum.toString() (e.g., "Baruffio's Brain Elixir" vs "BARUFFIOS_BRAIN_ELIXIR").
     * </p>
     *
     * @param potionString the enum name of the potion type (e.g., "SLEEPING_DRAUGHT", "WIGGENWELD_POTION")
     * @return the potion type if found, null if the string doesn't match any potion type
     */
    @Nullable
    public static O2PotionType getPotionTypeFromString(@NotNull String potionString) {
        O2PotionType potionType = null;

        try {
            potionType = O2PotionType.valueOf(potionString);
        }
        catch (Exception e) {
            // we don't do anything, this will happen if they send an invalid potion name
        }

        return potionType;
    }

    /**
     * Get a potion type by its display name.
     * <p>
     * This is distinctly different from {@link #getPotionTypeFromString(String)} which gets the potion
     * based on the enum.toString() value. Display names may include punctuation and spaces that aren't
     * valid in enum names (e.g., "Baruffio's Brain Elixir", "Wit-Sharpening Potion").
     * </p>
     * <p>
     * The comparison is case-insensitive.
     * </p>
     *
     * @param name the display name of the potion to find (e.g., "Sleeping Draught")
     * @return the potion type if found, null if no potion matches the given name
     */
    @Nullable
    public static O2PotionType getPotionTypeByName(@NotNull String name) {
        for (O2PotionType potionType : O2PotionType.values()) {
            if (potionType.getPotionName().equalsIgnoreCase(name))
                return potionType;
        }

        return null;
    }
}