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
 * Unit tests for {@link LYCANTHROPY_RELIEF}.
 *
 * @see EffectTestSuper
 */
public class LycanthropyReliefTest extends EffectTestSuper {
    @Override
    LYCANTHROPY_RELIEF createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new LYCANTHROPY_RELIEF(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * While active, relief sets the LYCANTHROPY relief flag and suppresses transformation past moonrise on a full
     * moon day.
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
     * LYCANTHROPY_RELIEF has no event handlers; the relief mechanism is passive via the LYCANTHROPY relief flag.
     */
    @Override
    void eventHandlerTests() {}

    /**
     * On expiry, doRemove() clears the LYCANTHROPY relief flag so the player transforms again past moonrise on a
     * full moon day.
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
