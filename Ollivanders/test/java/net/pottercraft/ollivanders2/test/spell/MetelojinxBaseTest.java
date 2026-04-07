package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.MetelojinxBase;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Abstract tests for {@link MetelojinxBase} weather-altering spells.
 *
 * <p>Verifies the shared behaviour across both Metelojinx variants: when the current weather is the
 * opposite of what the spell wants, the spell adjusts the weather duration; when the current weather
 * already matches the spell's intent, the spell sends a failure message and does not change the duration.</p>
 */
abstract public class MetelojinxBaseTest extends O2SpellTestSuper {
    /**
     * Get the initial storm state under which this spell should successfully change the weather.
     *
     * <p>METELOJINX (start storm) succeeds when there is no storm, so returns {@code false}.
     * METELOJINX_RECANTO (stop storm) succeeds when there is a storm, so returns {@code true}.</p>
     *
     * @return the storm state to set up for the success test
     */
    abstract boolean getInitialStormStateForSuccess();

    /**
     * Verify the spell changes the weather duration when the current weather opposes the spell's intent,
     * and sends a failure message when the current weather already matches the spell's intent.
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        // success case: current weather opposes the spell's intent
        testWorld.setStorm(getInitialStormStateForSuccess());
        testWorld.setWeatherDuration(20000);
        int initialDuration = testWorld.getWeatherDuration();

        MetelojinxBase spell = (MetelojinxBase) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(spell.isKilled(), "spell not killed after cast");
        assertNotEquals(initialDuration, testWorld.getWeatherDuration(), "weather duration was not changed on successful cast");
        assertNull(caster.nextMessage(), "caster received unexpected message on successful cast");

        // failure case: current weather already matches the spell's intent
        testWorld.setStorm(!getInitialStormStateForSuccess());
        testWorld.setWeatherDuration(20000);
        initialDuration = testWorld.getWeatherDuration();

        spell = (MetelojinxBase) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(spell.isKilled(), "spell not killed after cast");
        assertEquals(initialDuration, testWorld.getWeatherDuration(), "weather duration changed on failed cast");

        String message = caster.nextMessage();
        assertNotNull(message, "caster did not receive failure message");
        assertEquals(spell.getFailureMessage(), TestCommon.cleanChatMessage(message), "caster did not receive expected failure message");
    }

    @Override
    @Test
    void revertTest() {
        // no revert actions
    }
}