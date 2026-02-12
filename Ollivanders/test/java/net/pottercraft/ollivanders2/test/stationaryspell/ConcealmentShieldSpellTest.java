package net.pottercraft.ollivanders2.test.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.stationaryspell.ConcealmentShieldSpell;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Abstract test suite for concealment shield spell implementations.
 *
 * <p>Provides common test cases for all concealment shield spell types. Tests verify core behaviors
 * including spell lifecycle (upkeep), entity targeting prevention, visibility toggling, area entry
 * restrictions, chat filtering, proximity alarms, and cleanup effects. Subclasses implement
 * spell-specific factory methods and proximity alarm verification.</p>
 */
abstract public class ConcealmentShieldSpellTest extends O2StationarySpellTest {
    /**
     * Default spell radius for test instances (10 blocks).
     * Used by createStationarySpell() implementations as the initial radius.
     */
    int defaultRadius = 10;

    /**
     * Default spell duration for test instances (1000 ticks).
     * Used by createStationarySpell() implementations as the initial duration.
     */
    int defaultDuration = 1000;

    /**
     * Creates a concealment shield spell instance for testing.
     *
     * <p>Subclasses implement this to create their specific spell type with default radius
     * and duration values at the specified location.</p>
     *
     * @param caster   the player casting the spell (not null)
     * @param location the center location for the spell (not null)
     * @return a new concealment shield spell instance of the subclass type
     */
    abstract ConcealmentShieldSpell createStationarySpell(Player caster, Location location);

