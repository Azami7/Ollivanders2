package net.pottercraft.ollivanders2.test.potion;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.potion.O2Potion;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Abstract base class for potion effect testing.
 *
 * <p>Provides shared test infrastructure for testing individual potion effects. This superclass
 * handles MockBukkit server initialization, plugin loading, and the common test flow for verifying
 * that potions apply their expected effects to players. Child classes implement potion-specific
 * test configuration by overriding the {@code setUp()} method.</p>
 *
 * <p>The test flow is as follows:</p>
 * <ol>
 *   <li>{@code globalSetUp()} - Called once before all tests to initialize the mock server and load the plugin</li>
 *   <li>{@code setUp()} - Called before each test to set up potion-specific test state (implemented by child classes)</li>
 *   <li>{@code drinkTest()} - Template test method that verifies the potion effect is applied correctly</li>
 *   <li>{@code globalTearDown()} - Called once after all tests to clean up server resources</li>
 * </ol>
 *
 * <p>Child classes must set both {@code potionType} and {@code effectType} fields in their {@code setUp()}
 * method. The {@code potionType} specifies which potion to test, and {@code effectType} specifies which
 * effect should be applied when the potion is consumed.</p>
 */
public abstract class PotionTestSuper {
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
     * The type of potion being tested.
     *
     * <p>Must be set by child classes in their {@code setUp()} method before tests run. The
     * {@code drinkTest()} method uses this field to retrieve the correct potion instance from
     * the API. If not set, the test will fail with an assertion error.</p>
     */
    O2PotionType potionType = null;

    /**
     * The effect this potion added to the player.
     *
     * <p>Must be set by child classes in their {@code setUp()} method before tests run. The
     * {@code drinkTest()} method uses this field to check the correct effect was added. If not set,
     * the test will fail with an assertion error.</p>
     */
    O2EffectType effectType = null;

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
     * Set up test-specific state before each test.
     *
     * <p>Called before each test method to initialize potion-specific test state. Child classes
     * must implement this method to set the {@code potionType} field to the potion type being tested
     * and the {@code effectType} field to the effect that should be applied when the potion is consumed.
     * This allows the shared {@code drinkTest()} method to retrieve the correct potion instance and
     * verify that the correct effect was applied.</p>
     */
    @BeforeEach
    abstract void setUp();

    /**
     * Template test method that verifies potion effects are applied when consumed.
     *
     * <p>This test method provides the common testing flow for all potion tests:</p>
     * <ol>
     *   <li>Verifies that {@code potionType} was set by the child class's {@code setUp()} method</li>
     *   <li>Retrieves the potion instance from the API</li>
     *   <li>Creates a mock player and has them drink the potion</li>
     *   <li>Advances the server by 200 ticks to allow effects to take place</li>
     *   <li>Verifies that the player received the potion success message</li>
     *   <li>Verifies that the expected effect (specified by {@code effectType}) was applied to the player</li>
     * </ol>
     *
     * <p>Child classes must set both {@code potionType} and {@code effectType} in their {@code setUp()}
     * method to properly initialize this template test.</p>
     */
    @Test
    void drinkTest() {
        assertNotNull(potionType, "potionType not set");
        O2Potion potion = Ollivanders2API.getPotions().getPotionFromType(potionType);
        assertNotNull(potion, "Failed to get O2Potion");

        PlayerMock player = mockServer.addPlayer();
        potion.drink(player);

        // first check did they get the potion message
        String potionMessage = player.nextMessage();
        assertNotNull(potionMessage);
        assertEquals(potion.getPotionSuccessMessage(), TestCommon.cleanChatMessage(potionMessage), "Player did not receive expected message after drinking potion");

        // verify that the effect was added to the player
        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), effectType), effectType.name() + " not applied to player");
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
