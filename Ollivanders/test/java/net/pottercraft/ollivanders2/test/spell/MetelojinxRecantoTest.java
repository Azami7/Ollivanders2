package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.METELOJINX_RECANTO;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the {@link net.pottercraft.ollivanders2.spell.METELOJINX_RECANTO} spell, which ends a storm.
 */
public class MetelojinxRecantoTest extends MetelojinxBaseTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.METELOJINX_RECANTO;
    }

    /**
     * METELOJINX_RECANTO ends a storm, so it succeeds when there is a storm currently.
     */
    @Override
    boolean getInitialStormStateForSuccess() {
        return true;
    }

    /**
     * Verify that when the reduction would make the storm duration negative, the duration is clamped to
     * 0 and the storm ends.
     */
    @Test
    void stopStormDurationBelowZeroTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName() + "BelowZero");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        testWorld.setStorm(true);
        // small remaining duration so the reduction (default experience 20 → 2 * ticksPerMinute = 2400) goes negative
        testWorld.setWeatherDuration(100);

        METELOJINX_RECANTO recanto = (METELOJINX_RECANTO) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(recanto.isKilled(), "spell not killed after cast");
        assertEquals(0, testWorld.getWeatherDuration(), "weather duration should be clamped to 0 when reduction goes negative");

        // give MockBukkit additional ticks to process the duration-zero state and toggle the storm off
        mockServer.getScheduler().performTicks(100);
        assertFalse(testWorld.hasStorm(), "storm should have ended after duration clamped to 0");
    }
}