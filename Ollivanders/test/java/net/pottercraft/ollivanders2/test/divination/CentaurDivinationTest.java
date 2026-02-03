package net.pottercraft.ollivanders2.test.divination;

import net.pottercraft.ollivanders2.divination.CENTAUR_DIVINATION;
import net.pottercraft.ollivanders2.divination.O2Divination;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Test suite for the {@link CENTAUR_DIVINATION} divination spell.
 * <p>
 * Verifies that the CENTAUR_DIVINATION divination spell correctly generates prophecies about target players
 * through centaur-based divination techniques. This test class extends {@link DivinationTestSuper} and uses the
 * inherited {@link DivinationTestSuper#divineTest()} test method to validate the core divination
 * functionality specific to the CENTAUR_DIVINATION implementation.
 * </p>
 * <p>
 * Test coverage (inherited from DivinationTestSuper):
 * <ul>
 * <li>Prophecy generation when CENTAUR_DIVINATION.divine() is called</li>
 * <li>Target player name inclusion in generated prophecies</li>
 * <li>Prophecy list management and isolation between tests</li>
 * <li>Scheduler advancement for async prophecy generation</li>
 * </ul>
 * </p>
 *
 * @see CENTAUR_DIVINATION the centaur divination spell implementation being tested
 * @see DivinationTestSuper the parent test class providing shared test infrastructure
 */
public class CentaurDivinationTest extends DivinationTestSuper {
    /**
     * Create a CENTAUR_DIVINATION divination spell instance for testing.
     * <p>
     * Implements the abstract template method from {@link DivinationTestSuper#createDivination(Player, Player, int)}
     * to instantiate a new CENTAUR_DIVINATION spell with the provided prophet, target, and experience level.
     * This instance is used by the inherited {@link DivinationTestSuper#divineTest()} test method.
     * </p>
     *
     * @param prophet the player casting the CENTAUR_DIVINATION divination spell
     * @param target the player who is the subject of the centaur divination prophecy
     * @param experience the diviner's magical experience level, affecting prophecy accuracy
     * @return a new CENTAUR_DIVINATION divination spell instance ready for testing
     */
    @Override
    O2Divination createDivination(@NotNull Player prophet, @NotNull Player target, int experience) {
        return new CENTAUR_DIVINATION(testPlugin, prophet, target, experience);
    }
}
