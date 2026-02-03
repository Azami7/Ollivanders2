package net.pottercraft.ollivanders2.test.divination;

import net.pottercraft.ollivanders2.divination.ASTROLOGY;
import net.pottercraft.ollivanders2.divination.O2Divination;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Test suite for the {@link ASTROLOGY} divination spell.
 * <p>
 * Verifies that the ASTROLOGY divination spell correctly generates prophecies about target players
 * through astrological divination. This test class extends {@link DivinationTestSuper} and uses the
 * inherited {@link DivinationTestSuper#divineTest()} test method to validate the core divination
 * functionality specific to the ASTROLOGY implementation.
 * </p>
 * <p>
 * Test coverage (inherited from DivinationTestSuper):
 * <ul>
 * <li>Prophecy generation when ASTROLOGY.divine() is called</li>
 * <li>Target player name inclusion in generated prophecies</li>
 * <li>Prophecy list management and isolation between tests</li>
 * <li>Scheduler advancement for async prophecy generation</li>
 * </ul>
 * </p>
 *
 * @see ASTROLOGY the astrological divination spell implementation being tested
 * @see DivinationTestSuper the parent test class providing shared test infrastructure
 */
public class AstrologyTest extends DivinationTestSuper {
    /**
     * Create an ASTROLOGY divination spell instance for testing.
     * <p>
     * Implements the abstract template method from {@link DivinationTestSuper#createDivination(Player, Player, int)}
     * to instantiate a new ASTROLOGY spell with the provided prophet, target, and experience level.
     * This instance is used by the inherited {@link DivinationTestSuper#divineTest()} test method.
     * </p>
     *
     * @param prophet the player casting the ASTROLOGY divination spell
     * @param target the player who is the subject of the astrological prophecy
     * @param experience the diviner's magical experience level, affecting prophecy accuracy
     * @return a new ASTROLOGY divination spell instance ready for testing
     */
    @Override
    O2Divination createDivination(@NotNull Player prophet, @NotNull Player target, int experience) {
        return new ASTROLOGY(testPlugin, prophet, target, experience);
    }
}
