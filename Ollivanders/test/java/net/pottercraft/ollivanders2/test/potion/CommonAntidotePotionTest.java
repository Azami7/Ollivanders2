package net.pottercraft.ollivanders2.test.potion;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.potion.COMMON_ANTIDOTE_POTION;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test suite for the Common Antidote Potion effect.
 *
 * <p>Verifies that the Common Antidote Potion correctly removes poison effects from players
 * when consumed. Tests both the no-effect scenario (when player has no poison) and the
 * success scenario (when player has an active poison effect). This potion is designed to
 * cure poison effects that affect players.</p>
 */
public class CommonAntidotePotionTest  {
    /**
     * Shared mock Bukkit server instance for all tests.
     *
     * <p>Static field initialized once before all tests in this class. Reused across test instances
     * to avoid expensive server setup/teardown for each test method.</p>
     */
    static ServerMock mockServer;

    /**
     * The plugin instance being tested.
     *
     * <p>Loaded once before all tests with the default configuration. Provides access to
     * logger, scheduler, and other plugin API methods during tests.</p>
     */
    static Ollivanders2 testPlugin;

    /**
     * Initialize the mock Bukkit server before all tests.
     *
     * <p>Static setup method called once before all tests in this class. Creates the shared
     * MockBukkit server instance that is reused across all test methods to avoid expensive
     * server creation/destruction overhead.</p>
     */
    @BeforeAll
    static void globalSetUp() {
        Ollivanders2.testMode = true;

        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));

        // advance the server by 20 ticks to let the scheduler start (it has an initial delay of 20 ticks)
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    /**
     * Test the Common Antidote Potion's effect on poison and no-effect scenarios.
     *
     * <p>This test verifies two scenarios:</p>
     * <ol>
     *   <li><b>No-effect scenario:</b> When a player without any poison effect drinks the potion,
     *       they should receive a "potion has no effect" message</li>
     *   <li><b>Success scenario:</b> When a player with an active POISON effect drinks the potion,
     *       the poison effect should be removed and they should receive a success message</li>
     * </ol>
     *
     * <p>The test procedure:</p>
     * <ol>
     *   <li>Create a Common Antidote Potion instance</li>
     *   <li>Create a mock player and have them drink the potion (no poison)</li>
     *   <li>Verify the player receives the no-effect message</li>
     *   <li>Apply a POISON effect to the player</li>
     *   <li>Have the player drink the potion again</li>
     *   <li>Verify the player receives the success message</li>
     *   <li>Verify the POISON effect has been removed</li>
     * </ol>
     */
    @Test
    void drinkTest() {
        COMMON_ANTIDOTE_POTION potion = new COMMON_ANTIDOTE_POTION(testPlugin);
        PlayerMock player = mockServer.addPlayer();

        // player does not have poison
        potion.drink(player);

        String potionMessage = player.nextMessage();
        assertNotNull(potionMessage);
        assertEquals(potion.getPotionDoNothingMessage(), TestCommon.cleanChatMessage(potionMessage), "Player did not get expected message when potion has no effect");

        // player has poison
        player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 1000, 1));
        potion.drink(player);

        potionMessage = player.nextMessage();
        assertNotNull(potionMessage);
        assertEquals(potion.getPotionSuccessMessage(), TestCommon.cleanChatMessage(potionMessage), "Player did not get expected message when potion succeeded");
        assertFalse(player.hasPotionEffect(PotionEffectType.POISON), "Player still has POISON effect");
    }

    /**
     * Tear down the mock Bukkit server after all tests complete.
     *
     * <p>Static teardown method called once after all tests in this class have finished.
     * Releases the MockBukkit server resources to prevent memory leaks and allow clean
     * test execution in subsequent test classes.</p>
     */
    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
