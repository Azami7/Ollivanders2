package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.SHRINKING;
import org.bukkit.entity.Player;

/**
 * Test suite for the SHRINKING effect.
 *
 * <p>SHRINKING is a magical effect that decreases a player's size by halving their scale attribute.
 * This test class inherits comprehensive scale-modification testing from PlayerChangeSizeTestSuper,
 * ensuring that SHRINKING properly reduces the player, handles effect stacking, and restores
 * normal size when removed.</p>
 *
 * <p>Test Coverage (inherited from PlayerChangeSizeTestSuper):</p>
 * <ul>
 * <li>Scale halving - verifies the player's scale is multiplied by 0.5</li>
 * <li>Effect stacking - verifies that applying another size effect removes SHRINKING and recalculates scale</li>
 * <li>Effect cleanup - verifies the player is restored to normal scale (1.0) when SHRINKING expires</li>
 * <li>Scale bounds - verifies the minimum scale is clamped to 0.25</li>
 * </ul>
 *
 * @author Azami7
 * @see SHRINKING for the effect implementation being tested
 * @see PlayerChangeSizeTestSuper for the base testing framework
 */
public class ShrinkingTest extends PlayerChangeSizeTestSuper {
    /**
     * Create a SHRINKING effect for testing.
     *
     * <p>Instantiates a new SHRINKING effect with the specified parameters. This method is called
     * by the test framework to create fresh effect instances for each test scenario.</p>
     *
     * @param target          the player to shrink
     * @param durationInTicks the duration of the shrinking effect in game ticks
     * @param isPermanent     true if the effect should be permanent, false for limited duration
     * @return a new SHRINKING effect targeting the specified player
     */
    @Override
    SHRINKING createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new SHRINKING(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
