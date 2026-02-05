package net.pottercraft.ollivanders2.test.potion;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.ANIMAGUS_INCANTATION;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.potion.ANIMAGUS_POTION;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.World;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the Animagus Potion effect.
 *
 * <p>Verifies that the Animagus Potion correctly grants the ANIMAGUS_EFFECT to players who meet
 * specific conditions: they must have the ANIMAGUS_INCANTATION effect and the weather must be
 * thundering. Tests multiple scenarios including failure cases (missing incantation or wrong weather)
 * and success cases (all conditions met). Also tests behavior when a player is already an animagus.</p>
 */
public class AnimagusPotionTest {
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
     * Test the Animagus Potion's effect under various conditions.
     *
     * <p>This comprehensive test verifies four distinct scenarios:</p>
     * <ol>
     *   <li><b>No incantation:</b> Player without ANIMAGUS_INCANTATION receives failure message and no effect</li>
     *   <li><b>Wrong weather:</b> Player with ANIMAGUS_INCANTATION but no thunder receives failure message and no effect</li>
     *   <li><b>Success:</b> Player with ANIMAGUS_INCANTATION during thundering weather receives success message,
     *       gains ANIMAGUS_EFFECT, and loses ANIMAGUS_INCANTATION</li>
     *   <li><b>Already animagus:</b> Player who is already an animagus receives the already-animagus message</li>
     * </ol>
     */
    @Test
    void drinkTest() {
        PlayerMock player = mockServer.addPlayer();
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 4, 100);
        ANIMAGUS_POTION potion = new ANIMAGUS_POTION(testPlugin);

        player.setLocation(location);

        // player does not have animagus incantation effect and wrong weather, nothing happens
        potion.drink(player);
        String potionMessage = player.nextMessage();
        assertNotNull(potionMessage);
        assertEquals(potion.getPotionFailureMessage(), TestCommon.cleanChatMessage(potionMessage), "Player did not get expected potion failure message");
        assertFalse(Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.ANIMAGUS_EFFECT), "Animagus effect set on player when they did not have animagus incantation");

        // player has animagus incantation but it is not thundering
        ANIMAGUS_INCANTATION animagusIncantation = new ANIMAGUS_INCANTATION(testPlugin, 1000, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(animagusIncantation);
        mockServer.getScheduler().performTicks(20);
        potion.drink(player);
        potionMessage = player.nextMessage();
        assertNotNull(potionMessage);
        assertEquals(potion.getPotionFailureMessage(), TestCommon.cleanChatMessage(potionMessage), "Player did not get expected potion failure message");
        assertFalse(Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.ANIMAGUS_EFFECT), "Animagus effect set when it was not thundering");

        // player had animagus incantation and it is thundering
        testWorld.setThundering(true);
        potion.drink(player);
        mockServer.getScheduler().performTicks(20);
        potionMessage = player.nextMessage();
        assertNotNull(potionMessage);
        assertEquals(potion.getPotionSuccessMessage(), TestCommon.cleanChatMessage(potionMessage), "Player did not get potion success message");
        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.ANIMAGUS_EFFECT), "Animagus effect not set");
        assertFalse(Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.ANIMAGUS_INCANTATION), "Animagus incantation effect not removed");

        PlayerMock player2 = mockServer.addPlayer();
        O2Player o2player2 = Ollivanders2API.getPlayers().getPlayer(player2.getUniqueId());
        assertNotNull(o2player2);
        o2player2.setIsAnimagus();
        potion.drink(player2);
        potionMessage = player2.nextMessage();
        assertNotNull(potionMessage);
        assertEquals(potion.getAlreadyAnimagusMessage(), TestCommon.cleanChatMessage(potionMessage), "Player2 did not get already animagus message");
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
