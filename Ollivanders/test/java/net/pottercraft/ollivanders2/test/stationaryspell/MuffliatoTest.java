package net.pottercraft.ollivanders2.test.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.stationaryspell.MUFFLIATO;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the {@link MUFFLIATO} stationary spell.
 *
 * <p>Tests the muffliato charm spell, which prevents players outside the spell area from hearing
 * conversations occurring inside the protected area. Inherits common spell tests from
 * {@link O2StationarySpellTest} and provides spell-specific factory methods for test setup.</p>
 *
 * <p>The test verifies:
 * <ul>
 *   <li>Outside players cannot hear inside conversations</li>
 *   <li>Inside players can hear outside conversations</li>
 *   <li>Inside players can hear inside conversations</li>
 *   <li>Outside players can hear outside conversations</li>
 * </ul>
 * </p>
 *
 * @author Azami7
 */
public class MuffliatoTest extends O2StationarySpellTest {
    /**
     * Gets the spell type being tested.
     *
     * @return {@link O2StationarySpellType#MUFFLIATO}
     */
    @Override
    O2StationarySpellType getSpellType() {
        return O2StationarySpellType.MUFFLIATO;
    }

    /**
     * Creates a MUFFLIATO spell instance for testing.
     *
     * <p>Constructs a new muffliato spell at the specified location with the test's configured
     * radius and duration values (minimum values by default).</p>
     *
     * @param caster   the player casting the spell (not null)
     * @param location the center location of the spell (not null)
     * @return a new MUFFLIATO spell instance (not null)
     */
    @Override
    MUFFLIATO createStationarySpell(Player caster, Location location) {
        return new MUFFLIATO(testPlugin, caster.getUniqueId(), location, MUFFLIATO.minRadiusConfig, MUFFLIATO.minDurationConfig);
    }

    /**
     * Tests muffliato upkeep behavior (skipped - covered by base class tests).
     *
     * <p>The muffliato spell's upkeep method only performs aging, which is already tested
     * comprehensively by the inherited ageAndKillTest() from the base test class.</p>
     */
    @Override @Test
    void upkeepTest() {
        // upkeep only does age, which is covered by ageAndKillTest()
    }

    /**
     * Tests muffliato chat filtering behavior across all player location combinations.
     *
     * <p>Verifies that the spell properly filters chat recipients based on player locations:
     * <ul>
     *   <li>Inside speaker → outside recipient: recipient is removed (chat is muffled)</li>
     *   <li>Outside speaker → inside recipient: recipient is kept (inside can hear outside)</li>
     *   <li>Inside speaker → inside recipient: recipient is kept (inside hears inside)</li>
     *   <li>Outside speaker → outside recipient: recipient is kept (outside hears outside)</li>
     * </ul>
     * </p>
     */
    @Test
    void doOnAsyncPlayerChatEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 4, 100);
        Location outsideLocation = new Location(location.getWorld(), location.getX() + MUFFLIATO.maxRadiusConfig + 1, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();
        PlayerMock player = mockServer.addPlayer();

        MUFFLIATO muffliato = createStationarySpell(caster, location);
        Ollivanders2API.getStationarySpells().addStationarySpell(muffliato);
        mockServer.getScheduler().performTicks(20);

        // player outside spell area cannot see chats from inside spell area
        assertFalse(muffliato.isLocationInside(outsideLocation));
        caster.setLocation(location);
        player.setLocation(outsideLocation);
        HashSet<Player> recipients = new HashSet<>() {{
           add(player);
        }};
        String chat = "test chat 1";
        AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(false, caster, chat, recipients);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertTrue(event.getRecipients().isEmpty(), "Chat recipient outside muffliato not removed from chat inside muffliato");

        // player inside muffliato can see chats from outside
        recipients = new HashSet<>() {{
            add(caster);
        }};
        event = new AsyncPlayerChatEvent(false, player, chat, recipients);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.getRecipients().isEmpty(), "Chat recipient inside muffliato removed from chat outside muffliato");

        // player inside muffliato can see chats from inside
        player.setLocation(location);
        recipients = new HashSet<>() {{
            add(player);
        }};
        event = new AsyncPlayerChatEvent(false, caster, chat, recipients);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.getRecipients().isEmpty(), "Chat recipient inside muffliato removed from chat inside muffliato");

        // both player outside muffliato can see chats from outside
        player.setLocation(outsideLocation);
        caster.setLocation(outsideLocation);
        recipients = new HashSet<>() {{
            add(player);
        }};
        event = new AsyncPlayerChatEvent(false, caster, chat, recipients);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);
        assertFalse(event.getRecipients().isEmpty(), "Chat recipient outside muffliato removed from chat outside muffliato");
    }
}
