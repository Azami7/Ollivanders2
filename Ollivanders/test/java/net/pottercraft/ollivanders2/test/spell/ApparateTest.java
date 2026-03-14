package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.APPARATE;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByCoordinatesEvent;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByNameEvent;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockbukkit.mockbukkit.matcher.plugin.PluginManagerFiredEventClassMatcher.hasFiredEventInstance;

/**
 * Tests for the APPARATE spell.
 *
 * <p>Covers all apparation modes: line-of-sight, coordinates, and named locations.
 * Also tests location management (add, remove, list, save, load) and constraint validation
 * (distance limits, location name validation).</p>
 *
 * <p>Runs in isolation to prevent interference between parallel test runs, as tests
 * manipulate static apparate location state.</p>
 */
@Isolated
public class ApparateTest extends O2SpellTestSuper {
    /** {@inheritDoc} */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.APPARATE;
    }

    /**
     * Helper to create and register an APPARATE spell for testing.
     *
     * <p>Sets the caster to mastery level and adds the spell to the player's active spells.</p>
     *
     * @param caster the player casting the spell
     * @param words  the spell arguments
     * @return the created APPARATE spell instance
     */
    @NotNull
    APPARATE castSpell(@NotNull PlayerMock caster, String[] words) {
        TestCommon.setPlayerSpellExperience(testPlugin, caster, getSpellType(), O2Spell.spellMasteryLevel);

        APPARATE apparate = new APPARATE(testPlugin, caster, O2PlayerCommon.rightWand, words);
        assertNotNull(apparate, "Unable to create spell");

        Ollivanders2API.getSpells().addSpell(caster, apparate);

        return apparate;
    }

    /**
     * Tests that the APPARATE spell loads the maxApparateDistance config value on construction.
     */
    @Override
    @Test
    void spellConstructionTest() {
        mockServer.addSimpleWorld(getSpellType().getSpellName());
        PlayerMock caster = mockServer.addPlayer();

        APPARATE apparate = castSpell(caster, new String[]{});
        assertNotEquals(0, apparate.getMaxApparateDistance(), "failed to read maxApparateDistance config value");
    }

    /**
     * Comprehensive test covering all apparate modes and features.
     *
     * <p>Tests are grouped by functionality due to shared state (apparate locations are global).
     * Tests include:</p>
     *
     * <ul>
     * <li>Line-of-sight apparation</li>
     * <li>Coordinate-based apparation (valid and invalid)</li>
     * <li>Named location management (add, remove, clear, list)</li>
     * <li>Persistence (save and load)</li>
     * <li>Named location apparation</li>
     * <li>Distance limit validation</li>
     * </ul>
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld("apparate");
        Location location = new Location(testWorld, 100, 40, 100);
        PlayerMock caster = mockServer.addPlayer();
        caster.setLocation(location);

        String apparateString = "apparate";

        String[] words = new String[]{};

        // Test line-of-sight apparation
        Ollivanders2.apparateLocations = false;
        // apparate by coordinates, no coords, caster should get moved to a line-of-sight location
        APPARATE apparate = castSpell(caster, words);
        mockServer.getScheduler().performTicks(600);
        assertThat(mockServer.getPluginManager(), hasFiredEventInstance(OllivandersApparateByCoordinatesEvent.class));
        Location newLocation = caster.getLocation();
        assertNotEquals(location, newLocation, "player did not change location");
        assertTrue(location.distance(newLocation) <= apparate.getMaxLineOfSight(), "line of sight apparate exceeded max distance");

        // apparate by coord, invalid coords, caster goes to a line-of-sight location
        caster.setLocation(location);
        words = new String[]{apparateString, "100"};
        apparate = castSpell(caster, words);
        mockServer.getScheduler().performTicks(600);
        newLocation = caster.getLocation();
        assertNotEquals(location, newLocation, "player did not change location");
        assertTrue(location.distance(newLocation) <= apparate.getMaxLineOfSight(), "line of sight apparate exceeded max distance");

        // apparate by coord, valid coords
        caster.setLocation(location);
        Location hawaiiLocation = new Location(testWorld, 100, 40, 300);
        words = new String[]{apparateString, Double.toString(hawaiiLocation.getX()), Double.toString(hawaiiLocation.getY()), Double.toString(hawaiiLocation.getZ())};
        castSpell(caster, words);
        mockServer.getScheduler().performTicks(600);
        newLocation = caster.getLocation();
        assertEquals(hawaiiLocation, newLocation, "caster is not at expected destination");

        // using apparate location with coords will fail
        Ollivanders2.apparateLocations = true;

        caster.setLocation(location);
        castSpell(caster, words);
        mockServer.getScheduler().performTicks(600);
        newLocation = caster.getLocation();
        assertEquals(location, newLocation, "caster location changed when coords were specified instead of location name");
        String message = caster.nextMessage();
        assertNotNull(message, "caster did not receive failure message");

        // add apparate locations
        APPARATE.addLocation("", testWorld, 100, 40, 300);
        assertTrue(APPARATE.getAllApparateLocations().isEmpty(), "added apparate location with empty name");
        String origin = "Origin";
        APPARATE.addLocation(origin, testWorld, location.getX(), location.getY(), location.getZ());
        assertEquals(1, APPARATE.getAllApparateLocations().size(), "failed to add origin apparate location");

        String hawaii = "Hawaii";
        APPARATE.addLocation(hawaii, testWorld, hawaiiLocation.getX(), hawaiiLocation.getY(), hawaiiLocation.getZ());

        // list apparate locations
        TestCommon.clearMessageQueue(caster);
        APPARATE.listApparateLocations(caster);
        mockServer.getScheduler().performTicks(5);
        message = TestCommon.getWholeMessage(caster);
        assertNotNull(message, "caster did not get apparate locations list");
        assertTrue(message.contains(origin.toLowerCase()), "apparate locations did not contain " + origin.toLowerCase());
        assertTrue(message.contains(hawaii.toLowerCase()), "apparate locations did not contain " + hawaii.toLowerCase());

        // remove apparate locations
        APPARATE.removeLocation(hawaii);
        assertEquals(1, APPARATE.getAllApparateLocations().size(), "failed to remove apparate location");
        assertFalse(APPARATE.doesLocationExist(hawaii), "Hawaii location not removed");
        assertTrue(APPARATE.doesLocationExist(origin), "Origin location removed");

        // clear apparate locations
        APPARATE.clearApparateLocations();
        assertTrue(APPARATE.getAllApparateLocations().isEmpty(), "Clear did not remove all apparate locations");

        // save & load apparate locations
        APPARATE.addLocation(origin, testWorld, location.getX(), location.getY(), location.getZ());
        APPARATE.addLocation(hawaii, testWorld, hawaiiLocation.getX(), hawaiiLocation.getY(), hawaiiLocation.getZ());
        APPARATE.saveApparateLocations();
        mockServer.getScheduler().performTicks(5);

        APPARATE.clearApparateLocations();
        assertTrue(APPARATE.getAllApparateLocations().isEmpty());
        APPARATE.loadApparateLocations(testPlugin);
        assertFalse(APPARATE.getAllApparateLocations().isEmpty(), "failed to load saved apparate locations");
        assertTrue(APPARATE.doesLocationExist(hawaii), "Hawaii location not saved/loaded");
        assertTrue(APPARATE.doesLocationExist(origin), "Origin location not saved/loaded");

        // apparate by name
        caster.setLocation(location);
        words = new String[]{apparateString, origin};
        castSpell(caster, words);
        mockServer.getScheduler().performTicks(600);
        newLocation = caster.getLocation();
        assertEquals(location, newLocation, "caster teleporting to their current location was moved");

        words = new String[]{apparateString, hawaii};
        castSpell(caster, words);
        mockServer.getScheduler().performTicks(600);
        newLocation = caster.getLocation();
        assertThat(mockServer.getPluginManager(), hasFiredEventInstance(OllivandersApparateByNameEvent.class));
        assertEquals(hawaiiLocation, newLocation, "caster not at expected location");

        // apparate by name, invalid name
        caster.setLocation(location);
        TestCommon.clearMessageQueue(caster);
        words = new String[]{apparateString, "Foo"};
        castSpell(caster, words);
        mockServer.getScheduler().performTicks(600);
        newLocation = caster.getLocation();
        assertEquals(location, newLocation, "caster teleported when location name was not valid");
        message = caster.nextMessage();
        assertNotNull(message, "caster did not get failure message");

        // apparate > max distance
        caster.setLocation(location);
        TestCommon.clearMessageQueue(caster);
        Location fijiLocation = new Location(testWorld, location.getX(), location.getY(), location.getZ() + APPARATE.getMaxApparateDistance() + 1);
        String fiji = "Fiji";
        APPARATE.addLocation(fiji, testWorld, fijiLocation.getX(), fijiLocation.getY(), fijiLocation.getZ());
        words = new String[]{apparateString, fiji};
        castSpell(caster, words);
        mockServer.getScheduler().performTicks(600);
        newLocation = caster.getLocation();
        assertEquals(location, newLocation, "caster apparated beyond max apparate distance");
        message = caster.nextMessage();
        assertNotNull(message, "caster did not receive failure message");

        // canApparateFrom and canApparateTo cannot be tested until we mock world guard
    }

    /**
     * {@inheritDoc}
     *
     * <p>No state changes to revert for this spell.</p>
     */
    @Override
    @Test
    void revertTest() {
    }
}
