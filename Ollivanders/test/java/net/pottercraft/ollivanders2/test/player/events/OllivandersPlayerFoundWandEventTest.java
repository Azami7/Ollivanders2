package net.pottercraft.ollivanders2.test.player.events;

import net.pottercraft.ollivanders2.player.events.OllivandersPlayerFoundWandEvent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Unit tests for {@link OllivandersPlayerFoundWandEvent}.
 *
 * <p>The event firing on wand discovery is covered by the O2Player found-wand test; this covers the event's own
 * contract.</p>
 */
public class OllivandersPlayerFoundWandEventTest {
    static ServerMock mockServer;
    static PlayerMock player;

    @BeforeAll
    static void globalSetUp() {
        mockServer = MockBukkit.mock();
        player = mockServer.addPlayer();
    }

    /**
     * The event carries the player who found their wand and exposes its handler list.
     */
    @Test
    void testEvent() {
        OllivandersPlayerFoundWandEvent event = new OllivandersPlayerFoundWandEvent(player);

        assertSame(player, event.getPlayer(), "Event should carry the player who found their wand");
        assertNotNull(event.getHandlers(), "Event should expose its handlers");
        assertSame(OllivandersPlayerFoundWandEvent.getHandlerList(), event.getHandlers(),
                "Instance handlers should be the static handler list");
    }

    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
