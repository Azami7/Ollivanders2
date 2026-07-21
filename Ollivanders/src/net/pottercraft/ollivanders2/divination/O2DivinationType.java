package net.pottercraft.ollivanders2.divination;

import org.jetbrains.annotations.NotNull;

/**
 * The available divination methods, each mapped to its {@link O2Divination} implementation class. Commented-out entries
 * mark methods reserved for future implementation.
 *
 * @author Azami7
 * @see O2Divination
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
     * {@link TASSOMANCY}
     */
    TASSOMANCY(TASSOMANCY.class),
    //XYLOMANCY,
    ;

    final private Class<?> className;

    /**
     * Constructor
     *
     * @param c the implementation class for this divination type
     */
    O2DivinationType(@NotNull Class<?> c) {
        className = c;
    }

    /**
     * Get the implementation class for this divination type.
     *
     * @return the divination's implementation class
     */
    @NotNull
    public Class<?> getClassName() {
        return className;
    }
}
