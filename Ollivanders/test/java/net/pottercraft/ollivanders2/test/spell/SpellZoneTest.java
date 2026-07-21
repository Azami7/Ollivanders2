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
 * Unit tests for {@link SpellZone}. It is a side-effect-free value object, so each scenario is independent and lives
 * in its own {@code @Test} method.
 *
 * @author Azami7
 */
public class SpellZoneTest {
    /**
     * Name of the shared test world, used for both the zone's world parameter and the MockBukkit world.
     */
    static final String worldName = "SpellZone";

    /**
     * The shared MockBukkit world used by tests that need a {@link Location} for cuboid containment checks.
     */
    static World testWorld;

    /**
     * Shared MockBukkit server, created once for all tests in the class.
     */
    static ServerMock mockServer;

    /**
     * The plugin under test, loaded once with the default config.
     */
    static Ollivanders2 testPlugin;

    /**
     * Start the shared MockBukkit server, load the plugin, and add the shared test world before any tests run.
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
     * Verify a CUBOID zone stores its fields as provided and builds a working cuboid that reports points inside and
     * outside the supplied area correctly.
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
     * Verify a WORLD zone stores its fields as provided. Its cuboid is only a stub, but the consumer filters by
     * {@link SpellZoneType} before touching it.
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
     * Verify a WORLD_GUARD zone stores its fields as provided. As with WORLD zones its cuboid is only a stub.
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
     * Verify the zone defends its spell lists both ways: mutating the caller's lists after construction does not
     * change the zone, and the getters each return a fresh copy. Without this a caller could silently corrupt zone
     * state.
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
     * Verify a zone built with empty allow and disallow lists constructs cleanly and its getters return empty,
     * non-null lists.
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
     * Stop the MockBukkit server after all tests in the class complete.
     */
    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
