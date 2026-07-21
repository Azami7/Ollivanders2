package net.pottercraft.ollivanders2.test.potion;

import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for the Wit-Sharpening Potion.
 *
 * @see PotionTestSuper
 */
public class WitSharpeningPotionTest extends PotionTestSuper {
    /**
     * Select the potion and effect under test.
     */
    @Override @BeforeEach
    void setUp() {
        potionType = O2PotionType.WIT_SHARPENING_POTION;
        effectType = O2EffectType.IMPROVED_BOOK_LEARNING;
    }
}
