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
 * Unit tests for the Herbicide Potion.
 */
public class HerbicidePotionTest {
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
     * Splashing a Herbicide Potion creates a HERBICIDE stationary spell at the splash location.
     */
    @Test
    void doOnPotionSplashEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 4, 100);

        ItemStack potionItemStack = Ollivanders2API.getPotions().getPotionItemStackByType(O2PotionType.HERBICIDE_POTION, 1);
        assertNotNull(potionItemStack);

        ThrownPotion thrownPotion = testWorld.spawn(location, ThrownPotion.class);
        thrownPotion.setItem(potionItemStack);

        PotionSplashEvent event = new PotionSplashEvent(thrownPotion, new HashMap<>());
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);

        assertTrue(Ollivanders2API.getStationarySpells().checkLocationForStationarySpell(location, O2StationarySpellType.HERBICIDE), "Herbicide potion did not create herbicide stationary spell");
    }

    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
