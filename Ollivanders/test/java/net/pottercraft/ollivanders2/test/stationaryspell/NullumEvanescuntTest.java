package net.pottercraft.ollivanders2.test.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByCoordinatesEvent;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByNameEvent;
import net.pottercraft.ollivanders2.stationaryspell.NULLUM_APPAREBIT;
import net.pottercraft.ollivanders2.stationaryspell.NULLUM_EVANESCUNT;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the NULLUM_EVANESCUNT stationary spell.
 *
 * <p>Tests spell-specific behavior for the anti-disapparition barrier that prevents entities
 * from escaping the protected area through apparition or teleportation. Inherits common
 * stationary spell tests from the base class and adds tests for event handling specific to
 * this spell's escape-prevention mechanics.</p>
 *
 * @author Azami7
 */
public class NullumEvanescuntTest extends O2StationarySpellTest {
    /**
     * Gets the spell type being tested.
     *
     * @return {@link O2StationarySpellType#NULLUM_EVANESCUNT}
     */
    @Override
    O2StationarySpellType getSpellType() {
        return O2StationarySpellType.NULLUM_EVANESCUNT;
    }

    /**
     * Creates a NULLUM_EVANESCUNT spell instance for testing.
     *
     * <p>Constructs a new spell at the specified location with the minimum radius and duration values.</p>
     *
     * @param caster   the player casting the spell (not null)
     * @param location the center location of the spell (not null)
     * @return a new NULLUM_EVANESCUNT spell instance (not null)
     */
    @Override
    NULLUM_EVANESCUNT createStationarySpell(Player caster, Location location) {
        return new NULLUM_EVANESCUNT(testPlugin, caster.getUniqueId(), location, NULLUM_APPAREBIT.minRadiusConfig, NULLUM_APPAREBIT.minDurationConfig); // nullum evanescunt uses the radius and duration constants rom nullum apparebit
    }

    /**
     * Tests upkeep behavior (skipped - covered by base class tests).
     *
     * <p>The upkeep method only performs aging, which is already tested comprehensively by the inherited
     * ageAndKillTest() from the base test class.</p>
     */
    @Override
    @Test
    void upkeepTest() {
        // upkeep just calls age(), which is tested by ageAndKillTest()
    }

    /**
     * Tests apparate by name and coordinates event handling.
     *
     * <p>Verifies that players inside the spell area cannot apparate out by name or coordinates,
     * while players outside the spell area can freely apparate in. Confirms that both blocked and
     * allowed apparations are handled correctly, and that players receive feedback when their
     * escape attempts are prevented.</p>
     */
    @Test
    void doOnOllivandersApparateEventsTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 40, 100);
        Location outsideLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + NULLUM_APPAREBIT.maxRadiusConfig + 1);
        PlayerMock caster = mockServer.addPlayer();

        NULLUM_EVANESCUNT nullumEvanescunt = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(nullumEvanescunt);
        mockServer.getScheduler().performTicks(20);

        // player cannot apparate out of the spell area
        caster.setLocation(location);
        assertTrue(nullumEvanescunt.isLocationInside(caster.getLocation()));
        OllivandersApparateByNameEvent event = new OllivandersApparateByNameEvent(caster, outsideLocation, "outside");
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event.isCancelled(), "player allowed apparate out of spell area");

        // check player got feedback message
        mockServer.getScheduler().performTicks(200);
        String message = caster.nextMessage();
        assertNotNull(message, "Player did not get feedback message when trying to apparate out by name");
        assertEquals(NULLUM_EVANESCUNT.feedbackMessage, TestCommon.cleanChatMessage(message), "player did not get expected feedback message");

        OllivandersApparateByCoordinatesEvent event2 = new OllivandersApparateByCoordinatesEvent(caster, outsideLocation);
        mockServer.getPluginManager().callEvent(event2);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event2.isCancelled(), "player allowed apparate out of spell area");

        // check player got feedback message
        mockServer.getScheduler().performTicks(200);
        message = caster.nextMessage();
        assertNotNull(message, "Player did not get feedback message when trying to apparate out by coordinates");
        assertEquals(NULLUM_EVANESCUNT.feedbackMessage, TestCommon.cleanChatMessage(message), "player did not get expected feedback message");

        // player outside of area can apparate in
        caster.setLocation(outsideLocation);
        assertFalse(nullumEvanescunt.isLocationInside(caster.getLocation()));
        event = new OllivandersApparateByNameEvent(caster, location, "inside");
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.isCancelled(), "player outside spell area prevented from apparating in");

        event2 = new OllivandersApparateByCoordinatesEvent(caster, location);
        mockServer.getPluginManager().callEvent(event2);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event2.isCancelled(), "player outside spell area prevented from apparating in");
    }

    /**
     * Tests entity teleport event handling.
     *
     * <p>Verifies that non-player entities inside the spell area are prevented from teleporting out,
     * while entities outside the spell area can freely teleport into the protected area.</p>
     */
    @Test
    void doOnEntityTeleportEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 200, 40, 100);
        Location outsideLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + NULLUM_APPAREBIT.maxRadiusConfig + 1);
        PlayerMock caster = mockServer.addPlayer();

        NULLUM_EVANESCUNT nullumEvanescunt = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(nullumEvanescunt);
        mockServer.getScheduler().performTicks(20);

        // entity inside spell area cannot teleport out
        Rabbit rabbit = testWorld.spawn(location, Rabbit.class);
        assertTrue(nullumEvanescunt.isLocationInside(rabbit.getLocation()));
        EntityTeleportEvent event = new EntityTeleportEvent(rabbit, location, outsideLocation);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event.isCancelled(), "entity allowed to teleport out of spell area");

        // entity outside spell area can teleport in
        Rabbit rabbit2 = testWorld.spawn(outsideLocation, Rabbit.class);
        assertFalse(nullumEvanescunt.isLocationInside(rabbit2.getLocation()));
        event = new EntityTeleportEvent(rabbit2, outsideLocation, location);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.isCancelled(), "entity outside spell area prevented from teleporting in");
    }

    /**
     * Tests player teleport event handling.
     *
     * <p>Verifies that players inside the spell area are prevented from teleporting out and receive
     * feedback when their escape attempts are blocked, while players outside the spell area can
     * freely teleport into the protected area.</p>
     */
    @Test
    void doOnPlayerTeleportEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 300, 40, 100);
        Location outsideLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + NULLUM_APPAREBIT.maxRadiusConfig + 1);
        PlayerMock caster = mockServer.addPlayer();

        NULLUM_EVANESCUNT nullumEvanescunt = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(nullumEvanescunt);
        mockServer.getScheduler().performTicks(20);

        // player inside spell area cannot teleport out
        caster.setLocation(location);
        assertTrue(nullumEvanescunt.isLocationInside(caster.getLocation()));
        PlayerTeleportEvent event = new PlayerTeleportEvent(caster, location, outsideLocation);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event.isCancelled(), "player allowed to teleport out of spell area");

        // check player got feedback message
        mockServer.getScheduler().performTicks(200);
        String message = caster.nextMessage();
        assertNotNull(message, "Player did not get feedback message when trying to teleport out");
        assertEquals(NULLUM_EVANESCUNT.feedbackMessage, TestCommon.cleanChatMessage(message), "player did not get expected feedback message");

        // player outside spell area can teleport in
        caster.setLocation(outsideLocation);
        assertFalse(nullumEvanescunt.isLocationInside(caster.getLocation()));
        event = new PlayerTeleportEvent(caster, outsideLocation, location);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.isCancelled(), "player not allowed to teleport in to spell area");
    }
}
