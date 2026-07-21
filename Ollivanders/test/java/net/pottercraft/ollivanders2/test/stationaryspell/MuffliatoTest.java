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
 * Unit tests for {@link MUFFLIATO}. Extends {@link O2StationarySpellTest} for the shared stationary-spell tests.
 *
 * @author Azami7
 */
public class MuffliatoTest extends O2StationarySpellTest {
    @Override
    O2StationarySpellType getSpellType() {
        return O2StationarySpellType.MUFFLIATO;
    }

    @Override
    MUFFLIATO createStationarySpell(Player caster, Location location) {
        return new MUFFLIATO(testPlugin, caster.getUniqueId(), location, MUFFLIATO.minRadiusConfig, MUFFLIATO.minDurationConfig);
    }

    @Override @Test
    void upkeepTest() {
        // upkeep only does age, which is covered by ageAndKillTest()
    }

    /**
     * A recipient is dropped only when the speaker is inside and the recipient is outside; every other
     * inside/outside combination keeps the recipient.
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
