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
 * Unit tests for {@link COMMON_ANTIDOTE_POTION}.
 */
public class CommonAntidotePotionTest  {
    /**
     * Shared MockBukkit server, mocked once per test class as server setup is expensive.
     */
    static ServerMock mockServer;

    /**
     * The plugin instance, loaded once for the test class.
     */
    static Ollivanders2 testPlugin;

    @BeforeAll
    static void globalSetUp() {
        Ollivanders2.testMode = true;

        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));

        // advance past the scheduler's 20-tick startup delay
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    /**
     * Drinking with no poison sends the do-nothing message; drinking with POISON active removes it and sends the
     * success message.
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

    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
