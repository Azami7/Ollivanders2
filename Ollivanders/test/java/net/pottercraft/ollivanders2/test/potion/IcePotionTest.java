package net.pottercraft.ollivanders2.test.potion;

import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for the Ice Potion.
 *
 * @see PotionTestSuper
 */
public class IcePotionTest extends PotionTestSuper {
    @Override @BeforeEach
    void setUp() {
        potionType = O2PotionType.ICE_POTION;
        effectType = O2EffectType.FIRE_RESISTANCE;
    }
}
