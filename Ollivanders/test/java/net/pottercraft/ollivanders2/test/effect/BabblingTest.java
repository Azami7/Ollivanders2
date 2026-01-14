package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.BABBLING;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.entity.Player;
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
    /**
     * Create a BABBLING effect for testing.
     *
     * <p>Instantiates a new BABBLING effect with the specified parameters. This method is called
     * by the test methods to create fresh effect instances for each test scenario.</p>
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration of the effect in game ticks
     * @param isPermanent     true if the effect should be permanent, false for limited duration
     * @return a new BABBLING effect targeting the specified player
     */
    BABBLING createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new BABBLING(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * BABBLING effect does not have special checkEffect behavior.
     *
     * <p>The BABBLING effect has no unique behavior to test in checkEffectTest(). Aging and duration
     * management are tested in the base ageAndKillTest() method.</p>
     */
    void checkEffectTest() {
    }

    /**
     * Run all event handler tests for the BABBLING effect.
     *
     * <p>Executes the chat event test to verify that the effect correctly intercepts and
     * modifies player chat messages with dictionary words.</p>
     */
    void eventHandlerTests() {
        doOnAsyncPlayerChatEventTest();
    }

    /**
     * Test that BABBLING effect replaces chat messages with dictionary words.
     *
     * <p>Creates a player with the BABBLING effect, simulates multiple chat events, and verifies
     * that at least one message is replaced with a word from the effect's dictionary. The test runs
     * 10 iterations to account for the probabilistic nature of the effect (affectPercent).</p>
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
