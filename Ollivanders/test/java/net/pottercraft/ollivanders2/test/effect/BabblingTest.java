package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.BABBLING;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the BABBLING effect.
 *
 * <p>Tests that the BABBLING effect correctly replaces player chat messages with nonsense words
 * from its internal dictionary. Verifies probabilistic message replacement based on affectPercent
 * (default 100%) and that the replaced messages contain valid dictionary words.</p>
 *
 * <p>The event handler test runs multiple chat events to account for the probabilistic nature
 * of the effect and ensures at least one message is modified.</p>
 */
public class BabblingTest extends EffectTestSuper {
    BABBLING createEffect(int durationInTicks, boolean isPermanent) {
        return new BABBLING(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    void checkEffectTest() {
        checkEffectTestAgingHelper();
    }

    void eventHandlerTests() {
        doOnAsyncPlayerChatEventTest();
    }

    void doOnAsyncPlayerChatEventTest() {
        BABBLING babbling = createEffect(100, false);
        Ollivanders2API.getPlayers().playerEffects.addEffect(babbling);

        // Perform one tick to ensure the effect is processed into the active effects system
        mockServer.getScheduler().performTicks(1);

        String chat = "hello world";

        int chance = 0;
        for (int i = 0; i < 10; i++) {
            AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(false, target, chat, new HashSet<>());
            mockServer.getPluginManager().callEvent(event);

            String actualChat = event.getMessage();
            if (!actualChat.equals(chat)) {
                assertTrue(TestCommon.stringContainsListMatch(babbling.dictionary, actualChat), "Chat is not from the dictionary, chat: " + actualChat);
                chance = chance + 1;
            }
        }

        assertNotEquals(0, chance, babbling.effectType.toString() + " did not change the chat in 10 tries");
    }

    /**
     * doRemove() for BABBLING does nothing
     */
    void doRemoveTest() {}
}
