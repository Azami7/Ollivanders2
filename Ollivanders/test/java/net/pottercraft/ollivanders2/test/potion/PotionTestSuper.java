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
 * Base class for potion tests. Subclasses set {@link #potionType} and {@link #effectType} in {@link #setUp()}, and
 * inherit {@link #drinkTest()}, which drinks the potion and checks that the effect was applied.
 */
public abstract class PotionTestSuper {
    /**
     * Shared MockBukkit server, mocked once per test class as server setup is expensive.
     */
    static ServerMock mockServer;

    /**
     * The plugin instance, loaded once for the test class.
     */
    static Ollivanders2 testPlugin;

    /**
     * The potion under test; the subclass sets this in {@link #setUp()}.
     */
    O2PotionType potionType = null;

    /**
     * The effect the potion should apply; the subclass sets this in {@link #setUp()}.
     */
    O2EffectType effectType = null;

    @BeforeAll
    static void globalSetUp() {
        Ollivanders2.testMode = true;

        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));

        // advance past the scheduler's 20-tick startup delay
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    /**
     * Set {@link #potionType} and {@link #effectType} for the potion under test.
     */
    @BeforeEach
    abstract void setUp();

    /**
     * Drinking the potion sends its success message and applies its effect to the player.
     */
    @Test
    void drinkTest() {
        assertNotNull(potionType, "potionType not set");
        O2Potion potion = Ollivanders2API.getPotions().getPotionFromType(potionType);
        assertNotNull(potion, "Failed to get O2Potion");

        PlayerMock player = mockServer.addPlayer();
        potion.drink(player);

        String potionMessage = player.nextMessage();
        assertNotNull(potionMessage);
        assertEquals(potion.getPotionSuccessMessage(), TestCommon.cleanChatMessage(potionMessage), "Player did not receive expected message after drinking potion");

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
