package net.pottercraft.ollivanders2.divination;

import org.jetbrains.annotations.NotNull;

/**
 * Enumeration of all available divination methods in Ollivanders2.
 * <p>
 * This enum maps each divination type to its corresponding implementation class. The divination methods vary in
 * accuracy, with centaur divination being the most reliable (80% max accuracy) and tasseomancy being the least (20% max accuracy).
 * Each divination method defines its own prophecy prefixes and behavior through subclasses of {@link O2Divination}.
 * </p>
 * <p>
 * Implemented divination types:
 * </p>
 * <ul>
 * <li><strong>ASTROLOGY</strong> - Celestial-based divination (20% max accuracy)</li>
 * <li><strong>CARTOMANCY</strong> - Card divination with spades focus (25% max accuracy)</li>
 * <li><strong>CARTOMANCY_TAROT</strong> - Major arcana tarot reading (35% max accuracy)</li>
 * <li><strong>CENTAUR_DIVINATION</strong> - Celestial observation and augury (80% max accuracy)</li>
 * <li><strong>CRYSTAL_BALL</strong> - Scrying and clairvoyant vision (30% max accuracy)</li>
 * <li><strong>OVOMANCY</strong> - Egg pattern interpretation (40% max accuracy)</li>
 * <li><strong>TASSEOMANCY</strong> - Tea-leaf reading (20% max accuracy)</li>
 * </ul>
 * <p>
 * Additional divination methods are defined in comments for future implementation:
 * BIBLIOMANCY, CATOPTROMANCY, CHINESE_FORTUNE_STICKS, DREAM_INTERPRETATION, FIRE_OMEN,
 * HEPTOMOLOGY, ICHTHYOMANCY, MYOMANCY, PALMISTRY, ORNITHOMANCY, RUNE_STONES, XYLOMANCY
 * </p>
 *
 * @author Azami7
 * @see O2Divination for the abstract base class of divination implementations
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
