package net.pottercraft.ollivanders2.test.potion;

import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.potion.OCULUS_FELIS}.
 *
 * @see PotionTestSuper
 */
public class OculusFelisTest extends PotionTestSuper {
    /**
     * Set the potion and effect under test.
     */
    @Override @BeforeEach
    void setUp() {
        potionType = O2PotionType.OCULUS_FELIS;
        effectType = O2EffectType.NIGHT_VISION;
    }
}
