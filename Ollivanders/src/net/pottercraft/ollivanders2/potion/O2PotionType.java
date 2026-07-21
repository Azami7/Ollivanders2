package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.common.MagicLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The available potion types. Each carries its {@link O2Potion} implementation class (instantiated via reflection), a
 * display name, and a {@link MagicLevel} indicating brewing difficulty.
 *
 * @author Azami7
 * @see O2Potion
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
     * The {@link O2Potion} implementation class for this potion type, instantiated via reflection.
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
     * Constructor
     *
     * @param className the {@link O2Potion} implementation class for this potion type
     * @param name      the display name of the potion
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
     * Get a potion type from its enum name (the inverse of {@code toString()}). Use {@link #getPotionTypeByName(String)}
     * to look up by display name instead, since display names (e.g. "Baruffio's Brain Elixir") differ from enum names.
     *
     * @param potionString the enum name of the potion type, e.g. "SLEEPING_DRAUGHT"
     * @return the potion type, or null if the string matches no potion type
     */
    @Nullable
    public static O2PotionType getPotionTypeFromString(@NotNull String potionString) {
        O2PotionType potionType = null;

        try {
            potionType = O2PotionType.valueOf(potionString);
        }
        catch (Exception e) {
            // invalid potion name; leave potionType null
        }

        return potionType;
    }

    /**
     * Get a potion type by its display name, case-insensitively. Unlike {@link #getPotionTypeFromString(String)}, this
     * matches the human-readable name (which may contain spaces and punctuation not valid in an enum name).
     *
     * @param name the display name of the potion, e.g. "Sleeping Draught"
     * @return the potion type, or null if no potion matches the name
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