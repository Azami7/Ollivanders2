package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.LAUGHING;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.effect.TICKLING;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link TICKLING}.
 *
 * @author Azami7
 * @see EffectTestSuper
 */
public class TicklingTest extends EffectTestSuper {
    @Override
    TICKLING createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new TICKLING(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * TICKLING adds the companion LAUGHING effect and forces the target into sneak.
     */
    @Override
    void checkEffectTest() {
        PlayerMock target = mockServer.addPlayer();
        target.setLocation(origin);
        PlayerMock player = mockServer.addPlayer();
        player.setLocation(origin);

        addEffect(target, Ollivanders2Common.ticksPerSecond * 15, false);
        // advance the server to register the effect and make sure it is running
        mockServer.getScheduler().performTicks(5);

        // verify that LAUGHING effect was added when TICKLING was created
        LAUGHING laughing = (LAUGHING) Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.LAUGHING);
        assertNotNull(laughing, "LAUGHING effect was not added when TICKLING effect was created");

        // tickling effect happens every 5 seconds, advance the server to trigger the effect then verify the target
        // is sneaking.
        mockServer.getScheduler().performTicks(Ollivanders2Common.ticksPerSecond * 5);
        assertTrue(target.isSneaking(), "TICKLING did not cause player to sneak");
    }

    @Override
    void eventHandlerTests() {
        // PlayerToggleSneakEvent not yet implemented in MockBukkit and including this causes the whole test to be skipped
        //doOnPlayerToggleSneakEventTest(target);
    }

    /**
     * A sneak toggle by the tickled player is cancelled, keeping them doubled over. Currently unused: MockBukkit does
     * not implement PlayerToggleSneakEvent.
     *
     * @param target the tickled player
     */
    void doOnPlayerToggleSneakEventTest(Player target) {
        PlayerToggleSneakEvent event = new PlayerToggleSneakEvent(target, true);
        mockServer.getPluginManager().callEvent(event);

        mockServer.getScheduler().performTicks(5);
        assertTrue(event.isCancelled(), "PlayerToggleSneakEvent not canceled by TICKLING");
    }

    /**
     * Removing TICKLING also removes the companion LAUGHING effect.
     */
    @Override
    void doRemoveTest() {
        PlayerMock target = mockServer.addPlayer();
        TICKLING tickling = (TICKLING) addEffect(target, 100, false);
        Ollivanders2API.getPlayers().playerEffects.addEffect(tickling);
        mockServer.getScheduler().performTicks(5);

        // call doRemove() to clean up
        tickling.doRemove();

        // verify that LAUGHING effect was removed
        LAUGHING laughing = (LAUGHING) Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.LAUGHING);
        assertNull(laughing, "LAUGHING effect was not removed when TICKLING effect was cleaned up");
    }
}
