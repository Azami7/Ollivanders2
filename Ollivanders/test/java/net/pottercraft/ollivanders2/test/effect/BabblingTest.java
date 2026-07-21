package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.BABBLING;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Unit tests for {@link BABBLING}.
 *
 * @see EffectTestSuper
 */
public class BabblingTest extends EffectTestSuper {
    @Override
    BABBLING createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new BABBLING(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    @Override
    void checkEffectTest() {
    }

    @Override
    void eventHandlerTests() {
        doOnAsyncPlayerChatEventTest();
    }

    /**
     * The effect replaces chat messages with words from its dictionary. Runs 10 chats since replacement is
     * probabilistic (affectPercent) and asserts at least one was changed.
     */
    void doOnAsyncPlayerChatEventTest() {
        Player target = mockServer.addPlayer();
        BABBLING babbling = (BABBLING) addEffect(target, 100, false);

        String chat = "hello world";

        int chance = 0;
        for (int i = 0; i < 10; i++) {
            AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(false, target, chat, new HashSet<>());
            mockServer.getPluginManager().callEvent(event);

            String actualChat = event.getMessage();
            if (!actualChat.equals(chat)) {
                TestCommon.stringContainsListMatch(babbling.dictionary, actualChat);
                chance = chance + 1;
            }
        }

        assertNotEquals(0, chance, babbling.effectType.toString() + " did not change the chat in 10 tries");
    }

    /**
     * doRemove() for BABBLING does nothing
     */
    @Override
    void doRemoveTest() {}
}
