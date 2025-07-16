package net.pottercraft.ollivanders2.divination;

import org.jetbrains.annotations.NotNull;

/**
 * All divination methods
 *
 * @author Azami7
 * @since 2.2.9
 */
public enum O2DivinationType {
    /**
     * {@link ASTROLOGY}
     */
    ASTROLOGY(net.pottercraft.ollivanders2.divination.ASTROLOGY.class),
    //BIBLIOMANCY,
    /**
     * {@link CARTOMANCY}
     */
    CARTOMANCY(net.pottercraft.ollivanders2.divination.CARTOMANCY.class),
    /**
     * {@link CARTOMANCY_TAROT}
     */
    CARTOMANCY_TAROT(net.pottercraft.ollivanders2.divination.CARTOMANCY_TAROT.class),
    //CATOPTROMANCY,
    /**
     * {@link CENTAUR_DIVINATION}
     */
    CENTAUR_DIVINATION(net.pottercraft.ollivanders2.divination.CENTAUR_DIVINATION.class),
    //CHINESE_FORTUNE_STICKS,
    /**
     * {@link CRYSTAL_BALL}
     */
    CRYSTAL_BALL(net.pottercraft.ollivanders2.divination.CRYSTAL_BALL.class),
    //DREAM_INTERPRETATION,
    //FIRE_OMEN,
    //HEPTOMOLOGY,
    //ICHTHYOMANCY,
    //MYOMANCY,
    //PALMISTRY,
    //ORNITHOMANCY,
    /**
     * {@link OVOMANCY}
     */
    OVOMANCY(net.pottercraft.ollivanders2.divination.OVOMANCY.class),
    //RUNE_STONES,
    /**
     * {@link TASSEOMANCY}
     */
    TASSEOMANCY(TASSEOMANCY.class),
    //XYLOMANCY,
    ;

    final private Class<?> className;

    /**
     * constructor
     *
     * @param c the classname for this divination type
     */
    O2DivinationType(@NotNull Class<?> c) {
        className = c;
    }

    /**
     * get the classname for this divination type
     *
     * @return the class for this divination
     */
    @NotNull
    public Class<?> getClassName() {
        return className;
    }
}
