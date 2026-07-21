package net.pottercraft.ollivanders2.test.potion;

import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for the Strengthening Solution potion.
 *
 * @see PotionTestSuper
 */
public class StrengtheningSolutionTest extends PotionTestSuper {
    /**
     * Select the potion and effect under test.
     */
    @Override @BeforeEach
    void setUp() {
        potionType = O2PotionType.STRENGTHENING_SOLUTION;
        effectType = O2EffectType.STRENGTH;
    }
}
