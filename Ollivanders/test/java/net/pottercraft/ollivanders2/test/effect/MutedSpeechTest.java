package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.MUTED_SPEECH;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link MUTED_SPEECH}.
 *
 * @see EffectTestSuper
 */
public class MutedSpeechTest extends EffectTestSuper {
    @Override
    MUTED_SPEECH createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new MUTED_SPEECH(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    @Override
    void checkEffectTest() {}

    @Override
    void eventHandlerTests() {
        doOnAsyncPlayerChatEventTest();
    }

    /**
     * The chat handler cancels an affected player's AsyncPlayerChatEvent and no one receives the message.
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

    @Override
    void doRemoveTest() {}
}
