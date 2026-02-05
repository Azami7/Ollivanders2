package net.pottercraft.ollivanders2.test.potion;

import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import org.junit.jupiter.api.BeforeEach;

/**
 * Test suite for the Baruffios Brain Elixir potion effect.
 *
 * <p>Verifies that the Baruffios Brain Elixir potion correctly applies the HIGHER_SKILL effect to players
 * when consumed. Tests both the player feedback message and the effect application to ensure
 * the potion functions as intended.</p>
 *
 * @see PotionTestSuper for the base test infrastructure
 */
public class BaruffiosBrainElixirTest extends PotionTestSuper {
    /**
     * Set up the test by specifying the potion type to test.
     *
     * <p>Initializes the test to use the BARUFFIOS_BRAIN_ELIXIR potion type and the HIGHER_SKILL effect.
     * This method is called before each test to ensure the correct potion is being tested by the
     * inherited drinkTest() method.</p>
     */
    @Override @BeforeEach
    void setUp() {
        potionType = O2PotionType.BARUFFIOS_BRAIN_ELIXIR;
        effectType = O2EffectType.HIGHER_SKILL;
    }
}
