package net.pottercraft.ollivanders2.test.potion;

import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for the Swelling Solution potion.
 *
 * @see PotionTestSuper
 */
public class SwellingSolutionTest extends PotionTestSuper {
    @Override @BeforeEach
    void setUp() {
        potionType = O2PotionType.SWELLING_SOLUTION;
        effectType = O2EffectType.SWELLING;
    }
}
