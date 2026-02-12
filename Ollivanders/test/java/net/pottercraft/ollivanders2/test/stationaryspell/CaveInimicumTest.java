package net.pottercraft.ollivanders2.test.stationaryspell;

import net.pottercraft.ollivanders2.stationaryspell.CAVE_INIMICUM;
import net.pottercraft.ollivanders2.stationaryspell.ConcealmentShieldSpell;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test suite for the {@link CAVE_INIMICUM} stationary spell implementation.
 *
 * <p>Tests the cave inimicum protective barrier spell, which prevents hostile entities from entering
 * a protected area. Inherits common concealment shield spell tests from {@link ConcealmentShieldSpellTest}
 * and provides spell-specific factory methods for test setup.</p>
 */
public class CaveInimicumTest extends ConcealmentShieldSpellTest {
    /**
     * Gets the spell type for this test suite.
     *
     * @return {@link O2StationarySpellType#CAVE_INIMICUM}
     */
    @Override
    O2StationarySpellType getSpellType() {
        return O2StationarySpellType.CAVE_INIMICUM;
    }

    /**
     * Creates a {@link CAVE_INIMICUM} spell instance for testing.
     *
     * <p>Constructs a new spell at the specified location cast by the given player, using default
     * radius and duration values inherited from the parent test class.</p>
     *
     * @param caster   the player casting the spell (not null)
     * @param location the center location for the spell (not null)
     * @return a new CAVE_INIMICUM spell instance
     */
    @Override
    CAVE_INIMICUM createStationarySpell(Player caster, Location location) {
        return new CAVE_INIMICUM(testPlugin, caster.getUniqueId(), location, defaultRadius, defaultDuration);
    }

    /**
     * CAVE_INIMICUM does not restrict player visibility based on spell type (all players visible).
     *
     * <p>Unlike REPELLO_MUGGLETON, CAVE_INIMICUM does not distinguish between muggles and non-muggles
     * for visibility purposes. This test is a no-op placeholder to fulfill the abstract method contract.</p>
     */
    @Override @Test
    void doOnEntityTargetEventTestVisibilityCheck() {}

    /**
     * Verifies that the caster receives the correct proximity alarm message.
     *
     * <p>When a hostile entity approaches the spell boundary, checks that:
     * <ul>
     *   <li>The caster receives a proximity alarm message</li>
     *   <li>The message matches the spell's configured proximity alarm message</li>
     * </ul>
     * </p>
     *
     * @param caster the player who cast the spell (should receive alarm message)
     * @param spell  the CAVE_INIMICUM spell instance (provides the expected alarm message)
     */
    @Override
    void checkProximityAlarm(PlayerMock caster, ConcealmentShieldSpell spell) {
        String message = caster.nextMessage();
        assertNotNull(message, "Caster did not get a proximity alarm message");
        assertEquals(((CAVE_INIMICUM)spell).getProximityAlarmMessage(), TestCommon.cleanChatMessage(message), "Proximity alarm message did not match expected");
    }
}
