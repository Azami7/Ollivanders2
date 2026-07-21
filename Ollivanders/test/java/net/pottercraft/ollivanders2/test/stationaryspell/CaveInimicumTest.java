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
 * Unit tests for {@link CAVE_INIMICUM}. Extends {@link ConcealmentShieldSpellTest} for the shared concealment-shield
 * tests.
 */
public class CaveInimicumTest extends ConcealmentShieldSpellTest {
    @Override
    O2StationarySpellType getSpellType() {
        return O2StationarySpellType.CAVE_INIMICUM;
    }

    @Override
    CAVE_INIMICUM createStationarySpell(Player caster, Location location) {
        return new CAVE_INIMICUM(testPlugin, caster.getUniqueId(), location, defaultRadius, defaultDuration);
    }

    /**
     * No-op: Cave Inimicum conceals everyone, so it has no muggle-vs-non-muggle visibility distinction to check.
     */
    @Override @Test
    void doOnEntityTargetEventTestVisibilityCheck() {}

    /**
     * Assert the caster received this spell's proximity alarm message.
     *
     * @param caster the player who cast the spell
     * @param spell  the spell instance under test
     */
    @Override
    void checkProximityAlarm(PlayerMock caster, ConcealmentShieldSpell spell) {
        String message = caster.nextMessage();
        assertNotNull(message, "Caster did not get a proximity alarm message");
        assertEquals(((CAVE_INIMICUM)spell).getProximityAlarmMessage(), TestCommon.cleanChatMessage(message), "Proximity alarm message did not match expected");
    }
}
