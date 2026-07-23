package net.pottercraft.ollivanders2.test;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2OwlPost;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link Ollivanders2OwlPost}.
 *
 * <p>Each test uses its own {@link Ollivanders2OwlPost} instance and its own world so that the shared plugin owl post
 * queue is left alone and parallel test methods cannot see each other's deliveries.</p>
 */
public class Ollivanders2OwlPostTest {
    static Ollivanders2 testPlugin;
    static ServerMock mockServer;

    /**
     * The sender in the request parsing test.
     */
    static PlayerMock player1;

    /**
     * The sender in the delivery test.
     */
    static PlayerMock player2;

    /**
     * The recipient in the delivery test.
     */
    static PlayerMock player3;

    /**
     * The recipient in the deferred delivery test.
     */
    static PlayerMock player4;

    /**
     * The sender in the plugin disable test, who stays online.
     */
    static PlayerMock player5;

    /**
     * The sender in the plugin disable test who logs out before the plugin disables.
     */
    static PlayerMock player6;

    /**
     * The world for the request parsing test.
     */
    static World requestWorld;

    /**
     * The world for the delivery test.
     */
    static World deliveryWorld;

    /**
     * The world for the deferred delivery test.
     */
    static World deferredWorld;

    /**
     * The world for the plugin disable test.
     */
    static World disableWorld;

    /**
     * The height deliveries are made at, well above the world's terrain so the blocks around a player are empty air.
     */
    static final int deliveryTestHeight = 10;

