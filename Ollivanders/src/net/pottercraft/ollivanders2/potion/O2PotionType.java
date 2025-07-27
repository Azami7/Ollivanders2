package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.common.MagicLevel;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * All potions types
 *
 * @author Azami7
 */
public enum O2PotionType {
    /**
     * {@link ANIMAGUS_POTION}
     */
    ANIMAGUS_POTION(ANIMAGUS_POTION.class, MagicLevel.NEWT),
    /**
     * {@link BABBLING_BEVERAGE}
     */
    BABBLING_BEVERAGE(BABBLING_BEVERAGE.class, MagicLevel.OWL),
    /**
     * {@link BARUFFIOS_BRAIN_ELIXIR}
     */
    BARUFFIOS_BRAIN_ELIXIR(BARUFFIOS_BRAIN_ELIXIR.class, MagicLevel.EXPERT),
    /**
     * {@link COMMON_ANTIDOTE_POTION}
     */
    COMMON_ANTIDOTE_POTION(COMMON_ANTIDOTE_POTION.class, MagicLevel.BEGINNER),
    /**
     * {@link CURE_FOR_BOILS}
     */
    CURE_FOR_BOILS(CURE_FOR_BOILS.class, MagicLevel.BEGINNER),
    /**
     * {@link DRAUGHT_OF_LIVING_DEATH}
     */
    DRAUGHT_OF_LIVING_DEATH(DRAUGHT_OF_LIVING_DEATH.class, MagicLevel.NEWT),
    /**
     * {@link FORGETFULLNESS_POTION}
     */
    FORGETFULLNESS_POTION(FORGETFULLNESS_POTION.class, MagicLevel.OWL),
    /**
     * {@link HERBICIDE_POTION}
     */
    HERBICIDE_POTION(HERBICIDE_POTION.class, MagicLevel.BEGINNER),
    /**
     * {@link ICE_POTION}
     */
    ICE_POTION(ICE_POTION.class, MagicLevel.OWL),
    /**
     * {@link MEMORY_POTION}
     */
    MEMORY_POTION(MEMORY_POTION.class, MagicLevel.NEWT),
    /**
     * {@link OCULUS_FELIS}
     */
    OCULUS_FELIS(OCULUS_FELIS.class, MagicLevel.BEGINNER),
    /**
     * {@link REGENERATION_POTION}
     */
    REGENERATION_POTION(REGENERATION_POTION.class, MagicLevel.NEWT),
    /**
     * {@link SLEEPING_DRAUGHT}
     */
    SLEEPING_DRAUGHT(SLEEPING_DRAUGHT.class, MagicLevel.BEGINNER),
    /**
     * {@link STRENGTHENING_SOLUTION}
     */
    STRENGTHENING_SOLUTION(STRENGTHENING_SOLUTION.class, MagicLevel.OWL),
    /**
     * {@link WEAKNESS_POTION}
     */
    WEAKNESS_POTION(WEAKNESS_POTION.class, MagicLevel.BEGINNER),
    /**
     * {@link WIDEYE_POTION}
     */
    WIDEYE_POTION(WIDEYE_POTION.class, MagicLevel.BEGINNER),
    /**
     * {@link WIGGENWELD_POTION}
     */
    WIGGENWELD_POTION(WIGGENWELD_POTION.class, MagicLevel.OWL),
    /**
     * {@link WIT_SHARPENING_POTION}
     */
    WIT_SHARPENING_POTION(WIT_SHARPENING_POTION.class, MagicLevel.OWL),
    /**
     * {@link WOLFSBANE_POTION}
     */
    WOLFSBANE_POTION(WOLFSBANE_POTION.class, MagicLevel.EXPERT);

    /**
     * The class of the potion this type creates
     */
    private final Class<?> className;

    /**
     * The level of this potion
     */
    private final MagicLevel level;

    /**
     * Constructor.
     *
     * @param className the class for this potion type
     * @param level     the level of this potion
     */
    O2PotionType(@NotNull Class<?> className, MagicLevel level) {
        this.className = className;
        this.level = level;
    }

    /**
     * Get the class for this potion type
     *
     * @return the class name for this type of potion.
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
     * Get the name for this potion type.
     *
     * @return the spell name for this potion type.
     */
    @NotNull
    public String getPotionName() {
        String potionTypeString = this.toString().toLowerCase();

        return Ollivanders2Common.firstLetterCapitalize(potionTypeString.replace("_", " "));
    }

    /**
     * Get a O2PotionType enum from a string. This should be used as the opposite of toString() on the enum.
     *
     * @param potionString the name of the potion type, ex. "AQUA_ERUCTO"
     * @return the potion type
     */
    @Nullable
    public static O2PotionType potionTypeFromString(@NotNull String potionString) {
        O2PotionType potionType = null;

        try {
            potionType = O2PotionType.valueOf(potionString);
        }
        catch (Exception e) {
            // we don't do anything, this will happen if they send an invalid potion name
        }

        return potionType;
    }
}