package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.MUTED_SPEECH;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the MUTED_SPEECH effect.
 *
 * <p>Tests the speech suppression effect that prevents players from sending chat messages. Verifies
 * core effect behaviors (aging, duration expiration) and specific MUTED_SPEECH mechanics:</p>
 * <ul>
 * <li>Cancels AsyncPlayerChatEvent to block player chat messages</li>
 * <li>Properly ages and expires based on duration</li>
 * <li>Has no persistent state to clean up on removal</li>
 * </ul>
 *
 * <p>The main focus is on verifying that the event handler properly intercepts and cancels all
 * chat attempts from affected players.</p>
 */
public class MutedSpeechTest extends EffectTestSuper {
    /**
     * Create a MUTED_SPEECH effect for testing.
     *
     * <p>Instantiates a new MUTED_SPEECH effect with the specified parameters. This method is called
     * by the test methods to create fresh effect instances for each test scenario.</p>
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration of the effect in game ticks
     * @param isPermanent     true if the effect should be permanent, false for limited duration
     * @return a new MUTED_SPEECH effect targeting the specified player
     */
    @Override
    MUTED_SPEECH createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new MUTED_SPEECH(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * MUTED_SPEECH checkEffect() behavior test.
     *
     * <p>MUTED_SPEECH has no special behavior in checkEffect() beyond aging. The effect's main logic
     * is handled through event cancellation in doOnAsyncPlayerChatEvent(). Effect aging and duration
     * expiration are tested separately in the base class checkEventAging() test, so this method is empty.</p>
     */
    @Override
    void checkEffectTest() {}

    /**
     * Run all event handler tests for the MUTED_SPEECH effect.
     *
     * <p>Executes the AsyncPlayerChatEvent test to verify that the effect correctly intercepts
     * and cancels all chat messages from affected players.</p>
     */
    @Override
    void eventHandlerTests() {
        doOnAsyncPlayerChatEventTest();
    }

    /**
     * Test that MUTED_SPEECH effect prevents players from sending chat messages.
     *
     * <p>Applies the MUTED_SPEECH effect to a player, simulates an AsyncPlayerChatEvent from that player,
     * and verifies that:
     * <ul>
     * <li>The chat event is cancelled by the MUTED_SPEECH effect</li>
     * <li>Other players do not receive the cancelled chat message</li>
     * </ul>
     * </p>
     */
    void doOnAsyncPlayerChatEventTest() {
        Player target = mockServer.addPlayer();
        PlayerMock player = mockServer.addPlayer();
        MUTED_SPEECH muted_speech = (MUTED_SPEECH) addEffect(target, 100, false);

        String chat = "hello world";
        AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(false, target, chat, new HashSet<>());
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(5);

        // confirm the chat event was canceled
        assertTrue(event.isCancelled(), "MUTED_SPEECH did not cancel AsyncPlayerChatEvent");

        // confirm that players did not receive a chat message
        assertNull(player.nextMessage(), "Player saw a chat message when there should not have been one");
    }

    /**
     * MUTED_SPEECH cleanup test.
     *
     * <p>MUTED_SPEECH has no special cleanup logic in doRemove() because the effect has no persistent
     * state. When removed, the player simply regains normal chat ability. This method is empty as there
     * is nothing specific to test beyond the base class cleanup behavior.</p>
     */
    @Override
    void doRemoveTest() {}
}
