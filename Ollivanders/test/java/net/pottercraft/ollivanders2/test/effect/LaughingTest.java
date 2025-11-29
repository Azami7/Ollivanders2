package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.LAUGHING;
import org.bukkit.entity.Player;

/**
 * Test suite for the LAUGHING effect.
 *
 * <p>LAUGHING is an effect that causes players to speak nonsense words, similar to BABBLING.
 * Inherits all tests from BabblingTest, including chat message replacement verification and
 * default effect aging behavior.</p>
 *
 * <p>This effect uses all default behavior from the BABBLING test suite without any special
 * overrides, as LAUGHING follows the standard effect patterns for probabilistic message modification.</p>
 */
public class LaughingTest extends BabblingTest {
    /**
     * Create a LAUGHING effect for testing.
     *
     * <p>Instantiates a new LAUGHING effect with the specified parameters. This method is called
     * by the test methods to create fresh effect instances for each test scenario.</p>
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration of the effect in game ticks
     * @param isPermanent     true if the effect should be permanent, false for limited duration
     * @return a new LAUGHING effect targeting the specified player
     */
    LAUGHING createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new LAUGHING(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
