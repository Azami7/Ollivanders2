package net.pottercraft.ollivanders2.test.potion;

import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for the Memory Potion.
 *
 * @see PotionTestSuper
 */
public class MemoryPotionTest extends PotionTestSuper {
    @Override @BeforeEach
    void setUp() {
        potionType = O2PotionType.MEMORY_POTION;
        effectType = O2EffectType.FAST_LEARNING;
    }
}
