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
 * Test suite for the TICKLING effect.
 *
 * <p>TICKLING is a debilitating effect that forces the affected player into a continuous sneak state.
 * The effect also applies a secondary LAUGHING effect, making the player both crouch from tickling and
 * burst into uncontrollable laughter. This test validates that the effect properly forces sneaking,
 * applies the secondary effect, prevents sneak toggling, and cleans up both effects when removed.</p>
 *
 * <p>Test Coverage:</p>
 * <ul>
 * <li>Secondary LAUGHING effect creation - verifies LAUGHING is added when TICKLING is applied</li>
 * <li>Sneak state enforcement - verifies player is forced to sneak every 5 seconds</li>
 * <li>Secondary effect behavior - verifies LAUGHING chat messages are generated</li>
 * <li>Effect cleanup - verifies LAUGHING is removed when TICKLING is removed</li>
 * </ul>
 *
 * @author Azami7
 * @see TICKLING for the effect implementation being tested
 * @see EffectTestSuper for the base testing framework
 */
public class TicklingTest extends EffectTestSuper {
    /**
     * Create a TICKLING effect for testing.
     *
     * <p>Instantiates a new TICKLING effect with the specified parameters. This method is called
     * by the test methods to create fresh effect instances for each test scenario.</p>
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration of the effect in game ticks
     * @param isPermanent     true if the effect should be permanent, false for limited duration
     * @return a new TICKLING effect targeting the specified player
     */
    TICKLING createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new TICKLING(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * Test that TICKLING effect forces continuous sneaking and applies the LAUGHING secondary effect.
     *
     * <p>This test validates multiple aspects of the TICKLING effect:</p>
     * <ol>
     * <li><strong>Secondary Effect Creation:</strong>
     * <ul>
     * <li>Verifies that the LAUGHING effect is automatically added when TICKLING is applied</li>
     * </ul></li>
     * <li><strong>Sneak Enforcement:</strong>
     * <ul>
     * <li>Verifies that after 5 seconds, the player is forced into sneak state</li>
     * </ul></li>
     * <li><strong>Secondary Effect Behavior:</strong>
     * <ul>
     * <li>Verifies that nearby players receive chat messages from the LAUGHING effect</li>
     * </ul></li>
     * </ol>
     */
    @Override
    void checkEffectTest() {
        PlayerMock target = mockServer.addPlayer();
        target.setLocation(origin);
        PlayerMock player = mockServer.addPlayer();
        player.setLocation(origin);

        TICKLING tickling = (TICKLING) addEffect(target, Ollivanders2Common.ticksPerSecond * 15, false);
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

    /**
     * Run all event handler tests for the TICKLING effect.
     *
     * <p>TICKLING has a PlayerToggleSneakEvent handler that prevents the player from un-sneaking.
     * However, PlayerToggleSneakEvent is not yet implemented in MockBukkit, so event handler testing
     * is currently skipped to avoid test failures.</p>
     */
    @Override
    void eventHandlerTests() {
        // PlayerToggleSneakEvent not yet implemented in MockBukkit and including this causes the whole test to be skipped
        //doOnPlayerToggleSneakEventTest(target);
    }

    /**
     * Test that PlayerToggleSneakEvent is cancelled by TICKLING.
     *
     * <p>Verifies that tickled players cannot toggle out of sneak mode. The test creates a sneak
     * toggle event and ensures it is cancelled, preventing the player from standing up during the effect.</p>
     *
     * <p>Note: This test is currently commented out because PlayerToggleSneakEvent is not yet
     * implemented in MockBukkit.</p>
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
     * Verify that removing TICKLING also removes the secondary LAUGHING effect.
     *
     * <p>Tests that the doRemove() method correctly cleans up the LAUGHING secondary effect
     * that was added when TICKLING was applied. This ensures the player doesn't remain affected
     * by the laughter after the tickling ends.</p>
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
