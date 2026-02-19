package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
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

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HarmoniaNecterePassusTest {
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
     * <p>Loaded fresh before each test method with the default configuration. Provides access to
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

    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld("world1");
        Location location1 = new Location(testWorld, 100, 40, 100);
        Location location2 = new Location(testWorld, 120, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        net.pottercraft.ollivanders2.test.stationaryspell.HarmoniaNecterePassusTest.createCabinet(location1, location2);
        net.pottercraft.ollivanders2.test.stationaryspell.HarmoniaNecterePassusTest.createCabinet(location2, location1);

        Location castLocation = new Location(testWorld, 100, 39, 102);
        Location lookAt = new Location(testWorld, 100, 40, 101);
        caster.setLocation(castLocation);

        net.pottercraft.ollivanders2.spell.HARMONIA_NECTERE_PASSUS harmoniaSpell = new net.pottercraft.ollivanders2.spell.HARMONIA_NECTERE_PASSUS(testPlugin, caster, 1.0);
        harmoniaSpell.setVector((location1.toVector().subtract(lookAt.toVector())));
        Ollivanders2API.getSpells().addSpell(caster, harmoniaSpell);
        mockServer.getScheduler().performTicks(20);

        assertTrue(Ollivanders2API.getStationarySpells().checkLocationForStationarySpell(location1, O2StationarySpellType.HARMONIA_NECTERE_PASSUS));
        assertTrue(Ollivanders2API.getStationarySpells().checkLocationForStationarySpell(location2, O2StationarySpellType.HARMONIA_NECTERE_PASSUS));
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
