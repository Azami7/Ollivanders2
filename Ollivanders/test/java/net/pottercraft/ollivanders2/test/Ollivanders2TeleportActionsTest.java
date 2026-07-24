package net.pottercraft.ollivanders2.test;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2TeleportActions;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link Ollivanders2TeleportActions} queue semantics: adding, removing, and the defensive copy
 * returned by {@link Ollivanders2TeleportActions#getTeleportActions()}. Execution of the queued teleports is the
 * scheduler's job and is covered by the spell tests that queue teleports.
 *
 * <p>Each test uses its own {@link Ollivanders2TeleportActions} instance so the plugin's own queue is left alone
 * and parallel test methods cannot see each other's actions.</p>
 */
public class Ollivanders2TeleportActionsTest {
    static ServerMock mockServer;

    /**
     * The world teleport actions are created in.
     */
    static World testWorld;

    /**
     * The player being teleported.
     */
    static PlayerMock player;

    @BeforeAll
    static void globalSetUp() {
        Ollivanders2.testMode = true;

        mockServer = MockBukkit.mock();
        // load the plugin so the common helpers the queue logs through are initialized
        MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));

        testWorld = mockServer.addSimpleWorld("teleportActions");
        player = mockServer.addPlayer();
    }

    /**
     * An added action is visible in the pending list and round-trips the player, locations, and explosion flag it
     * was created with.
     */
    @Test
    void addTeleportActionTest() {
        Ollivanders2TeleportActions teleportActions = new Ollivanders2TeleportActions();
        Location from = new Location(testWorld, 0, 4, 0);
        Location to = new Location(testWorld, 100, 4, 100);

        teleportActions.addTeleportAction(player, from, to, true);

        List<Ollivanders2TeleportActions.O2TeleportAction> pending = teleportActions.getTeleportActions();
        assertEquals(1, pending.size(), "Adding an action should make it pending");

        Ollivanders2TeleportActions.O2TeleportAction action = pending.get(0);
        assertEquals(player, action.getPlayer(), "The action should keep the player it was created with");
        assertEquals(from, action.getFromLocation(), "The action should keep the from location it was created with");
        assertEquals(to, action.getToLocation(), "The action should keep the to location it was created with");
        assertTrue(action.isExplosionOnTeleport(), "The action should keep the explosion flag it was created with");

        assertTrue(to.getChunk().isLoaded(), "Adding an action should load the destination chunk");

        // -- an action created without the explosion effect reports the flag off --
        teleportActions.addTeleportAction(player, from, to, false);
        assertFalse(teleportActions.getTeleportActions().get(1).isExplosionOnTeleport(),
                "An action created without the explosion effect should report the flag off");
    }

    /**
     * The pending list is a defensive copy - changing it does not change the queue.
     */
    @Test
    void getTeleportActionsCopyTest() {
        Ollivanders2TeleportActions teleportActions = new Ollivanders2TeleportActions();
        Location from = new Location(testWorld, 0, 4, 200);
        Location to = new Location(testWorld, 100, 4, 200);

        teleportActions.addTeleportAction(player, from, to, false);

        teleportActions.getTeleportActions().clear();
        assertEquals(1, teleportActions.getTeleportActions().size(),
                "Changing the returned list should not change the queue");
    }

    /**
     * Removing a queued action removes only that action, and removing an action that is not queued does nothing.
     */
    @Test
    void removeTeleportActionTest() {
        Ollivanders2TeleportActions teleportActions = new Ollivanders2TeleportActions();
        Location from = new Location(testWorld, 0, 4, 400);
        Location to = new Location(testWorld, 100, 4, 400);

        teleportActions.addTeleportAction(player, from, to, false);
        teleportActions.addTeleportAction(player, from, to, false);

        List<Ollivanders2TeleportActions.O2TeleportAction> pending = teleportActions.getTeleportActions();
        Ollivanders2TeleportActions.O2TeleportAction removed = pending.get(0);

        teleportActions.removeTeleportAction(removed);
        assertEquals(1, teleportActions.getTeleportActions().size(), "Removing an action should leave the rest queued");
        assertFalse(teleportActions.getTeleportActions().contains(removed), "The removed action should be gone");

        // -- removing an action that is not queued does nothing --
        teleportActions.removeTeleportAction(removed);
        assertEquals(1, teleportActions.getTeleportActions().size(),
                "Removing an action that is not queued should change nothing");
    }

    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
