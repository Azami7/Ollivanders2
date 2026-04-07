package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.METELOJINX;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the {@link net.pottercraft.ollivanders2.spell.METELOJINX} spell, which starts a storm.
 */
public class MetelojinxTest extends MetelojinxBaseTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.METELOJINX;
    }

    /**
     * METELOJINX starts a storm, so it succeeds when there is no storm currently.
     */
    @Override
    boolean getInitialStormStateForSuccess() {
        return false;
    }

    /**
     * Verify that when usesModifier is less than 10 (so {@code (int)(usesModifier/10)} truncates to 0),
     * the storm duration is clamped to the 1-minute minimum rather than 0.
     */
    @Test
    void minStormDurationTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName() + "MinDuration");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        testWorld.setStorm(false);
        testWorld.setWeatherDuration(20000);

        // experience 5 → usesModifier = 5, (int)(5/10) = 0
        METELOJINX metelojinx = (METELOJINX) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 5);
        mockServer.getScheduler().performTicks(20);
        assertTrue(metelojinx.isKilled(), "spell not killed after cast");
        assertEquals(Ollivanders2Common.ticksPerMinute, testWorld.getWeatherDuration(),
                "weather duration should be clamped to 1 minute minimum when usesModifier < 10");
    }
}