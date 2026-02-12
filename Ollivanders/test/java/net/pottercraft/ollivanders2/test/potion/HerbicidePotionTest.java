package net.pottercraft.ollivanders2.test.potion;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.io.File;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the Herbicide Potion effect.
 *
 * <p>Verifies that the Herbicide Potion correctly creates a HERBICIDE stationary spell effect
 * when the potion splash event occurs. Tests the splash mechanic and stationary spell creation
 * to ensure the potion functions as intended.</p>
 */
public class HerbicidePotionTest {
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
     * Test the Herbicide Potion's splash event behavior.
     *
     * <p>This test verifies that when a Herbicide Potion is splashed, it correctly creates a
     * HERBICIDE stationary spell at the splash location. The test creates a thrown potion item,
     * triggers a splash event, and then verifies that the stationary spell was created at the
     * expected location.</p>
     */
    @Test
    void doOnPotionSplashEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 4, 100);

        ItemStack potionItemStack = Ollivanders2API.getPotions().getPotionItemStackByType(O2PotionType.HERBICIDE_POTION, 1);
        assertNotNull(potionItemStack);

        // Spawn the ThrownPotion entity
        ThrownPotion thrownPotion = testWorld.spawn(location, ThrownPotion.class);
        thrownPotion.setItem(potionItemStack);

        PotionSplashEvent event = new PotionSplashEvent(thrownPotion, new HashMap<>());
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);

        assertTrue(Ollivanders2API.getStationarySpells().checkLocationForStationarySpell(location, O2StationarySpellType.HERBICIDE), "Herbicide potion did not create herbicide stationary spell");
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
