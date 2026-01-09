package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.SWELLING;
import org.bukkit.entity.Player;

/**
 * Test suite for the SWELLING effect.
 *
 * <p>SWELLING is a magical effect that increases a player's size by doubling their scale attribute.
 * This test class inherits comprehensive scale-modification testing from PlayerChangeSizeTestSuper,
 * ensuring that SWELLING properly enlarges the player, handles effect stacking, and restores
 * normal size when removed.</p>
 *
 * <p>Test Coverage (inherited from PlayerChangeSizeTestSuper):</p>
 * <ul>
 * <li>Scale doubling - verifies the player's scale is multiplied by 2.0</li>
 * <li>Effect stacking - verifies that applying another size effect removes SWELLING and recalculates scale</li>
 * <li>Effect cleanup - verifies the player is restored to normal scale (1.0) when SWELLING expires</li>
 * <li>Scale bounds - verifies the maximum scale is clamped to 4.0</li>
 * </ul>
 *
 * @author Azami7
 * @see SWELLING for the effect implementation being tested
 * @see PlayerChangeSizeTestSuper for the base testing framework
 */
public class SwellingTest extends PlayerChangeSizeTestSuper {
    /**
     * Create a SWELLING effect for testing.
     *
     * <p>Instantiates a new SWELLING effect with the specified parameters. This method is called
     * by the test framework to create fresh effect instances for each test scenario.</p>
     *
     * @param target          the player to swell
     * @param durationInTicks the duration of the swelling effect in game ticks
     * @param isPermanent     true if the effect should be permanent, false for limited duration
     * @return a new SWELLING effect targeting the specified player
     */
    @Override
    SWELLING createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new SWELLING(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