    @BeforeAll
    static void globalSetUp() {
        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));

        // a world per test so parallel test methods cannot see each other's owls and packages
        requestWorld = mockServer.addSimpleWorld("owlPostRequest");
        deliveryWorld = mockServer.addSimpleWorld("owlPostDelivery");
        deferredWorld = mockServer.addSimpleWorld("owlPostDeferred");
        disableWorld = mockServer.addSimpleWorld("owlPostDisable");

        player1 = mockServer.addPlayer();
        player2 = mockServer.addPlayer();
        player3 = mockServer.addPlayer();
        player4 = mockServer.addPlayer();
        player5 = mockServer.addPlayer();
        player6 = mockServer.addPlayer();
    }

    /**
     * Spawn an owl next to a player so they have a courier to hand a package to.
     *
     * @param location the location to spawn the owl at
     * @return the owl
     */
    private Entity spawnOwl(Location location) {
        return location.getWorld().spawnEntity(location, Ollivanders2OwlPost.owlPostEntityType);
    }

    /**
     * A chat request only queues a delivery when it names a known player, the sender is near an owl, and they are
     * holding something to send. Every other case tells the sender what went wrong and leaves the queue empty.
     */
    @Test
    void processOwlPostRequestTest() {
        Ollivanders2OwlPost owlPost = new Ollivanders2OwlPost(testPlugin);
        Location senderLocation = new Location(requestWorld, 0, deliveryTestHeight, 0);
        player1.teleport(senderLocation);
        TestCommon.clearMessageQueue(player1);

        // a message that is not an owl post request is ignored entirely
        owlPost.processOwlPostRequest(player1, "hello there");
        assertNull(TestCommon.getWholeMessage(player1), "A non-delivery message should not be answered");
        assertEquals(0, owlPost.getPendingDeliveryCount(), "A non-delivery message should not queue a delivery");

        // a request that is not the keyword plus exactly one name cannot be parsed and is dropped silently
        owlPost.processOwlPostRequest(player1, Ollivanders2OwlPost.deliveryKeyword + " " + player1.getName() + " please");
        assertNull(TestCommon.getWholeMessage(player1), "A malformed request should not be answered");
        assertEquals(0, owlPost.getPendingDeliveryCount(), "A malformed request should not queue a delivery");

        // a recipient the plugin has never seen
        owlPost.processOwlPostRequest(player1, Ollivanders2OwlPost.deliveryKeyword + " NotAPlayer");
        String wholeMessage = TestCommon.getWholeMessage(player1);
        assertNotNull(wholeMessage, "An unknown recipient should be reported to the sender");
        assertTrue(wholeMessage.contains("not found"), "An unknown recipient should be reported to the sender: " + wholeMessage);
        assertEquals(0, owlPost.getPendingDeliveryCount(), "An unknown recipient should not queue a delivery");

        // there is no owl nearby to carry the package
        owlPost.processOwlPostRequest(player1, Ollivanders2OwlPost.deliveryKeyword + " " + player1.getName());
        wholeMessage = TestCommon.getWholeMessage(player1);
        assertNotNull(wholeMessage, "Having no owl nearby should be reported to the sender");
        assertTrue(wholeMessage.contains("No owl"), "Having no owl nearby should be reported to the sender: " + wholeMessage);
        assertEquals(0, owlPost.getPendingDeliveryCount(), "Having no owl nearby should not queue a delivery");

        // an owl is nearby now, but the sender is not holding anything to send
        spawnOwl(senderLocation);
        player1.getInventory().setItemInMainHand(null);
        owlPost.processOwlPostRequest(player1, Ollivanders2OwlPost.deliveryKeyword + " " + player1.getName());
        wholeMessage = TestCommon.getWholeMessage(player1);
        assertNotNull(wholeMessage, "Having nothing in hand should be reported to the sender");
        assertTrue(wholeMessage.contains("No item in your primary hand"), "Having nothing in hand should be reported to the sender: " + wholeMessage);
        assertEquals(0, owlPost.getPendingDeliveryCount(), "Having nothing in hand should not queue a delivery");

        // everything the request needs is in place
        player1.getInventory().setItemInMainHand(new ItemStack(Material.DIAMOND, 1));
        owlPost.processOwlPostRequest(player1, Ollivanders2OwlPost.deliveryKeyword + " " + player1.getName());
        wholeMessage = TestCommon.getWholeMessage(player1);
        assertNotNull(wholeMessage, "A successful request should be confirmed to the sender");
        assertTrue(wholeMessage.contains("scheduled"), "A successful request should be confirmed to the sender: " + wholeMessage);
        assertEquals(1, owlPost.getPendingDeliveryCount(), "A successful request should queue a delivery");
        assertEquals(Material.AIR, player1.getInventory().getItemInMainHand().getType(), "The package should be taken out of the sender's hand");

        // neither the keyword nor the recipient name is case sensitive
        player1.getInventory().setItemInMainHand(new ItemStack(Material.DIAMOND, 1));
        owlPost.processOwlPostRequest(player1, Ollivanders2OwlPost.deliveryKeyword.toUpperCase() + " " + player1.getName().toUpperCase());
        assertEquals(2, owlPost.getPendingDeliveryCount(), "The keyword and recipient name should not be case sensitive");
    }

    /**
     * A queued delivery is held for a moment, then the owl flies to the recipient, hands over the package, and returns
     * to where it was picked up.
     */
    @Test
    void deliveryTest() {
        final int handoffDelay = 2 * Ollivanders2Common.ticksPerSecond; // the owl's flight time to the recipient
        final int returnDelay = 5 * Ollivanders2Common.ticksPerSecond; // the owl's flight time home

        Ollivanders2OwlPost owlPost = new Ollivanders2OwlPost(testPlugin);
        Location senderLocation = new Location(deliveryWorld, 0, deliveryTestHeight, 0);
        Location recipientLocation = new Location(deliveryWorld, 20, deliveryTestHeight, 20);

        player2.teleport(senderLocation);
        player3.teleport(recipientLocation);
        TestCommon.clearMessageQueue(player3);
        player3.getInventory().clear();

        Entity owl = spawnOwl(senderLocation);
        ItemStack delivery = new ItemStack(Material.DIAMOND, 3);
        owlPost.addDelivery(player2, player3.getUniqueId(), owl, delivery);

        // nothing happens while the owl is getting ready to fly
        owlPost.upkeep();
        assertEquals(1, owlPost.getPendingDeliveryCount(), "The delivery should still be queued during its initial cooldown");
        assertFalse(TestCommon.isInPlayerInventory(player3, Material.DIAMOND), "The package should not arrive during the initial cooldown");

        // the owl sets off once the cooldown expires
        for (int i = 0; i < Ollivanders2Common.ticksPerSecond; i++) {
            owlPost.upkeep();
        }
        assertEquals(0, owlPost.getPendingDeliveryCount(), "The delivery should leave the queue once it is underway");

        mockServer.getScheduler().performTicks(handoffDelay);
        ItemStack received = TestCommon.getPlayerInventoryItem(player3, Material.DIAMOND);
        assertNotNull(received, "The recipient should receive the package");
        assertEquals(delivery.getAmount(), received.getAmount(), "The recipient should receive the whole package");

        String wholeMessage = TestCommon.getWholeMessage(player3);
        assertNotNull(wholeMessage, "The recipient should be told a delivery arrived");
        assertTrue(wholeMessage.contains(player2.getName()), "The recipient should be told who sent the delivery: " + wholeMessage);

        // the owl goes home after handing over the package
        mockServer.getScheduler().performTicks(returnDelay);
        assertEquals(senderLocation.getBlockX(), owl.getLocation().getBlockX(), "The owl should return to where it was picked up");
        assertEquals(senderLocation.getBlockZ(), owl.getLocation().getBlockZ(), "The owl should return to where it was picked up");
    }

    /**
     * Deliveries the owl cannot make stay queued: the recipient may be offline, or somewhere the owl cannot fly to.
     */
    @Test
    void deferredDeliveryTest() {
        Ollivanders2OwlPost owlPost = new Ollivanders2OwlPost(testPlugin);
        Location senderLocation = new Location(deferredWorld, 0, deliveryTestHeight, 0);
        Location recipientLocation = new Location(deferredWorld, 20, deliveryTestHeight, 20);

        player4.teleport(recipientLocation);
        player4.getInventory().clear();

        // an offline recipient cannot be delivered to, so the delivery waits for them
        Entity offlineCourier = spawnOwl(senderLocation);
        owlPost.addDelivery(player4, UUID.randomUUID(), offlineCourier, new ItemStack(Material.DIAMOND, 1));

        for (int i = 0; i <= Ollivanders2Common.ticksPerSecond; i++) {
            owlPost.upkeep();
        }
        assertEquals(1, owlPost.getPendingDeliveryCount(), "A delivery for an offline recipient should stay queued");

        // the recipient is online but has no open air above them for the owl to fly in to
        Location airSpaceAboveRecipient = recipientLocation.clone().add(0, 2, 0);
        airSpaceAboveRecipient.getBlock().setType(Material.STONE);

        Entity blockedCourier = spawnOwl(senderLocation);
        owlPost.addDelivery(player4, player4.getUniqueId(), blockedCourier, new ItemStack(Material.EMERALD, 1));

        for (int i = 0; i <= Ollivanders2Common.ticksPerSecond; i++) {
            owlPost.upkeep();
        }
        assertEquals(2, owlPost.getPendingDeliveryCount(), "A delivery to an unreachable recipient should stay queued");
        assertFalse(TestCommon.isInPlayerInventory(player4, Material.EMERALD), "An unreachable recipient should not receive the package");

        airSpaceAboveRecipient.getBlock().setType(Material.AIR);
    }

    /**
     * Pending deliveries are not persisted, so disabling the plugin returns every package still in the queue to the
     * player who sent it rather than destroying it.
     */
    @Test
    void onDisableTest() {
        Ollivanders2OwlPost owlPost = new Ollivanders2OwlPost(testPlugin);
        Location onlineSenderLocation = new Location(disableWorld, 0, deliveryTestHeight, 0);
        Location offlineSenderLocation = new Location(disableWorld, 40, deliveryTestHeight, 40);

        player5.teleport(onlineSenderLocation);
        player5.getInventory().clear();
        TestCommon.clearMessageQueue(player5);
        player6.teleport(offlineSenderLocation);

        // both deliveries are for a player who will never be online to receive them
        UUID absentRecipient = UUID.randomUUID();
        owlPost.addDelivery(player5, absentRecipient, spawnOwl(onlineSenderLocation), new ItemStack(Material.DIAMOND, 2));
        owlPost.addDelivery(player6, absentRecipient, spawnOwl(offlineSenderLocation), new ItemStack(Material.EMERALD, 2));

        player6.disconnect();
        owlPost.onDisable();

        assertEquals(0, owlPost.getPendingDeliveryCount(), "Disabling the plugin should empty the delivery queue");

        // an online sender gets their package handed back
        ItemStack returned = TestCommon.getPlayerInventoryItem(player5, Material.DIAMOND);
        assertNotNull(returned, "An online sender should get their package back");
        assertEquals(2, returned.getAmount(), "An online sender should get their whole package back");
        String wholeMessage = TestCommon.getWholeMessage(player5);
        assertNotNull(wholeMessage, "An online sender should be told their delivery was returned");
        assertTrue(wholeMessage.contains("returned to you"), "An online sender should be told their delivery was returned: " + wholeMessage);

        // an offline sender's package is dropped by their owl so it is not destroyed
        List<Item> droppedItems = TestCommon.getAllItems(disableWorld);
        boolean packageDropped = false;
        for (Item item : droppedItems) {
            if (item.getItemStack().getType() == Material.EMERALD)
                packageDropped = true;
        }
        assertTrue(packageDropped, "An offline sender's package should be dropped rather than destroyed");
    }

    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
