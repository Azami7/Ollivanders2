package net.pottercraft.ollivanders2.test.potion;

import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.potion.BARUFFIOS_BRAIN_ELIXIR}.
 *
 * @see PotionTestSuper
 */
public class BaruffiosBrainElixirTest extends PotionTestSuper {
    /**
     * Set the potion and effect types under test.
     */
    @Override @BeforeEach
    void setUp() {
        potionType = O2PotionType.BARUFFIOS_BRAIN_ELIXIR;
        effectType = O2EffectType.HIGHER_SKILL;
    }
}
