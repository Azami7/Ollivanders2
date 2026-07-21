package net.pottercraft.ollivanders2.test.potion;

import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.potion.SATIATION_POTION}.
 *
 * @see PotionTestSuper
 */
public class SatiationPotionTest extends PotionTestSuper {
    /**
     * Set the potion and effect types under test.
     */
    @Override @BeforeEach
    void setUp() {
        potionType = O2PotionType.SATIATION_POTION;
        effectType = O2EffectType.SATIATION;
    }
}
