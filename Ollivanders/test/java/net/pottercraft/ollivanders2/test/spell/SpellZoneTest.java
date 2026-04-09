package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.SpellZone;
import net.pottercraft.ollivanders2.spell.SpellZone.SpellZoneType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link SpellZone}.
 *
 * <p>SpellZone is a value-object configuration class with no game-state side effects, so each
 * scenario is independent and lives in its own {@code @Test} method. Tests cover:
 * <ul>
 * <li><strong>Construction:</strong> Each zone type stores name, world, type, and spell lists correctly</li>
 * <li><strong>Cuboid wiring:</strong> CUBOID zones receive a working cuboid built from the area array</li>
 * <li><strong>Defensive copies:</strong> Caller-supplied lists cannot be mutated through the SpellZone</li>
 * <li><strong>Empty lists:</strong> Zones constructed with empty allow/disallow lists work without error</li>
 * </ul>
 *
 * @author Azami7
 */
public class SpellZoneTest {
    /**
     * Name of the shared test world. Passed to {@link SpellZone}'s {@code world} parameter and to
     * {@link org.mockbukkit.mockbukkit.ServerMock#addSimpleWorld(String)}.
     */
    static final String worldName = "SpellZone";

    /**
     * The shared MockBukkit world used by tests that need a {@link Location} for cuboid
     * containment checks.
     */
    static World testWorld;

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
     * <p>Loaded before all test methods with the default configuration. Provides access to
     * logger, scheduler, and other plugin API methods during tests.</p>
     */
    static Ollivanders2 testPlugin;

