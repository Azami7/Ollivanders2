package net.pottercraft.ollivanders2.test.potion;

import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import org.junit.jupiter.api.BeforeEach;

/**
 * Test suite for the Strengthening Solution potion effect.
 *
 * <p>Verifies that the Strengthening Solution potion correctly applies the STRENGTH effect to players
 * when consumed. Tests both the player feedback message and the effect application to ensure
 * the potion functions as intended.</p>
 *
 * @see PotionTestSuper for the base test infrastructure
 */
public class StrengtheningSolutionTest extends PotionTestSuper {
    /**
     * Set up the test by specifying the potion type to test.
     *
     * <p>Initializes the test to use the STRENGTHENING_SOLUTION potion type and the STRENGTH effect.
     * This method is called before each test to ensure the correct potion is being tested by the
     * inherited drinkTest() method.</p>
     */
    @Override @BeforeEach
    void setUp() {
        potionType = O2PotionType.STRENGTHENING_SOLUTION;
        effectType = O2EffectType.STRENGTH;
    }
}
