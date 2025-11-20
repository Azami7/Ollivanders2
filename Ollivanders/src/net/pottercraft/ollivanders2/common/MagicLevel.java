package net.pottercraft.ollivanders2.common;

/**
 * Difficulty progression levels for spells, effects, enchantments, and potions.
 * <p>
 * Represents a four-tier difficulty system ranging from beginner-friendly magic to expert-only magic.
 * Used to gate access to spells and potions based on player progression and to balance gameplay difficulty.
 * </p>
 *
 * @author Azami7
 */
public enum MagicLevel {
    /**
     * Entry-level magic for beginners (years 1-2 equivalent).
     * The easiest and most basic magic available to new players.
     */
    BEGINNER,
    /**
     * Intermediate magic for intermediate practitioners (years 3-4 equivalent).
     * More complex and powerful than beginner magic, requiring some skill and experience.
     */
    OWL,
    /**
     * Advanced magic for skilled practitioners (years 5-7 equivalent).
     * Powerful magic requiring significant skill and experience to perform safely.
     */
    NEWT,
    /**
     * Expert-level magic for advanced magicians.
     * The most powerful and restrictive magic, typically requiring special training or achievement.
     * May be inaccessible to regular players depending on configuration.
     */
    EXPERT;
}
