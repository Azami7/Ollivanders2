package net.pottercraft.ollivanders2.common;

/**
 * Difficulty tiers for spells, effects, enchantments, and potions, used to gate access by player progression.
 *
 * @author Azami7
 */
public enum MagicLevel {
    /**
     * Entry-level magic (years 1-2 equivalent).
     */
    BEGINNER,
    /**
     * Intermediate magic (years 3-4 equivalent).
     */
    OWL,
    /**
     * Advanced magic (years 5-7 equivalent).
     */
    NEWT,
    /**
     * Expert magic, the most powerful and restrictive tier; may be inaccessible to regular players by configuration.
     */
    EXPERT
}
