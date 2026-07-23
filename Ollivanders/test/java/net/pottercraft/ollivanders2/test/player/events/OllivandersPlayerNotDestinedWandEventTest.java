package net.pottercraft.ollivanders2.test.player.events;

import net.pottercraft.ollivanders2.player.events.OllivandersPlayerNotDestinedWandEvent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Unit tests for {@link OllivandersPlayerNotDestinedWandEvent}.
 */
public class OllivandersPlayerNotDestinedWandEventTest {
    static ServerMock mockServer;
    static PlayerMock player;

    @BeforeAll
    static void globalSetUp() {
        mockServer = MockBukkit.mock();
        player = mockServer.addPlayer();
    }

    /**
     * The event carries the player who used a non-destined wand and exposes its handler list.
     */
    @Test
    void testEvent() {
        OllivandersPlayerNotDestinedWandEvent event = new OllivandersPlayerNotDestinedWandEvent(player);

        assertSame(player, event.getPlayer(), "Event should carry the player who used the wand");
        assertNotNull(event.getHandlers(), "Event should expose its handlers");
        assertSame(OllivandersPlayerNotDestinedWandEvent.getHandlerList(), event.getHandlers(),
                "Instance handlers should be the static handler list");
    }

    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
