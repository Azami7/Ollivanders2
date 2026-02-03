package net.pottercraft.ollivanders2.test.divination;

import net.pottercraft.ollivanders2.divination.CRYSTAL_BALL;
import net.pottercraft.ollivanders2.divination.O2Divination;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Test suite for the {@link CRYSTAL_BALL} divination spell.
 * <p>
 * Verifies that the CRYSTAL_BALL divination spell correctly generates prophecies about target players
 * through crystal ball scrying. This test class extends {@link DivinationTestSuper} and uses the
 * inherited {@link DivinationTestSuper#divineTest()} test method to validate the core divination
 * functionality specific to the CRYSTAL_BALL implementation.
 * </p>
 * <p>
 * Test coverage (inherited from DivinationTestSuper):
 * <ul>
 * <li>Prophecy generation when CRYSTAL_BALL.divine() is called</li>
 * <li>Target player name inclusion in generated prophecies</li>
 * <li>Prophecy list management and isolation between tests</li>
 * <li>Scheduler advancement for async prophecy generation</li>
 * </ul>
 * </p>
 *
 * @see CRYSTAL_BALL the crystal ball scrying spell implementation being tested
 * @see DivinationTestSuper the parent test class providing shared test infrastructure
 */
public class CrystalBallTest extends DivinationTestSuper {
    /**
     * Create a CRYSTAL_BALL divination spell instance for testing.
     * <p>
     * Implements the abstract template method from {@link DivinationTestSuper#createDivination(Player, Player, int)}
     * to instantiate a new CRYSTAL_BALL spell with the provided prophet, target, and experience level.
     * This instance is used by the inherited {@link DivinationTestSuper#divineTest()} test method.
     * </p>
     *
     * @param prophet the player casting the CRYSTAL_BALL divination spell
     * @param target the player who is the subject of the crystal ball prophecy
     * @param experience the diviner's magical experience level, affecting prophecy accuracy
     * @return a new CRYSTAL_BALL divination spell instance ready for testing
     */
    @Override
    O2Divination createDivination(@NotNull Player prophet, @NotNull Player target, int experience) {
        return new CRYSTAL_BALL(testPlugin, prophet, target, experience);
    }
}
