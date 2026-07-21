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
 * Base test class for concealment shield spells, holding the tests common to that family: upkeep aging, targeting
 * prevention, visibility toggling, entry restrictions, chat filtering, proximity alarms, and cleanup. Subclasses
 * provide the spell factory and proximity-alarm assertion.
 */
abstract public class ConcealmentShieldSpellTest extends O2StationarySpellTest {
    /**
     * Radius passed to the spell factory in these tests, in blocks.
     */
    int defaultRadius = 10;

    /**
     * Duration passed to the spell factory in these tests, in ticks.
     */
    int defaultDuration = 1000;

    /**
     * Build a spell instance to test at the default radius and duration.
     *
     * @param caster   the player casting the spell
     * @param location the center location for the spell
     * @return a new concealment shield spell instance
     */
    abstract ConcealmentShieldSpell createStationarySpell(Player caster, Location location);

    /**
     * Upkeep decrements the duration by one tick and kills the spell when it reaches zero.
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
     * An outside entity's attempt to target a player inside the area is cancelled but targeting a player outside is
     * allowed. Skipped for spells that permit targeting into the area.
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
     * Visibility updates correctly as the players move across the boundary: an outsider cannot see a player concealed
     * inside, while a player inside the area can see everyone, across all inside/outside combinations.
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
     * Verify this spell's own visibility rule, e.g. which kinds of outside entity can see players inside the area.
     */
    @Test
    abstract void doOnEntityTargetEventTestVisibilityCheck();

    /**
     * Proximity detection distinguishes locations inside and outside the proximity radius, and moving into range fires
     * the alarm (via {@link #checkProximityAlarm}) for spells that have one and stays silent for those that do not.
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
     * Assert this spell's proximity-alarm behavior; called only for spells that have an alarm.
     *
     * @param caster                 the player who cast the spell, inside the area
     * @param concealmentShieldSpell the active spell to check
     */
    abstract void checkProximityAlarm(PlayerMock caster, ConcealmentShieldSpell concealmentShieldSpell);

    /**
     * Chat from a player inside the area does not reach a recipient outside it.
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
     * A player joining while the spell is active immediately sees the correct visibility: a player concealed inside the
     * area is hidden from them without any movement event.
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
     * When the spell expires, a player who was concealed inside the area becomes visible again.
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