    /**
     * Initialize the mock Bukkit server, plugin, and shared test world before all tests.
     *
     * <p>Static setup method called once before all tests in this class. Creates the shared
     * MockBukkit server, loads the Ollivanders2 plugin with the default test config, adds the
     * shared {@link #testWorld}, and advances the scheduler past the plugin's startup delay.
     * The same server, plugin, and world instances are reused across all test methods to
     * avoid expensive setup/teardown per method.</p>
     */
    @BeforeAll
    static void globalSetUp() {
        Ollivanders2.testMode = true;

        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));
        testWorld = mockServer.addSimpleWorld(worldName);

        // advance the server by 20 ticks to let the scheduler start (it has an initial delay of 20 ticks)
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    /**
     * Tests construction of a CUBOID zone.
     *
     * <p>Verifies that:
     * <ul>
     * <li>Name, type, world name, allowed list, and disallowed list are stored as provided</li>
     * <li>The backing {@link net.pottercraft.ollivanders2.common.Cuboid} reports points inside
     *     the supplied area as inside</li>
     * <li>The backing cuboid reports points outside the supplied area as outside</li>
     * </ul>
     */
    @Test
    void cuboidZoneConstructorTest() {
        int[] area = {0, 0, 0, 100, 100, 100};
        ArrayList<O2SpellType> allowed = new ArrayList<>();
        allowed.add(O2SpellType.PACK);
        ArrayList<O2SpellType> disallowed = new ArrayList<>();
        disallowed.add(O2SpellType.BOMBARDA);

        SpellZone zone = new SpellZone("testCuboid", worldName, SpellZoneType.CUBOID, area, allowed, disallowed);

        assertEquals("testCuboid", zone.getZoneName(), "zone name not stored correctly");
        assertEquals(SpellZoneType.CUBOID, zone.getZoneType(), "zone type not stored correctly");
        assertEquals(worldName, zone.getZoneWorldName(), "zone world name not stored correctly");
        assertEquals(allowed, zone.getAllowedSpells(), "allowed spells not stored correctly");
        assertEquals(disallowed, zone.getDisallowedSpells(), "disallowed spells not stored correctly");

        assertNotNull(zone.getCuboid(), "cuboid not constructed for CUBOID zone");
        assertTrue(zone.getCuboid().isInside(new Location(testWorld, 50, 50, 50)),
                "cuboid should contain a point inside the supplied area");
        assertFalse(zone.getCuboid().isInside(new Location(testWorld, 200, 200, 200)),
                "cuboid should not contain a point outside the supplied area");
    }

    /**
     * Tests construction of a WORLD zone.
     *
     * <p>Verifies that name, type, world name, and spell lists are stored as provided. The cuboid
     * field is constructed as a stub for non-CUBOID zones (current implementation detail) but is
     * not queried — the consumer filters by {@link SpellZoneType} before touching it.
     */
    @Test
    void worldZoneConstructorTest() {
        ArrayList<O2SpellType> allowed = new ArrayList<>();
        allowed.add(O2SpellType.PACK);
        allowed.add(O2SpellType.BOMBARDA);
        ArrayList<O2SpellType> disallowed = new ArrayList<>();

        // area is unused for WORLD zones; an empty stub is passed in production
        int[] area = {0, 0, 0, 0, 0, 0};
        SpellZone zone = new SpellZone("testWorld", worldName, SpellZoneType.WORLD, area, allowed, disallowed);

        assertEquals("testWorld", zone.getZoneName(), "zone name not stored correctly");
        assertEquals(SpellZoneType.WORLD, zone.getZoneType(), "zone type not stored correctly");
        assertEquals(worldName, zone.getZoneWorldName(), "zone world name not stored correctly");
        assertEquals(allowed, zone.getAllowedSpells(), "allowed spells not stored correctly");
        assertEquals(disallowed, zone.getDisallowedSpells(), "disallowed spells not stored correctly");
    }

    /**
     * Tests construction of a WORLD_GUARD zone.
     *
     * <p>Verifies that name, type, world name, and spell lists are stored as provided. As with
     * WORLD zones, the cuboid field is a stub and is not queried.
     */
    @Test
    void worldGuardZoneConstructorTest() {
        ArrayList<O2SpellType> allowed = new ArrayList<>();
        ArrayList<O2SpellType> disallowed = new ArrayList<>();
        disallowed.add(O2SpellType.PACK);

        int[] area = {0, 0, 0, 0, 0, 0};
        SpellZone zone = new SpellZone("testRegion", worldName, SpellZoneType.WORLD_GUARD, area, allowed, disallowed);

        assertEquals("testRegion", zone.getZoneName(), "zone name not stored correctly");
        assertEquals(SpellZoneType.WORLD_GUARD, zone.getZoneType(), "zone type not stored correctly");
        assertEquals(worldName, zone.getZoneWorldName(), "zone world name not stored correctly");
        assertEquals(allowed, zone.getAllowedSpells(), "allowed spells not stored correctly");
        assertEquals(disallowed, zone.getDisallowedSpells(), "disallowed spells not stored correctly");
    }

    /**
     * Tests that the constructor makes defensive copies of the allowed and disallowed lists.
     *
     * <p>Verifies that:
     * <ul>
     * <li>Mutating the caller's allowed list after construction does not affect the zone's stored list</li>
     * <li>Mutating the caller's disallowed list after construction does not affect the zone's stored list</li>
     * <li>The getters also return copies — mutating a returned list does not affect the next call</li>
     * </ul>
     *
     * <p>Without defensive copies, a caller (or a buggy test) could silently corrupt zone state
     * after construction.
     */
    @Test
    void defensiveCopyTest() {
        ArrayList<O2SpellType> allowed = new ArrayList<>();
        allowed.add(O2SpellType.PACK);
        ArrayList<O2SpellType> disallowed = new ArrayList<>();
        disallowed.add(O2SpellType.BOMBARDA);

        int[] area = {0, 0, 0, 0, 0, 0};
        SpellZone zone = new SpellZone("defensiveCopy", worldName, SpellZoneType.WORLD, area, allowed, disallowed);

        // mutate the caller's lists after construction — must not affect the zone's stored copies
        allowed.clear();
        disallowed.clear();

        assertEquals(1, zone.getAllowedSpells().size(), "zone's allowed list was aliased to caller list");
        assertEquals(O2SpellType.PACK, zone.getAllowedSpells().getFirst(), "zone's allowed list was mutated by caller");
        assertEquals(1, zone.getDisallowedSpells().size(), "zone's disallowed list was aliased to caller list");
        assertEquals(O2SpellType.BOMBARDA, zone.getDisallowedSpells().getFirst(), "zone's disallowed list was mutated by caller");

        // mutate the getter result — must not affect the next getter call
        List<O2SpellType> firstAllowed = zone.getAllowedSpells();
        firstAllowed.clear();
        assertEquals(1, zone.getAllowedSpells().size(), "getAllowedSpells() returned a live reference instead of a copy");
        assertNotSame(firstAllowed, zone.getAllowedSpells(), "getAllowedSpells() returned the same list reference twice");
    }

    /**
     * Tests construction with empty allow and disallow lists.
     *
     * <p>Verifies that a zone can be constructed with no spell restrictions and the getters
     * return empty (not null) lists.
     */
    @Test
    void emptySpellListsTest() {
        int[] area = {0, 0, 0, 0, 0, 0};
        SpellZone zone = new SpellZone("empty", worldName, SpellZoneType.WORLD, area, new ArrayList<>(), new ArrayList<>());

        assertNotNull(zone.getAllowedSpells(), "allowed spells getter returned null");
        assertNotNull(zone.getDisallowedSpells(), "disallowed spells getter returned null");
        assertTrue(zone.getAllowedSpells().isEmpty(), "allowed spells should be empty");
        assertTrue(zone.getDisallowedSpells().isEmpty(), "disallowed spells should be empty");
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
