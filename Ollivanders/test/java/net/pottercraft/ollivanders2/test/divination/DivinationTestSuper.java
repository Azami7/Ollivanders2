package net.pottercraft.ollivanders2.test.divination;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.divination.O2Divination;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract public class DivinationTestSuper {
    static ServerMock mockServer;
    Ollivanders2 testPlugin;

    @BeforeAll
    static void globalSetUp() {
        mockServer = MockBukkit.mock();
    }

    @BeforeEach
    void setUp() {
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));

        // advance the server by 20 ticks to let the scheduler start (it has an initial delay of 20 ticks)
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    abstract O2Divination createDivination(@NotNull Player prophet, @NotNull Player target, int experience);

    @Test
    void divineTest() {
        // make sure we're starting with a clean list
        List<String> prophecies = Ollivanders2API.getProphecies().getProphecies();
        assertEquals(0, prophecies.size(), "Tests did not start out with empty prophecies list.");

        // create a prophet and a target
        PlayerMock prophet = mockServer.addPlayer();
        PlayerMock target = mockServer.addPlayer();

        // do the divination with high experience
        O2Divination divination = createDivination(prophet, target, 100);
        divination.divine();

        // advance the game ticks
        mockServer.getScheduler().performTicks(10);

        // verify a prophecy was added
        prophecies = Ollivanders2API.getProphecies().getProphecies();
        assertEquals(1, prophecies.size(), "Number of prophecies is not 1 after O2Divination.divine()");
        assertTrue(TestCommon.containsStringMatch(prophecies, target.getName()), "Target player name not found in any prophecy.");
    }

    @AfterEach
    void tearDown() {
        Ollivanders2API.getProphecies().resetProphecies();
    }

    /**
     * Global test teardown. Unmocks the MockBukkit server.
     */
    @AfterAll
    static void globalTearDown () {
        MockBukkit.unmock();
    }
}
