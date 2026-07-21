package net.pottercraft.ollivanders2.test.potion;

import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for the CURE_FOR_BOILS potion.
 *
 * @see PotionTestSuper
 */
public class CureForBoilsTest extends PotionTestSuper {
    @Override @BeforeEach
    void setUp() {
        potionType = O2PotionType.CURE_FOR_BOILS;
        effectType = O2EffectType.HEAL;
    }
}
