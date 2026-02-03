package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.TimeCommon;
import net.pottercraft.ollivanders2.effect.LYCANTHROPY;
import net.pottercraft.ollivanders2.effect.LYCANTHROPY_RELIEF;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the LYCANTHROPY_RELIEF effect.
 *
 * <p>Tests the lycanthropy relief marker effect that temporarily suppresses the secondary symptoms
 * of the lycanthropy curse. Verifies core effect behaviors (aging, duration expiration) and specific
 * LYCANTHROPY_RELIEF mechanics:</p>
 * <ul>
 * <li>Suppresses AGGRESSION and LYCANTHROPY_SPEECH secondary effects from lycanthropy</li>
 * <li>Prevents transformation during moonrise when relief is active</li>
 * <li>Allows transformation to resume when relief expires</li>
 * <li>Properly removes itself from the player when effect expires</li>
 * <li>Sets relief flag on LYCANTHROPY effect when active</li>
 * <li>Clears relief flag on LYCANTHROPY effect when removed</li>
 * </ul>
 *
 * <p>The main focus is on verifying that the relief effect properly suppresses lycanthropy
 * transformation symptoms and correctly manages the relief state when applying and expiring.</p>
 */
public class LycanthropyReliefTest extends EffectTestSuper {
    /**
     * Create a LYCANTHROPY_RELIEF effect for testing.
     *
     * <p>Instantiates a new LYCANTHROPY_RELIEF effect with the specified parameters. This method is called
     * by the test methods to create fresh effect instances for each test scenario.</p>
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration of the effect in game ticks
     * @param isPermanent     true if the effect should be permanent, false for limited duration
     * @return a new LYCANTHROPY_RELIEF effect targeting the specified player
     */
    @Override
    LYCANTHROPY_RELIEF createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new LYCANTHROPY_RELIEF(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * Test that LYCANTHROPY_RELIEF effect suppresses lycanthropy transformation during moonrise.
     *
     * <p>Applies a LYCANTHROPY effect to a player, sets the world to a full moon day at midday,
     * then applies LYCANTHROPY_RELIEF and advances time past moonrise. Verifies that:</p>
     * <ul>
     * <li>The relief flag is set on the lycanthropy effect</li>
     * <li>The player does not transform even though it is past moonrise on a full moon day</li>
     * </ul>
     */
    @Override
    void checkEffectTest() {
        PlayerMock target = mockServer.addPlayer();
        World world = target.getWorld();

        // add lycanthropy effect
        LYCANTHROPY lycanthropy = new LYCANTHROPY(testPlugin, 100, false, target.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(lycanthropy);
        // play forward 5 ticks for the effect to run
        mockServer.getScheduler().performTicks(5);

        // set the time to midday on a full moon day
        world.setFullTime((24000 * 8) + TimeCommon.MIDDAY.getTick());

        // add lycanthropy relief
        LYCANTHROPY_RELIEF lycanthropyRelief = (LYCANTHROPY_RELIEF) addEffect(target, 24000, false);
        // advance the server past moonrise by 10 ticks
        mockServer.getScheduler().performTicks(TimeCommon.MOONRISE.getTick() - TimeCommon.MIDDAY.getTick() + 10);

        assertTrue(lycanthropy.getRelief(), "Target does not have lycanthropy relief");
        assertFalse(lycanthropy.isTransformed(), "Target transformed by lycanthropy when they have relief");
    }

    /**
     * Event handler tests for the LYCANTHROPY_RELIEF effect.
     *
     * <p>LYCANTHROPY_RELIEF does not implement any event handlers because the relief mechanism
     * is handled passively through the relief flag on the LYCANTHROPY effect. Therefore, this
     * test method is empty as there are no event handlers to test.</p>
     */
    @Override
    void eventHandlerTests() {}

    /**
     * Test that LYCANTHROPY_RELIEF cleanup properly restores lycanthropy transformation.
     *
     * <p>Applies a LYCANTHROPY effect to a player with a 100 tick LYCANTHROPY_RELIEF effect,
     * verifies the relief flag is set, then allows the relief to expire and verifies that:</p>
     * <ul>
     * <li>The relief flag is cleared when the effect expires</li>
     * <li>The player transforms as normal when relief expires during moonrise on a full moon day</li>
     * </ul>
     *
     * <p>This confirms that doRemove() properly resets the relief flag on the lycanthropy effect.</p>
     */
    @Override
    void doRemoveTest() {
        PlayerMock target = mockServer.addPlayer();
        World world = target.getWorld();

        // add lycanthropy effect
        LYCANTHROPY lycanthropy = new LYCANTHROPY(testPlugin, 100, false, target.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(lycanthropy);
        // play forward 5 ticks for the effect to run
        mockServer.getScheduler().performTicks(5);

        // set the time to midday on a full moon day
        world.setFullTime((24000 * 8) + TimeCommon.MIDDAY.getTick());

        // add lycanthropy relief for 100 ticks
        LYCANTHROPY_RELIEF lycanthropyRelief = (LYCANTHROPY_RELIEF) addEffect(target, 100, false);
        // advance the server 10 ticks
        mockServer.getScheduler().performTicks(10);
        assertTrue(lycanthropy.getRelief(), "Target has lycanthropy relief");

        // advance the server past moonrise and confirm that the player no longer has relief and transforms
        mockServer.getScheduler().performTicks(TimeCommon.MOONRISE.getTick() - TimeCommon.MIDDAY.getTick());
        assertFalse(lycanthropy.getRelief(), "Target still has lyncanthropy relief when the effect has ended.");
        assertTrue(lycanthropy.isTransformed(), "Target did not transform after moonrise");
    }
}
