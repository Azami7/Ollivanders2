package net.pottercraft.ollivanders2.test.potion;

import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for the Weakness Potion.
 *
 * @see PotionTestSuper
 */
public class WeaknessTest extends PotionTestSuper {
    @Override @BeforeEach
    void setUp() {
        potionType = O2PotionType.WEAKNESS_POTION;
        effectType = O2EffectType.WEAKNESS;
    }
}