    /**
     * Tests the spell lifecycle during upkeep (aging and death).
     *
     * <p>Verifies that:
     * <ul>
     *   <li>Calling upkeep() decrements the spell duration by 1</li>
     *   <li>The spell remains alive until duration reaches 0</li>
     *   <li>The spell is killed when upkeep() is called at duration 1</li>
     * </ul>
     * </p>
     */
    @Override @Test
    void upkeepTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 200, 4, 100);
        PlayerMock player = mockServer.addPlayer();
        ConcealmentShieldSpell stationarySpell = createStationarySpell(player, location);

        int duration = stationarySpell.getDuration();
        stationarySpell.upkeep();
        assertEquals(duration - 1, stationarySpell.getDuration(), "");

        duration = stationarySpell.getDuration();
        stationarySpell.age(duration - 1);
        assertFalse(stationarySpell.isKilled(), "");
        stationarySpell.upkeep();
        assertTrue(stationarySpell.isKilled(), "");
    }

    /**
     * Tests entity targeting prevention for spells that block targeting.
     *
     * <p>Verifies that:
     * <ul>
     *   <li>Entities outside the spell area cannot target players inside (event cancelled)</li>
     *   <li>Entities outside the spell area can target players outside (event not cancelled)</li>
     *   <li>The test skips for spell types that allow unrestricted targeting (canTarget returns true)</li>
     * </ul>
     * </p>
     */
    @Test
    void doOnEntityTargetEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 4, 100);
        PlayerMock caster = mockServer.addPlayer();

        // create concealment shield spell
        ConcealmentShieldSpell stationarySpell = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(stationarySpell);

        // set player inside the spell radius
        caster.setLocation(location);

        // spawn skeleton outside the spell radius
        Location outsideLocation = new Location(location.getWorld(), location.getX() + stationarySpell.getMaxRadius() + 1, location.getY(), location.getZ());
        Skeleton skeleton = testWorld.spawn(outsideLocation, Skeleton.class);

        // this spell type allows targeting inside the area, skip this test
        if (stationarySpell.canTarget(skeleton))
            return;

        // skeleton target the player inside the spell radius
        EntityTargetEvent event = new EntityTargetEvent(skeleton, caster, EntityTargetEvent.TargetReason.CLOSEST_ENTITY);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event.isCancelled(), "");

        // move the player outside the spell radius, skeleton target the player
        caster.setLocation(outsideLocation);
        event = new EntityTargetEvent(skeleton, caster, EntityTargetEvent.TargetReason.CLOSEST_ENTITY);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.isCancelled(), "");
    }

    /**
     * Tests visibility toggling as players move across spell boundaries.
     *
     * <p>Verifies that concealment rules are properly applied and updated when players move relative
     * to the spell area. Tests all four scenarios:
     * <ul>
     *   <li>Player outside can't see caster inside; caster inside can see player outside</li>
     *   <li>Both inside: both can see each other</li>
     *   <li>Caster exits: player inside can still see caster outside; caster outside can't see player inside</li>
     *   <li>Both outside: both can see each other</li>
     * </ul>
     * </p>
     */
    @Test
    void doOnPlayerMoveEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 4, 100);
        PlayerMock caster = mockServer.addPlayer();
        PlayerMock player = mockServer.addPlayer();

        caster.setLocation(location);
        ConcealmentShieldSpell concealmentShieldSpell = createStationarySpell(caster, location);
        Location outsideSpellLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + concealmentShieldSpell.getProximityRadius() + 1);
        player.setLocation(outsideSpellLocation); // player just outside proximity radius

        Ollivanders2API.getStationarySpells().addStationarySpell(concealmentShieldSpell);
        mockServer.getScheduler().performTicks(20);
        // player cannot see caster in the spell area but caster can see player outside
        assertFalse(player.canSee(caster), "Player outside spell area can see caster inside the spell area");
        assertTrue(caster.canSee(player), "Caster inside the spell area cannot see player outside the spell area");

        player.setLocation(location); // move player in to the spell area
        PlayerMoveEvent event = new PlayerMoveEvent(player, outsideSpellLocation, location);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);

        // player can now see caster in the spell area and caster can still see player
        assertTrue(player.canSee(caster), "Player inside the spell area cannot see caster inside the spell area");
        assertTrue(caster.canSee(player), "Caster inside the spell area cannot see player inside the spell area");

        caster.setLocation(outsideSpellLocation);
        event = new PlayerMoveEvent(caster, location, outsideSpellLocation);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);

        // player can still see caster now outside the spell area but caster cannot see player inside
        assertTrue(player.canSee(caster), "Player inside the spell area cannot see caster outside the spell area");
        assertFalse(caster.canSee(player), "Caster outside the spell area can see player inside the spell area");

        player.setLocation(outsideSpellLocation);
        event = new PlayerMoveEvent(player, location, outsideSpellLocation);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);

        // caster can now see player outside the spell area, player can still see caster
        assertTrue(player.canSee(caster), "Player outside the spell area cannot see caster outside the spell area");
        assertTrue(caster.canSee(player), "Caster outside the spell area cannot see player outside the spell area");
    }

    /**
     * Tests spell-specific visibility logic from the perspective of outside entities.
     *
     * <p>Subclasses implement this to verify spell-specific visibility rules, such as which types
     * of entities (muggles, non-magical beings, etc.) can or cannot see concealed players inside
     * the protected area.</p>
     */
    @Test
    abstract void doOnEntityTargetEventTestVisibilityCheck();

    /**
     * Tests proximity alarm functionality when entities approach the spell boundary.
     *
     * <p>Verifies that:
     * <ul>
     *   <li>Proximity detection correctly identifies locations inside and outside the proximity radius</li>
     *   <li>When an entity moves into the proximity alarm range, appropriate alarm actions occur</li>
     *   <li>For spells with proximity alarms enabled, checkProximityAlarm() is invoked</li>
     *   <li>For spells without proximity alarms, no messages are sent to the caster</li>
     * </ul>
     * </p>
     */
    @Test
    void doOnPlayerMoveEventTestProximityCheck() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 4, 100);
        PlayerMock caster = mockServer.addPlayer();
        caster.setLocation(location);

        // create concealment shield spell
        ConcealmentShieldSpell concealmentShieldSpell = createStationarySpell(caster, location);
        Location insideProximityLocation = new Location(testWorld, location.getX() + (concealmentShieldSpell.getProximityRadius() - 1), location.getY(), location.getZ());
        Location outsideProximityLocation = new Location(testWorld, location.getX() + (concealmentShieldSpell.getProximityRadius() + 1), location.getY(), location.getZ());

        // create a player outside the spell, near the proximity boundary
        PlayerMock player = mockServer.addPlayer();
        player.setLocation(outsideProximityLocation);

        Ollivanders2API.getStationarySpells().addStationarySpell(concealmentShieldSpell);
        mockServer.getScheduler().performTicks(20);

        assertTrue(concealmentShieldSpell.isInProximity(insideProximityLocation), "insideProximityLocation is not within the proximity area");
        assertFalse(concealmentShieldSpell.isInProximity(outsideProximityLocation), "outsideProximityLocation is inside the proximity area");

        // move player to proximity boundary
        player.setLocation(outsideProximityLocation);
        PlayerMoveEvent event = new PlayerMoveEvent(player, outsideProximityLocation, insideProximityLocation);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(Ollivanders2Common.ticksPerSecond * 2);

        if (concealmentShieldSpell.doesAlarmOnProximty()) {
            // spell has proximity alarms
            checkProximityAlarm(caster, concealmentShieldSpell);
        }
        else {
            // spell does not send proximity alarm messages so caster should not have received any alarm message about the muggle approaching
            assertNull(caster.nextMessage(), "Caster received unexpected proximity alarm message");
        }
    }

    /**
     * Helper method for subclasses to verify spell-specific proximity alarm behavior.
     *
     * <p>Called by doOnPlayerMoveEventTestProximityCheck() when a spell has proximity alarms enabled.
     * Subclasses implement this to verify that appropriate alarm messages are sent or other
     * alarm actions are triggered.</p>
     *
     * @param caster                   the player who cast the spell (inside the spell area)
     * @param concealmentShieldSpell   the active concealment spell to check alarm behavior on
     */
    abstract void checkProximityAlarm(PlayerMock caster, ConcealmentShieldSpell concealmentShieldSpell);

    /**
     * Tests that chat from inside the spell area is blocked from reaching outside recipients.
     *
     * <p>Verifies that:
     * <ul>
     *   <li>A player outside the spell is initially in the chat recipients list</li>
     *   <li>When the caster inside sends chat, the outside player is removed from recipients</li>
     *   <li>The outside player never receives the chat message</li>
     *   <li>Conversation within the spell area is concealed from outsiders</li>
     * </ul>
     * </p>
     */
    @Test
    void doOnAsyncPlayerChatEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 4, 100);
        PlayerMock caster = mockServer.addPlayer();
        PlayerMock player = mockServer.addPlayer();
        caster.setLocation(location);

        ConcealmentShieldSpell concealmentShieldSpell = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(concealmentShieldSpell);
        mockServer.getScheduler().performTicks(20);

        Location outsideLocation = new Location(location.getWorld(), (location.getX() + concealmentShieldSpell.getMaxRadius() + 1), location.getY(), location.getZ());
        player.setLocation(outsideLocation);

        String chat = "test chat";
        HashSet<Player> recipients = new HashSet<>() {{
            add(player);
        }};

        caster.sendMessage(chat);
        AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(false, caster, chat, recipients);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);

        assertTrue(event.getRecipients().isEmpty(), "event.getRecipients() is not empty");
        assertNull(player.nextMessage(), "Player saw chat when speaker inside spell area");
    }

    /**
     * Tests visibility initialization for newly joined players.
     *
     * <p>Verifies that:
     * <ul>
     *   <li>When a player joins the server while a spell is active, they immediately have correct visibility</li>
     *   <li>Players inside the spell area are correctly hidden from the newly joined player based on spell rules</li>
     *   <li>The newly joined player can see the caster when appropriate (e.g., outside the spell area)</li>
     *   <li>Visibility is properly initialized without requiring movement events</li>
     * </ul>
     * </p>
     */
    @Test
    void doOnPlayerJoinEvent() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 4, 100);
        PlayerMock caster = mockServer.addPlayer();
        caster.setLocation(location);

        ConcealmentShieldSpell concealmentShieldSpell = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(concealmentShieldSpell);
        mockServer.getScheduler().performTicks(20);

        PlayerMock player = mockServer.addPlayer();
        assertFalse(player.canSee(caster), "Caster in spell area not hidden from player outside at join");
        assertTrue(caster.canSee(player), "Player hidden from caster on join");
    }

    /**
     * Tests cleanup of concealment effects when the spell expires.
     *
     * <p>Verifies that:
     * <ul>
     *   <li>Players inside the spell are initially hidden from outside players</li>
     *   <li>When the spell expires after its duration elapses, all concealment effects are removed</li>
     *   <li>Previously hidden players become visible again to all other players</li>
     *   <li>The spell's visibility effects are properly reversed on cleanup</li>
     * </ul>
     * </p>
     */
    @Test
    void doCleanUp() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 4, 100);
        PlayerMock caster = mockServer.addPlayer();
        PlayerMock player = mockServer.addPlayer();
        caster.setLocation(location);

        ConcealmentShieldSpell concealmentShieldSpell = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(concealmentShieldSpell);
        mockServer.getScheduler().performTicks(20);
        assertFalse(player.canSee(caster));

        mockServer.getScheduler().performTicks(defaultDuration);
        assertTrue(player.canSee(caster), "After spell ended, player still cannot see caster");
    }
}
